package com.hieptran.devicesmanager.fragment.tweak.battery;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.hieptran.devicesmanager.AnalyzeActivity;
import com.hieptran.devicesmanager.MainActivity;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.services.PopupService;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.provider.DbHelper;

import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by hieptran on 02/03/2016.
 */
public class BatteryInfoFragment extends Fragment implements Const, View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int RECORD_NOTIFICATION_ID = 1;
    public static double BATTERY_CAPACITY;
    public static int level, voltage;
    public String result;
    public NotificationManager mNotificationManager;
    public LogVolAmp mLogRecord;
    public String mEndTimeRecord = "", mStartTimeRecord = "";
    double totalPower, currentPower;
    double realLevel;
    String top_tile = "";
    int time;
    Bitmap b;
    View v;
    View blur_view;
    boolean first_time = false;
    private DbHelper dbHelper;
    private WaveLoadingView waveLevelView, realLv;
    private TextView  mNowValueText;
    private TextView voltageText;
    private TextView totalPw;
    private ListView lvRecordFile;
    private android.os.Handler hand;
    private Button btRecord;
    private boolean isRecorded = true;
    private boolean isPopuped = false;
    private ArrayList<String> voltage_now_al, current_now_al, record_files_al;
    private ArrayAdapter<String> record_files_ad;
    boolean usbCharge,acCharge;
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Utils.saveInt("level_popup",level,getContext());
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            currentPower = Integer.valueOf(readVoltageNow()) * level * 2.3 / 1000;
           // count_time = Utils.getInt("time_record", 0, getContext());
            Log.d("HiepTHb", "Test     " + Utils.readFileUnRoot(BATTERY_VOLTAGE_NOW) + "        ---    " + Utils.getBoolean("rooted", false, getContext()));
            realLevel = (currentPower / totalPower);
            realLv.setProgressValue(100 - (int) (realLevel));
            realLv.setCenterTitle(String.format("%.2f", currentPower / 100) + " mWh\n");
            //realLv.setTopTitle(String.format("%.2f", realLevel) + " %");
            if (!isCharging) {
                waveLevelView.setAmplitudeRatio(1);
            }
            else {
                waveLevelView.setAmplitudeRatio(50);
            }
            waveLevelView.setProgressValue(100 - level);

            //  waveLevelView.setWaveColor(Color.GREEN);
            waveLevelView.setCenterTitle(level + " %");
            waveLevelView.setCenterTitleSize(18.0f);
            if(Utils.getBoolean("dark_theme",false,getContext())) {
                waveLevelView.setCenterTitleColor(Color.WHITE);
                realLv.setCenterTitleColor(Color.WHITE);

            }
            else {
                realLv.setCenterTitleColor(Color.BLACK);

                waveLevelView.setCenterTitleColor(Color.BLACK);
            }

            //voltageText.setText("Battery Voltage: " + voltage + " mV");
            if(intent.getAction().equals("com.hieptran.devicesmanager.CLOSED_POPUP_ACTION")) {
                showPopup.setBackgroundResource(R.drawable.bg_on);
                showPopup.setText(getString(R.string.show_popup));
                getActivity().stopService(mIntenShowPopup);
                isPopuped = false;

            }

        }
    };

    private final BroadcastReceiver mClosedPopup = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showPopup.setBackgroundResource(R.drawable.bg_on);
            showPopup.setText(getString(R.string.show_popup));
            getActivity().stopService(mIntenShowPopup);
            isPopuped = false;
        }
    };

    String mInfoPopup ="";
    private Runnable updateView = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(usbCharge)
                            mNowValueText.setText(getString(R.string.voltage_now)+": "+ readVoltageNow() + " uV" +
                                    "\n"+getString(R.string.current_now) +": "+ readCurrentNow() + " uA\n"+getString(R.string.charge_type)+" "+getString(R.string.usb_charge));
                            else if(acCharge)
                                mNowValueText.setText(getString(R.string.voltage_now)+": "+ readVoltageNow() + " uV" +
                                        "\n"+getString(R.string.current_now) +": "+ readCurrentNow() + " uA\n"+getString(R.string.charge_type)+" "+getString(R.string.ac_charge));
                            else
                                mNowValueText.setText(getString(R.string.voltage_now)+": "+ readVoltageNow() + " uV" +
                                        "\n"+getString(R.string.current_now) +": "+ readCurrentNow() + " uA\n"+getString(R.string.charge_type)+" "+getString(R.string.no_charge));
                            top_tile = getString(R.string.time)+": " + "N/A" +
                                    " \n" + getString(R.string.avg_power)+": " + "N/A" +
                                    " mW\n" + getString(R.string.bat_consumed)+
                                    ": " + "N/A" + " mAh" +
                                    "(~" + "%)\n"+getString(R.string.avg_voltaget)+": " + "N/A" + " uV\n"+getString(R.string.avg_current)+": " + "N/A" + " uA";


                            // voltage_now_ad.add("\t\t\t\t\t" + readVoltageNow() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + readCurrentNow());
                            // voltage_now_ad.setNotifyOnChange(true);
                            //  lsRunningApp.add(listRunning());
                            if (first_time) {
                                if (!isRecorded) {
                                  /*  count_time++;
                                    vol_sum += Double.valueOf(readVoltageNow());
                                    cur_sum += Double.valueOf(readCurrentNow());*/
                          /*  top_tile = "Time: " + Utils.formatSeconds(count_time)+
                                    " \n"+ "Average Power: "+ String.format("%.2f",vol_sum*cur_sum/count_time/count_time/1000/1000000) +
                                    " mW\n" +
                                    "Battery Consumed: "+ String.format("%.2f",Math.abs(cur_sum/60/1000/1000)) +" mAh" +
                                    "(~"+String.format("%.1f",100*Math.abs(cur_sum/60/1000/1000)/2300)+"%)" ;*/
//                                    updateRecordFileView();
                                    top_tile = getString(R.string.time)+": " + Utils.formatSeconds(Utils.getInt("time_record", 0, getContext())) +
                                            " \n" +getString(R.string.avg_power)+": "+ Utils.getString("average_power", "", getContext()) +
                                            " mW\n" +
                                            getString(R.string.bat_consumed)+": "+ Utils.getString("battery_consumed", "", getContext()) + " mAh" +
                                            "(~" + Utils.getString("percent", "", getContext()) + "%)\n"+
                                            getString(R.string.avg_voltaget)+": " + Utils.getString("average_voltage", "", getContext()) + " uV"+"\n"+
                                            getString(R.string.avg_current)+": " + Utils.getString("average_current", "", getContext()) + " uA";
                                    mIntenShowPopup.putExtra("info", top_tile);
                                    mIntenShowPopup.putExtra("level_popup",level);
                                    // totalPw.setText(top_tile);
                                    // btRecord.setText(getString(R.string.start_record));
                                } else {
                                    //btRecord.setText(("Stop record"));

                                    top_tile = getString(R.string.time)+": " + "N/A" +
                                            " \n" + getString(R.string.avg_power)+": " + "N/A" +
                                            " mW\n" + getString(R.string.bat_consumed)+
                                            ": " + "N/A" + " mAh" +
                                            "(~" + "%)\n"+getString(R.string.avg_voltaget)+": " + "N/A" + " uV\n"+getString(R.string.avg_current)+": " + "N/A" + " uA";

                                    mIntenShowPopup.putExtra("level_popup", level);
                               /* // count_time =0;
                                result = Utils.formatSeconds(time++);
                                btRecord.setText(result);
                                showNotification(result);
                                voltage_now_ad.add(readVoltageNow());

                                Log.d("HiepTHb", "avg voltage now : " + vol_sum / time);
                                voltage_now_ad.setNotifyOnChange(true);
                                current_now_ad.add(readCurrentNow());

                                Log.d("HiepTHb", "avg cur now : " + vol_sum / time);
                                current_now_ad.setNotifyOnChange(true);*/
                                }
                            }
                           // mInfoPopup = top_tile + "\n"+current_now_tv.getText()+"\n" + voltage_now_tv.getText();
                            Utils.saveString("info_popup", top_tile,getContext());
                            totalPw.setText(top_tile);
                        }
                    });
                }
            }).start();
            hand.postDelayed(updateView, 1000);
        }

    };
