package com.hieptran.devicesmanager.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hieptran.devicesmanager.utils.Const;

/**
 * Created by hieptran on 09/03/2016.
 */
public class BOOTReceiver extends BroadcastReceiver implements Const {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DumpLogService.class);
        context.startService(i);

    }


}
