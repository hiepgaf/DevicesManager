package com.hieptran.devicesmanager.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.hieptran.devicesmanager.R;


/**
 * Created by hieptran on 09/01/2016.
 */
public class Utils implements Const {
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


}