boolean show =false;
    @Override
    public void onClick(View v) {

        first_time = true;
        if (v == btRecord) {
            if (!isRecorded) {
                show =false;
                updateRecordFileView();
                isRecorded = true;
                btRecord.setBackgroundResource(R.drawable.bg_on);
                btRecord.setText(getString(R.string.start_record));
                getActivity().stopService(MainActivity.i);
                showPopup.setVisibility(View.GONE);

               /* mStartTimeRecord = new SimpleDateFormat("HHmmss").format(new Date(System.currentTimeMillis()));
                mLogRecord.setStartTime(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));*/
            } else {
                updateRecordFileView();
                //   record_files_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dbHelper.getTablesName());
                //  record_files_ad.setNotifyOnChange(true);
                time = 0;
                isRecorded = false;
               // mNotificationManager.cancel(RECORD_NOTIFICATION_ID);
                btRecord.setBackgroundResource(R.drawable.bg_off);
                show = true;
                showPopup.setVisibility(View.VISIBLE);
                showPopup.setBackgroundResource(R.drawable.bg_on);
                showPopup.setText(getString(R.string.show_popup));
                btRecord.setText(getString(R.string.stop_record));
                getActivity().startService(MainActivity.i);

            }
        } else {
            if(show) {
                if (isPopuped) {
                    Log.d("HiepTHb","Nhay1");
                    showPopup.setBackgroundResource(R.drawable.bg_on);
                    showPopup.setText(getString(R.string.show_popup));
                    getActivity().stopService(mIntenShowPopup);
                    isPopuped = false;


                } else {
                    Log.d("HiepTHb","Nhay2");
                    showPopup.setBackgroundResource(R.drawable.bg_off);
                    showPopup.setText(getString(R.string.cancle_popup));
                    getActivity().startService(mIntenShowPopup);
                    isPopuped = true;
                }
            }
        }
    }
