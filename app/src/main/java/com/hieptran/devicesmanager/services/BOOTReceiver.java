package com.hieptran.devicesmanager.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

/**
 * Created by hieptran on 09/03/2016.
 */
public class BOOTReceiver extends BroadcastReceiver implements Const {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DumpLogService.class);
        if(Utils.getBoolean("set_on_boot",false,context));
        context.startService(i);

    }


}
