package com.hieptran.devicesmanager.fragment.tweak.battery;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.hieptran.devicesmanager.LogViewActivity;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.ViewLogActivity;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.drakeet.materialdialog.MaterialDialog;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by hieptran on 02/03/2016.
 */
public class BatteryInfoFragment extends Fragment implements Const, View.OnClickListener, AdapterView.OnItemClickListener {
    private WaveLoadingView waveLevelView, realLv;
    private TextView lvText, mTemperatureText;
    private TextView voltageText;
    private TextView totalPw;
    private ListView lvRecordFile;
    public static int level, voltage;
    double totalPower, currentPower;
    double realLevel;
    private TextView current_now_tv, voltage_now_tv;
    private android.os.Handler hand;
    int time;
    private Button btRecord;
    public String result;
    private boolean isRecorded = false;
    public static final int RECORD_NOTIFICATION_ID = 1;
    public NotificationManager mNotificationManager;
    public LogVolAmp mLogRecord;
    public String mEndTimeRecord = "", mStartTimeRecord = "";
    private ArrayList<String> voltage_now_al, current_now_al,record_files_al;
    private ArrayAdapter<String> voltage_now_ad, current_now_ad,record_files_ad;


    @Override
    public void onClick(View v) {
        if (v == btRecord) {
            if (!isRecorded) {
                updateRecordFileView();
                isRecorded = true;
                btRecord.setBackgroundResource(R.drawable.bg_off);
                btRecord.setText(getString(R.string.start_record));
                mStartTimeRecord = new SimpleDateFormat("HHmmss").format(new Date(System.currentTimeMillis()));
                mLogRecord.setStartTime(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
            } else {
                updateRecordFileView();
                time = 0;
                isRecorded = false;
                mNotificationManager.cancel(RECORD_NOTIFICATION_ID);
                btRecord.setBackgroundResource(R.drawable.bg_on);
                btRecord.setText(result);
                mLogRecord.setEndTime(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                Utils.writeToSdcard(mStartTimeRecord + "_log.txt", mLogRecord.toString());
                Log.d(TAG, "Result :\n" + mLogRecord.toString() + "\n currt" + System.currentTimeMillis());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.battery_info_fragment, container, false);
        waveLevelView = (WaveLoadingView) v.findViewById(R.id.waveLoadingView);
        totalPw = (TextView) v.findViewById(R.id.totalpower);
        realLv = (WaveLoadingView) v.findViewById(R.id.reallv);
        current_now_tv = (TextView) v.findViewById(R.id.current_now_tv);
        voltage_now_tv = (TextView) v.findViewById(R.id.voltage_now_tv);
        btRecord = (Button) v.findViewById(R.id.bt_start_record);
        btRecord.setOnClickListener(this);
        totalPower = 3.7 * 2300;
        totalPw.setText(String.valueOf(totalPower) + " mWh");
        btRecord.setBackgroundResource(R.drawable.bg_on);
        lvRecordFile = (ListView) v.findViewById(R.id.lv_record_file);
        lvRecordFile.setOnItemClickListener(this);
        record_files_al = new ArrayList<>();
        voltage_now_al = new ArrayList<>();
        current_now_al = new ArrayList<>();
        voltage_now_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, voltage_now_al);
        current_now_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, current_now_al);
        record_files_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, record_files_al);
        lvRecordFile.setAdapter(record_files_ad);
        updateRecordFileView();

        mLogRecord = new LogVolAmp(mStartTimeRecord, mEndTimeRecord, voltage_now_al, current_now_al,listRunning());
        voltageText = (TextView) v.findViewById(R.id.bat_vol);
        try {
            getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (NullPointerException ignored) {
        }
        if (Utils.DARK) {
            waveLevelView.setWaveColor(R.color.color_primary_dark);
            realLv.setWaveColor(R.color.color_primary_dark);
        }
        hand = new android.os.Handler();
        return v;
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            currentPower = Integer.valueOf(readVoltageNow()) * level * 2.3 / 1000;
            realLevel = (currentPower / totalPower);
            realLv.setProgressValue(100 - (int) (realLevel));
            realLv.setCenterTitle(String.format("%.2f", currentPower / 100) + " mWh\n");
            realLv.setTopTitle(String.format("%.2f", realLevel) + " %");
            if (!isCharging)
                waveLevelView.setAmplitudeRatio(1);
            else waveLevelView.setAmplitudeRatio(50);
            waveLevelView.setProgressValue(100 - level);

            //  waveLevelView.setWaveColor(Color.GREEN);
            waveLevelView.setCenterTitle(level + " %");
            voltageText.setText("Battery Voltage " + voltage + "    mV");

        }
    };

    private String readCurrentNow() {
        return Utils.readFile(BATTERY_CURRENT_NOW);
    }

    private String readVoltageNow() {
        return Utils.readFile(BATTERY_VOLTAGE_NOW);
    }