Button showPopup;
    Intent mIntenShowPopup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.battery_info_fragment, container, false);
        waveLevelView = (WaveLoadingView) v.findViewById(R.id.waveLoadingView);
        totalPw = (TextView) v.findViewById(R.id.totalpower);
        realLv = (WaveLoadingView) v.findViewById(R.id.reallv);
        btRecord = (Button) v.findViewById(R.id.bt_start_record);
        showPopup = (Button) v.findViewById(R.id.bt_show_popup);
        mNowValueText = (TextView) v.findViewById(R.id.now_value);
        blur_view = v.findViewById(R.id.blur_view);
        btRecord.setOnClickListener(this);
        totalPower = 3.7 * getCapacityFromPowerProfile();
        dbHelper = new DbHelper(getContext());
        btRecord.setBackgroundResource(R.drawable.bg_on);
        lvRecordFile = (ListView) v.findViewById(R.id.lv_record_file);
        lvRecordFile.setOnItemClickListener(this);
        record_files_al = new ArrayList<>();
        voltage_now_al = new ArrayList<>();
        current_now_al = new ArrayList<>();
        BATTERY_CAPACITY = getCapacityFromPowerProfile();
      /*  voltage_now_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, voltage_now_al);
        current_now_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, current_now_al);*/
        record_files_ad = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dbHelper.getTablesName());
        record_files_ad.setNotifyOnChange(true);
        lvRecordFile.setAdapter(record_files_ad);
       // updateRecordFileView();
        mLogRecord = new LogVolAmp(mStartTimeRecord, mEndTimeRecord, voltage_now_al, current_now_al, listRunning());
        voltageText = (TextView) v.findViewById(R.id.bat_vol);
        voltageText.setText(getString(R.string.bat_capacity)+": "+ getCapacityFromPowerProfile() + "mAh");
        IntentFilter fiIntent = new IntentFilter();
        fiIntent.addAction(Intent.ACTION_BATTERY_CHANGED);
        fiIntent.addAction(Intent.ACTION_POWER_CONNECTED);
        fiIntent.addAction(Intent.ACTION_POWER_DISCONNECTED);
        fiIntent.addAction(Intent.ACTION_BATTERY_OKAY);
        fiIntent.addAction(Intent.ACTION_BATTERY_LOW);
        try {
            getActivity().registerReceiver(mBatInfoReceiver,fiIntent);
            getActivity().registerReceiver(mClosedPopup, new IntentFilter("com.hieptran.devicesmanager.CLOSED_POPUP_ACTION"));
        } catch (NullPointerException ignored) {
        }
        showPopup.setVisibility(View.GONE);
        showPopup.setOnClickListener(this);

        mIntenShowPopup = new Intent(getActivity(), PopupService.class);
        mIntenShowPopup.putExtra("bat_level",level);



        hand = new android.os.Handler();

