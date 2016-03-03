package com.hieptran.devicesmanager.fragment.tweak.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by hieptran on 02/03/2016.
 */
public class BatteryInfoFragment extends Fragment implements Const {
    private WaveLoadingView waveLevelView,realLv;
    private TextView lvText;
    private TextView voltageText;
    private TextView tempText,totalPw;
    public static int level,voltage;
    double totalPower,currentPower;
    double realLevel;
    private TextView current_now_tv,voltage_now_tv;
    private  android.os.Handler hand;
    int time;
    private List<String> lsRunningApp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.battery_info_fragment,container,false);
        waveLevelView = (WaveLoadingView) v.findViewById(R.id.waveLoadingView);
        totalPw = (TextView) v.findViewById(R.id.totalpower);
        realLv = (WaveLoadingView) v.findViewById(R.id.reallv);
        current_now_tv = (TextView) v.findViewById(R.id.current_now_tv);
        voltage_now_tv = (TextView) v.findViewById(R.id.voltage_now_tv);
        totalPower = 3.7 * 2300;
        lvText = (TextView) v.findViewById(R.id.text);
        lvText.setText("Battery Level");
        totalPw.setText("Total: "+String.valueOf(totalPower)+ " mWh");

        voltageText = (TextView) v.findViewById(R.id.bat_vol);
        try {
            getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (NullPointerException ignored) {
        }
        if(Utils.DARK) {
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
            currentPower = Integer.valueOf(readVoltageNow()) * level * 2.3;
            realLevel  = (currentPower/totalPower);
            realLv.setProgressValue(100 - (int) (realLevel));
            realLv.setCenterTitle(String.format("%.2f", currentPower / 100) + " mW\n");
            realLv.setTopTitle(String.format("%.2f", realLevel) + " %");
            if(!isCharging)
                waveLevelView.setAmplitudeRatio(1);
            else waveLevelView.setAmplitudeRatio(50);
                waveLevelView.setProgressValue(100-level);

            //  waveLevelView.setWaveColor(Color.GREEN);
            waveLevelView.setCenterTitle(level+" %");
            voltageText.setText("Battery Voltage "+voltage + "    mV");

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
                            Date date = new Date(1000*(time++)); //260000 milliseconds

                            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
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
                            String result = sdf.format(date);
                            current_now_tv.setText("Current now: "+ readCurrentNow());
                            voltage_now_tv.setText("Voltage now: " + readVoltageNow());
                           // voltage_now_ad.add("\t\t\t\t\t" + readVoltageNow() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + readCurrentNow());
                           // voltage_now_ad.setNotifyOnChange(true);
                          //  lsRunningApp.add(listRunning());

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
    }
    //
public class LogVolAmp {
    private String startTime,endTime;
        private ArrayList<String> lsVolNow,lsAmpNow;
        void LogVolAmp(String startTime, String endTime,ArrayList<String>lsVolNow,ArrayList<String> lsAmpNow) {
            this.endTime =endTime;
            this.lsAmpNow = lsAmpNow;
            this.lsVolNow = lsVolNow;
            this.startTime = startTime;
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
            builder.append("Start Time: "+ startTime + " - End Time: "+ endTime + "\n");
            builder.append("Voltage Now - Current Now\n");
            for(int i =0; i < lsAmpNow.size();i++) {
                builder.append(lsVolNow.get(i)+lsAmpNow.get(i)+"\n");
            }
            return builder.toString();

        }
    }
}
