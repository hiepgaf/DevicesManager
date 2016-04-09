package com.hieptran.devicesmanager.utils.power;

import android.content.Context;
import android.provider.Settings;

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
            return false;
        }
    }

}
