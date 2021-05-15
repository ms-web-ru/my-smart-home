package com.example.mysmarthome3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;

import io.socket.engineio.client.transports.WebSocket;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    public static boolean[] btnStates = new boolean[7];
    private Socket mSocket;
    private MainActivity mContext;
    public static SharedPreferences preferences;


    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

//                        // add the message to view
//                        addMessage(username, message);
                }
            });
        }
    };
    private Intent intent;



    interface Callback{
        void callingBack();
    }
    Callback callback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        {
            try {
                IO.Options opts = new IO.Options();
                opts.transports = new String[] { WebSocket.NAME };
                String url = preferences.getString("messagerUrl", "");
                mSocket = IO.socket(url, opts);
                mSocket.connect();
                mSocket.on("message", onNewMessage);
                mSocket.emit("attach_event", "asdwefw");

            } catch (URISyntaxException e) {
            }
        }

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

    /**
     * Emitting events to socket
     * @param message
     */
    private void attemptSend(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSocket.emit("new message", message);
    }

    public void settingActivity(View v) {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }

}