/*
 * Copyright (c) 2021. Boost Software License 1.0
 * @author Mikhail Sayapin dev@ms-web.ru
 */

package com.example.mysmarthome3.ui.home;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mysmarthome3.MainActivity;
import com.example.mysmarthome3.R;

import static com.example.mysmarthome3.MainActivity.btnStates;

public class HomeFragment extends Fragment {

	private HomeViewModel homeViewModel;
	private Drawable img_off;
	private Drawable img_on;

	private ImageButton hallway;
	private ImageButton btnToilet;
	private ImageButton btnBedroom;
	private ImageButton btnLivingroom;
	private ImageButton btnLivingroomSpots;
	private ImageButton btnKitchen;
	private ImageButton btnKitchenSpots;
	private static ImageButton[] imageButtons = new ImageButton[7];

	private SeekBar lightPower;

	private TextView messageText;

	private long touchStartTime;
	private boolean isSeekBarCalled = false;
	private boolean isSeekBarStopped = true;

	@SuppressLint("WrongViewCast")
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		homeViewModel =
			ViewModelProviders.of(this).get(HomeViewModel.class);
		View root = inflater.inflate(R.layout.fragment_home, container, false);

		img_off = ResourcesCompat.getDrawable(getResources(), R.mipmap.lamp_foreground, null);
		img_on = ResourcesCompat.getDrawable(getResources(), R.mipmap.lamp_on_foreground, null);

		lightPower = (SeekBar) root.findViewById(R.id.lightPower);
		messageText = (TextView) root.findViewById(R.id.messageText);

		setBtnsListeners(root);
		setLightPowerListener();

		ViewTreeObserver viewTreeObserver = root.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				setBtnsStates();
			}
		});


		return root;
	}

	void setLightPowerListener() {
		lightPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				messageText.setText((progress * 10) + "%");
				messageText.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// если отпустили и в течение 2х секунд начали двигать этот флаг не даст убрать ползунок
				isSeekBarStopped = false;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// ставим флаг что отпустили палец
				isSeekBarStopped = true;
				hideLightPowerBarTimeout();
			}
		});
	}

	/**
	 * при отпускании пальца, убирает ползунок регулятора если его повторно не трогали в течение 2х секунд
	 */
	void hideLightPowerBarTimeout() {
		if (lightPower.getVisibility() == View.INVISIBLE) {
			return;
		}
		new android.os.Handler(Looper.getMainLooper()).postDelayed(
			new Runnable() {
				public void run() {
					if (isSeekBarStopped) {
						lightPower.setVisibility(View.INVISIBLE);
						messageText.setVisibility(View.INVISIBLE);
					}
					else {
						hideLightPowerBarTimeout();
					}
				}
			},
			2000);
	}

	/**
	 * Устанавливает активное состояние включенным кнопкам
	 */
	void setBtnsStates() {
		for (int i = 0, ln = imageButtons.length; i < ln; i++) {
			imageButtons[i].setImageDrawable(btnStates[i] ? img_on : img_off);
		}
	}

	/**
	 * устанавливает обработчики кликов
	 *
	 * @param root
	 */
	@SuppressLint("ClickableViewAccessibility")
	void setBtnsListeners(View root) {
		hallway = (ImageButton) root.findViewById(R.id.btnHallway);
		btnToilet = (ImageButton) root.findViewById(R.id.btnToilet);
		btnBedroom = (ImageButton) root.findViewById(R.id.btnBedroom);
		btnLivingroom = (ImageButton) root.findViewById(R.id.btnLivingroom);
		btnLivingroomSpots = (ImageButton) root.findViewById(R.id.btnLivingroomSpots);
		btnKitchen = (ImageButton) root.findViewById(R.id.btnKitchen);
		btnKitchenSpots = (ImageButton) root.findViewById(R.id.btnKitchenSpots);

		imageButtons[0] = hallway;
		imageButtons[1] = btnToilet;
		imageButtons[2] = btnBedroom;
		imageButtons[3] = btnLivingroom;
		imageButtons[4] = btnLivingroomSpots;
		imageButtons[5] = btnKitchen;
		imageButtons[6] = btnKitchenSpots;

		for (int i = 0, ln = imageButtons.length; i < ln; i++) {
			final int fI = i;
			imageButtons[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBtnClick(imageButtons[fI], fI);
				}
			});

			imageButtons[i].setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					onBtnTouch(v, event, fI);
					return false;
				}
			});
		}
	}

	/**
	 * нажатие на кнопку лампочки
	 *
	 * @param btn
	 * @param btnIdx
	 */
	void onBtnClick(ImageButton btn, int btnIdx) {
		if (isSeekBarCalled) {
			isSeekBarCalled = false;
			return;
		}
		boolean state = getBtnState(btnIdx);
		if (!state) {
			btn.setImageDrawable(img_on);
			setBtnState(btnIdx, true);
		}
		else {
			btn.setImageDrawable(img_off);
			setBtnState(btnIdx, false);
		}
	}

	void setBtnState(int btnIdx, boolean state) {
		btnStates[btnIdx] = state;
	}

	boolean getBtnState(int btnIdx) {
		return btnStates[btnIdx];
	}

	void onBtnTouch(View v, MotionEvent event, int btnIdx) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: // нажатие
				touchStartTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_MOVE: // движение
				break;
			case MotionEvent.ACTION_UP: // отпускание
			case MotionEvent.ACTION_CANCEL:
				long totalTime = System.currentTimeMillis() - touchStartTime;
				long totalSecunds = totalTime / 1000;
				if (totalSecunds >= 1) {
					//ВОТ тут прошло 3 или больше секунды с начала нажатия
					//можно что-то запустить
					showLightPowerBar();
					isSeekBarCalled = true;
				}
				break;
		}
	}

	private void showLightPowerBar() {
		lightPower.setVisibility(View.VISIBLE);
	}

	private void hideLightPowerBar() {
		lightPower.setVisibility(View.INVISIBLE);
	}

}