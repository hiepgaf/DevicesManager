package com.hieptran.devicesmanager.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.root.RootFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by hieptran on 09/01/2016.
 */
public class Utils implements Const {
    //Show log
    public static void showLog(String Tag,String msg) {
        Log.d(Const.TAG+Tag,msg);
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


}