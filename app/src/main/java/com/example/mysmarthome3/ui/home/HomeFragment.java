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
		hallway.setImageDrawable(btnStates[0] ? img_on : img_off);
		btnToilet.setImageDrawable(btnStates[1] ? img_on : img_off);
		btnBedroom.setImageDrawable(btnStates[2] ? img_on : img_off);
		btnLivingroom.setImageDrawable(btnStates[3] ? img_on : img_off);
		btnLivingroomSpots.setImageDrawable(btnStates[4] ? img_on : img_off);
		btnKitchen.setImageDrawable(btnStates[5] ? img_on : img_off);
		btnKitchenSpots.setImageDrawable(btnStates[6] ? img_on : img_off);
	}

	/**
	 * устанавливает обработчики кликов
	 *
	 * @param root
	 */
	void setBtnsListeners(View root) {
		hallway = (ImageButton) root.findViewById(R.id.btnHallway);
		hallway.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(hallway, 0);
			}
		});
		hallway.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onBtnTouch(v, event);
				return false;
			}
		});


		btnToilet = (ImageButton) root.findViewById(R.id.btnToilet);
		btnToilet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(btnToilet, 1);
			}
		});

		btnBedroom = (ImageButton) root.findViewById(R.id.btnBedroom);
		btnBedroom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(btnBedroom, 2);
			}
		});

		btnLivingroom = (ImageButton) root.findViewById(R.id.btnLivingroom);
		btnLivingroom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(btnLivingroom, 3);
			}
		});

		btnLivingroomSpots = (ImageButton) root.findViewById(R.id.btnLivingroomSpots);
		btnLivingroomSpots.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(btnLivingroomSpots, 4);
			}
		});

		btnKitchen = (ImageButton) root.findViewById(R.id.btnKitchen);
		btnKitchen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(btnKitchen, 5);
			}
		});

		btnKitchenSpots = (ImageButton) root.findViewById(R.id.btnKitchenSpots);
		btnKitchenSpots.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnClick(btnKitchenSpots, 6);
			}
		});


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

	void onBtnTouch(View v, MotionEvent event) {
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