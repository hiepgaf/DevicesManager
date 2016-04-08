package com.hieptran.devicesmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.hieptran.devicesmanager.common.Wakelock;
import com.hieptran.devicesmanager.utils.Utils;

/**
 * Created by hiepth on 08/04/2016.
 */
public class ScreenStateReceiver extends BroadcastReceiver{
    PowerManager.WakeLock wakeLock;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            wakeLock.release();
           // Wakelock.releaseWakelock();
            Utils.saveBoolean("screen_on", true, context);
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DeviceManager Record Data");
            wakeLock.acquire();

            // Wakelock.aquireWakelock(context);
            Utils.saveBoolean("screen_on", false, context);
        }

        Log.d("HiepTHb", Utils.getBoolean("screen_on", false, context) + " ---");
    }


}
