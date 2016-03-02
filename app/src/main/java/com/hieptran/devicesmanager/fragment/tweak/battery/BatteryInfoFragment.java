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

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by hieptran on 02/03/2016.
 */
public class BatteryInfoFragment extends Fragment{
    private WaveLoadingView waveLevelView;
    private TextView lvText;
    private TextView voltageText;
    private TextView tempText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.battery_info_fragment,container,false);
        waveLevelView = (WaveLoadingView) v.findViewById(R.id.waveLoadingView);
        lvText = (TextView) v.findViewById(R.id.text);
        lvText.setText("Battery Level");
        voltageText = (TextView) v.findViewById(R.id.bat_vol);
        try {
            getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (NullPointerException ignored) {
        }
        return v;
    }
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            waveLevelView.setProgressValue(100-level);
          //  waveLevelView.setWaveColor(Color.GREEN);
            waveLevelView.setCenterTitle(level+" %");
            voltageText.setText("Battery Voltage "+voltage + "    mV");

        }
    };
}
