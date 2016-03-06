package com.hieptran.devicesmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.root.RootUtils;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by hieptran on 09/01/2016.
 */
public class Utils implements Const {
    //Provide changing theme
    public static boolean DARK = false;
    //Show log
    public static void showLog(String msg) {
        Log.d(Const.TAG, msg);
    }
    public static void confirmDialog(String title, String message, DialogInterface.OnClickListener onClickListener,
                                     Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        builder.setNegativeButton(context.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(context.getString(R.string.dialog_ok), onClickListener).show();
    }
    public static String getString(String name, String defaults, Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(name, defaults);
    }

    public static void saveString(String name, String value, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(name, value).apply();
    }
    public static void saveInt(String name, int value, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(name, value).apply();
    }
    public static int getInt(String name, int defaults, Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(name, defaults);
    }
    public static void saveBoolean(String name, boolean value, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(name, value).apply();
    }
    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(name, defaults);
    }
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int getScreenOrientation(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels <
                context.getResources().getDisplayMetrics().heightPixels ?
                Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean existFile(String file) {
        return Tools.existFile(file, true);
    }

    public static void writeFile(String path, String text, boolean append) {
        Tools.writeFile(path, text, append, true);
    }

    public static String readFile(String file) {
        return Tools.readFile(file, true);
    }

    public static int stringToInt(String string) {
        try {
            return Math.round(Float.parseFloat(string));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long stringToLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static View genarateView(Activity mActivity, int id, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(mActivity);
        return inf.inflate(id, parent, false);
    }

    public static boolean isPropActive(String key) {
        try {
            return RootUtils.runCommand("getprop | grep " + key).split("]:")[1].contains("running");
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean hasProp(String key) {
        try {
            return RootUtils.runCommand("getprop | grep " + key).split("]:").length > 1;
        } catch (Exception ignored) {
            return false;
        }
    }
    public static void writeToSdcard(String name,String content) {
        File wearableOsDir = new File("/sdcard/WearableOS/");
        wearableOsDir.mkdirs();
        File output = new File(wearableOsDir,name);
        FileWriter writer= null;
        try {
            writer = new FileWriter(output,true);
            writer.write(content);
            writer.flush();
        } catch (Exception ex) {
            Log.e("HiepTHb", ex.getMessage());
        }
        finally {
            try {
                if(writer != null ) writer.close();
            }
            catch (Exception e) {

            }
        }
    }
    public static void sendIntent(Context ctx, String action) {
        Intent i = new Intent();
        i.setAction(action);
        ctx.sendBroadcast(i);
    }
    public static String formatSeconds(int seconds) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(new Date(seconds*1000));
        return time;
    }
}
