package com.hieptran.devicesmanager.common;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by hiepth on 08/04/2016.
 */
//Todo: Thuc hien wakelock (acquire va release) voi state cua screen
public class Wakelock {
    private static PowerManager.WakeLock m_saveWakelock;
    static final String WAKELOCK = "HiepTHb_Testwake_service";
    static final String TAG = "Wakelock";
    static final long TIMEOUT = 120 * 1000; // we should not hold a wakelock for longer that 30s

    public static synchronized void aquireWakelock(Context ctx)
    {
        PowerManager powerManager = (PowerManager) ctx.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        releaseWakelock();
        m_saveWakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK);

        m_saveWakelock.acquire(TIMEOUT);
        Log.d(TAG, "Wakelock " + WAKELOCK + " aquired");
    }

    public static synchronized void releaseWakelock()
    {
        try
        {
            if ((m_saveWakelock != null) && (m_saveWakelock.isHeld()))
            {

                m_saveWakelock.release();
                Log.d(TAG, "Wakelock " + WAKELOCK + " released");

            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "An error occured releasing wakelock:" + e.getMessage());
        }
    }
}
