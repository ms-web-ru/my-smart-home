package com.example.mysmarthome3;

import android.os.Bundle;
import android.os.Looper;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static boolean[] btnStates = new boolean[7];


    interface Callback{
        void callingBack();
    }
    Callback callback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnStates[0] = false;
        btnStates[1] = false;
        btnStates[2] = false;
        btnStates[3] = false;
        btnStates[4] = false;
        btnStates[5] = false;
        btnStates[6] = false;

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void registerCallBack(Callback callback){
        this.callback = callback;
    }


    public void setTimeout (final Callback callback, int timeout) {
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        callback.callingBack();
                    }
                },
                timeout);

    }

}