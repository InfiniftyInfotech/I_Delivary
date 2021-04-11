package com.example.i_delivery.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.i_delivery.R;
import com.example.i_delivery.utils.PrefClient;
import com.example.i_delivery.utils.Utils;

import org.sumon.eagleeye.EagleEyeObserver;
import org.sumon.eagleeye.OnChangeConnectivityListener;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

    /*    EagleEyeObserver.setConnectivityListener(new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d(TAG, "onChanged: " + status);
                if (status) {
                    Utils.hideNoInternetDialog(LauncherActivity.this);

                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "isLogin: " + new PrefClient(LauncherActivity.this).checkLogin());
                            // TODO: 12/8/2020 implement login logic
                            if (!new PrefClient(LauncherActivity.this).checkLogin()){
                                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                            } else {
                                startActivity(new Intent(LauncherActivity.this, DashboardActivity.class));
                            }
                            // startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, 2000);

                } else {
                    Utils.showNoInternetDialog(LauncherActivity.this);
                }
            }
        });*/

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            Utils.hideNoInternetDialog(LauncherActivity.this);

            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "isLogin: " + new PrefClient(LauncherActivity.this).checkLogin());
                    // TODO: 12/8/2020 implement login logic
                    if (!new PrefClient(LauncherActivity.this).checkLogin()){
                        startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(LauncherActivity.this, DashboardActivity.class));
                    }
                    // startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                    finish();
                }
            }, 2000);
        } else {
            Utils.showNoInternetDialog(LauncherActivity.this);
        }
    }
}