    private Runnable updateView = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Object mPowerProfile_ = null;
                            final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
                            try {
                                mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                                        .getConstructor(Context.class).newInstance(getContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                // batteryCapacity = (Double)Class.forName(POWER_PROFILE_CLASS).getMethod("getAveragePower", java.lang.String.class).invoke(mPowerProfile_, "battery.capacity");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            current_now_tv.setText("Current now: " + readCurrentNow() + " uA");
                            voltage_now_tv.setText("Voltage now: " + readVoltageNow() + " uV");
                            // voltage_now_ad.add("\t\t\t\t\t" + readVoltageNow() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + readCurrentNow());
                            // voltage_now_ad.setNotifyOnChange(true);
                            //  lsRunningApp.add(listRunning());
                            if (!isRecorded) {
                                btRecord.setText(getString(R.string.start_record));
                            } else {
                               result = Utils.formatSeconds(time++);
                                btRecord.setText(result);
                                showNotification(result);
                                voltage_now_ad.add(readVoltageNow());
                                voltage_now_ad.setNotifyOnChange(true);
                                current_now_ad.add(readCurrentNow());
                                current_now_ad.setNotifyOnChange(true);
                            }

                        }
                    });
                }
            }).start();
            hand.postDelayed(updateView, 1000);
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        if (hand != null) hand.post(updateView);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hand != null) hand.removeCallbacks(updateView);
        //getActivity().unregisterReceiver(mBatInfoReceiver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBatInfoReceiver);
    }

    //
    public class LogVolAmp {
        private String startTime, endTime;
        private ArrayList<String> lsVolNow, lsAmpNow;
        private String lsAppRunning;
        public LogVolAmp(String startTime, String endTime, ArrayList<String> lsVolNow, ArrayList<String> lsAmpNow, String lsAppRunning) {
            this.endTime = endTime;
            this.lsAmpNow = lsAmpNow;
            this.lsVolNow = lsVolNow;
            this.startTime = startTime;
            this.lsAppRunning = lsAppRunning;

        }

        public String getLsAppRunning() {
            return lsAppRunning;
        }

        public void setLsAppRunning(String lsAppRunning) {
            this.lsAppRunning = lsAppRunning;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public void addVol(String volNow) {
            lsVolNow.add(volNow);
        }

        public void addAmp(String ampNow) {
            lsVolNow.add(ampNow);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Start Time: " + startTime + " - End Time: " + endTime + "\n");
            builder.append("Voltage Now - Current Now\n");
            for (int i = 0; i < lsAmpNow.size(); i++) {
                builder.append(lsVolNow.get(i) + "  " + lsAmpNow.get(i) + "\n");
            }
            return builder.toString();

        }
    }

    private void showNotification(String time) {
        Notification notification = new Notification(R.drawable.about_icon, "Record", System.currentTimeMillis());
        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews notificationView = new RemoteViews(getContext().getPackageName(), R.layout.record_notification);
        notificationView.setTextViewText(R.id.title, getString(R.string.app_name));
        notificationView.setTextViewText(R.id.text, "Recording data ..." + " Time: " + time +"");
        notification.contentView = notificationView;
        mNotificationManager.notify(RECORD_NOTIFICATION_ID, notification);
    }
    private String listRunning() {
        StringBuilder builder = new StringBuilder();
        ActivityManager am =(ActivityManager)  getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo rap: am.getRunningAppProcesses()
                ) {
            builder.append(rap.processName + "\n");
        }
        return builder.toString();
    }
    private void updateRecordFileView() {
        record_files_ad.clear();
        File wearableOsDir = new File(WEARABLE_OS_PATH);
        if(!wearableOsDir.exists())
            wearableOsDir.mkdir();
        File[] files = wearableOsDir.listFiles();
        for (File file:files) {
            record_files_ad.add(file.getName());
            record_files_ad.setNotifyOnChange(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        final String path = Environment.getExternalStorageDirectory().toString();
        Uri uri = Uri.parse(path+File.separator+"WearableOS"+File.separator + record_files_ad.getItem(position));
        Toast.makeText(getContext(), path + File.separator + "WearableOS" + File.separator + record_files_ad.getItem(position), Toast.LENGTH_LONG).show();
       // intent.setDataAndType(uri, "plain/text");
       // getActivity().startActivity(intent);
        final MaterialDialog onClickFile = new MaterialDialog(getContext());
        //onClickFile.setTitle("Options");
        onClickFile.setMessage(path + File.separator + "WearableOS" + File.separator + record_files_ad.getItem(position));
        onClickFile.setPositiveButton(getString(R.string.open), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFile.dismiss();
                Intent i = new Intent(getActivity(), ViewLogActivity.class);
                i.putExtra("FILE_NAME", path + File.separator + "WearableOS" + File.separator + record_files_ad.getItem(position));
                getActivity().startActivity(i);
            }
        });
        onClickFile.setNegativeButton(getString(R.string.dialog_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFile.dismiss();
            }
        });
        onClickFile.show();
        //android.app.AlertDialog dialog = new android.app.AlertDialog(getContext());
    }

}
