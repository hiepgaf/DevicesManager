package com.hieptran.devicesmanager.utils.power;

import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Handler;

/**
 * Created by hiepth on 09/04/2016.
 */
//Todo:
public class PowerMode {
    public static boolean DisablePowerSave(Context context) {
        try {
            return Settings.Global.putInt(context.getContentResolver(),"low_power",0);
        } catch (Exception ex) {
            return false;
        }
    }
    public static boolean EnablePowerSave(Context context) {
        try {
            return Settings.Global.putInt(context.getContentResolver(),"low_power",1);
        } catch (Exception ex) {
            Log.d("HiepTHb",ex.toString());
            return false;
        }
    }
    public static void setPowerSave(Context context) {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "android.os.PowerManager";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class, PowerManager.class, Handler.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            boolean batteryCapacity = (Boolean) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("setPowerSaveMode", java.lang.Boolean.class)
                    .invoke(mPowerProfile_, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