//        b = BlurBitmapHelper.blurBitmap(getActivity(), 0, 0, 1080, 1920);

        return v;
    }

    private String readCurrentNow() {
        if (Utils.getBoolean("rooted", true, getContext()))
            return Utils.readFileRoot(BATTERY_CURRENT_NOW);
        else return Utils.readFileUnRoot(BATTERY_CURRENT_NOW);
    }

    private String readVoltageNow() {
        if (Utils.getBoolean("rooted", true, getContext()))
            return Utils.readFileRoot(BATTERY_VOLTAGE_NOW);
        else return Utils.readFileUnRoot(BATTERY_VOLTAGE_NOW);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hand != null) hand.post(updateView);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hand != null) hand.post(updateView);
        //getActivity().unregisterReceiver(mBatInfoReceiver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hand != null) hand.removeCallbacks(updateView);

        Utils.saveInt("time_record", 0, getContext());
        Utils.saveString("average_power", "", getContext());
        Utils.saveString("battery_consumed", "", getContext());
        Utils.saveString("average_voltage", "" , getContext());
        Utils.saveString("average_current", "", getContext());
        Utils.saveString("percent", "", getContext());
        getActivity().unregisterReceiver(mBatInfoReceiver);
        getActivity().stopService(mIntenShowPopup);
        getActivity().stopService(MainActivity.i);
    }

    private void showNotification(String time) {
        Notification notification = new Notification(R.drawable.about_icon, "Recording", System.currentTimeMillis());
        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews notificationView = new RemoteViews(getContext().getPackageName(), R.layout.record_notification);
        notificationView.setTextViewText(R.id.title, getString(R.string.app_name));
        notificationView.setTextViewText(R.id.text, "Recording data ..." + " Time: " + time + "");
        notification.contentView = notificationView;
        mNotificationManager.notify(RECORD_NOTIFICATION_ID, notification);
    }

    private String listRunning() {
        StringBuilder builder = new StringBuilder();
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo rap : am.getRunningAppProcesses()
                ) {
            builder.append(rap.processName + "\n");
        }
        return builder.toString();
    }

    private void updateRecordFileView() {
        record_files_ad.clear();
        for (int i = 0; i < dbHelper.getTablesName().size(); i++) {
            record_files_ad.add(dbHelper.getTablesName().get(i));
            record_files_ad.setNotifyOnChange(true);
        }
      /*  File wearableOsDir = new File(WEARABLE_OS_PATH);
        if (!wearableOsDir.exists())
            wearableOsDir.mkdir();
        File[] files = wearableOsDir.listFiles();
        for (File file : files) {
            record_files_ad.add(file.getName());
            record_files_ad.setNotifyOnChange(true);
        }*/
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Intent i = new Intent(getActivity(), AnalyzeActivity.class);
        i.putExtra("TABLE_NAME", record_files_ad.getItem(position));
       // AnalyzeActivity.REAL_TIME = position == record_files_ad.getCount() - 1;
      //  getActivity().startActivity(i);

       /* final String path = Environment.getExternalStorageDirectory().toString();
        Uri uri = Uri.parse(path + File.separator + "WearableOS" + File.separator + record_files_ad.getItem(position));
        Toast.makeText(getContext(), path + File.separator + "WearableOS" + File.separator + record_files_ad.getItem(position), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), ViewLogActivity.class);
        intent.putExtra("FILE_NAME", path + File.separator + "WearableOS" + File.separator + record_files_ad.getItem(position));
        getActivity().startActivity(intent);*/

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
    public double getCapacityFromPowerProfile() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return (Double) Class.forName(POWER_PROFILE_CLASS).getMethod("getAveragePower", java.lang.String.class).invoke(mPowerProfile_, "battery.capacity");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  0;
    }

}
