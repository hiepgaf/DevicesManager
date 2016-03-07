package com.hieptran.devicesmanager.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hieptran.devicesmanager.utils.Const;

/**
 * Created by hieptran on 02/02/2016.
 */
public class ProfileService extends Service implements Const {
    BroadcastReceiver profileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_PROFILE_BATTERY_LEVEL:
                    Log.d("HiepTHb", "battery lv profile is running");
                    showTest();
                    break;
                case ACTION_PROFILE_POWER_PLUG:
                    Log.d("HiepTHb", "power profile is running");
                    showTest();

                    break;
                case ACTION_PROFILE_SCREEN_STATE:
                    Log.d("HiepTHb", "screen state profile is running");
                    showTest();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("HiepTHb", " Service is running");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PROFILE_BATTERY_LEVEL);
        filter.addAction(ACTION_PROFILE_POWER_PLUG);

        filter.addAction(ACTION_PROFILE_SCREEN_STATE);

        registerReceiver(profileReceiver, filter);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {

        Log.d("HiepTHb", "Service is killed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showTest() {
        Log.d("HiepTHb","Test running services : ");

        for(int i=0;i <100;i++) {
            try {

               // Thread.sleep(3000);
                Log.d("HiepTHb", i + "\n");

            } catch (Exception ex) {}
        }
    }
}
