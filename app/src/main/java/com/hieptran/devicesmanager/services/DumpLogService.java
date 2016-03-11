package com.hieptran.devicesmanager.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.info.LogRecord;
import com.hieptran.devicesmanager.utils.provider.DbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hieptran on 02/02/2016.
 */
public class DumpLogService extends Service implements Const {
    double vol_sum, cur_sum;
    Context con;
    NotificationManager mNotificationManager;
    int count_time;
    Notification notification;
    DbHelper mydb;
    LogRecord mLog;// = new LogRecord();
    Thread t;
    private String table_name;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //  if (hand != null)  hand.postDelayed(updateView, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        con = getApplicationContext();
        mNotificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("HiepTHb", "onBoot - onStartCommnad");
        cur_sum = vol_sum = 0;
        mydb = new DbHelper(getApplicationContext());
        table_name = "log" + new SimpleDateFormat("ddMMyyHHmmss").format(new Date(System.currentTimeMillis()));
        mydb.doCreateTable(table_name);
        Runnable runable = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        count_time++;
                        vol_sum += Double.valueOf(readVoltageNow());
                        cur_sum += Double.valueOf(readCurrentNow());
                        String avg_power = String.format("%.2f", vol_sum * cur_sum / count_time / count_time / 1000 / 1000000);
                        String battery_consumed = String.format("%.2f", Math.abs(cur_sum / 60 / 60 / 1000));
                        String avg_voltage = String.format("%.2f", vol_sum / count_time);
                        String avg_current = String.format("%.2f", cur_sum / count_time);
                        String percent = String.format("%.1f", 100 * Math.abs(cur_sum / 60 / 60 / 1000) / 2300);
                        String top_tile = "Time: " + Utils.formatSeconds(count_time) + " \n" + "Average Power: " + String.format("%.2f", vol_sum * cur_sum / count_time / count_time / 1000 / 1000000) + " mW\n" +
                                "Battery Consumed: " + String.format("%.2f", (cur_sum / 60 / 60 / 1000)) + " mAh" + "~" + String.format("%.1f", 100 * Math.abs(cur_sum / 60 / 60 / 1000) / 2300) + "%\n" +
                                "Average Voltage: " + String.format("%.2f", vol_sum / count_time) + " uV\n" +
                                "Average Current: " + String.format("%.2f", cur_sum / count_time) + " uA\n";

                        Utils.saveInt("time_record", count_time, getApplicationContext());
                        Utils.saveString("average_power", String.format("%.2f", vol_sum * cur_sum / count_time / count_time / 1000 / 1000000), getApplicationContext());
                        Utils.saveString("battery_consumed", String.format("%.2f", (cur_sum / 60 / 60 / 1000)), getApplicationContext());
                        Utils.saveString("average_voltage", "" + String.format("%.2f", vol_sum / count_time), getApplicationContext());
                        Utils.saveString("average_current", String.format("%.2f", cur_sum / count_time), getApplicationContext());
                        Utils.saveString("percent", percent, getApplicationContext());
                        mydb.doPutRecord(table_name, count_time, avg_power, battery_consumed, avg_voltage, avg_current);
                        notification = new NotificationCompat.Builder(con)
                                .setSmallIcon(R.drawable.about_icon)
                                .setContentTitle("Device Manager")
                                .setContentText(top_tile)
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(top_tile))
                                .build();
                        mNotificationManager.notify(309, notification);
                        Log.d("HiepTHb", "Test" + count_time);
                    }
                } catch (Exception ex) {
                    Log.d("HiepTHb", "--" + ex.getMessage());
                }
            }
        };
        t = new Thread(runable);
        t.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(309);
        t.interrupt();
        Utils.saveInt("time_record", 0, getApplicationContext());
        Utils.saveString("average_power", "", getApplicationContext());
        Utils.saveString("battery_consumed", "", getApplicationContext());
        Utils.saveString("average_voltage", "", getApplicationContext());
        Utils.saveString("average_current", "", getApplicationContext());
        Utils.saveString("percent", "", getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private String readCurrentNow() {
        return Utils.readFile(BATTERY_CURRENT_NOW);
    }

    private String readVoltageNow() {
        return Utils.readFile(BATTERY_VOLTAGE_NOW);
    }

}
