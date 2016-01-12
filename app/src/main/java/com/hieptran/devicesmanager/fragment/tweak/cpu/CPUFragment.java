package com.hieptran.devicesmanager.fragment.tweak.cpu;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.CircleChart;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;
import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;


/**
 * Created by hieptran on 11/01/2016.
 */
public class CPUFragment extends Fragment {
    TextView tvmax, tvmin;
    Spinner dseekbarmax;
    Spinner dseekbarmin;
    ArrayAdapter<String> freqs_adapter;
    private SwitchCompat[] mCoreCheckBox;
    private CircleChart[] mCoreUsageText;
    private AppCompatTextView[] mCoreFreqText;
    private TextView[] mCoreLable;
    private Handler handler;
    private WaveLoadingView mUsageCircle;
    private final Runnable cpuUsage = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final float[] usage = CPU.getCpuUsage();
                    for (int i = 0; i < usage.length; i++) {

                    }
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (usage != null) {
                                    if (usage[0] > 75) {
                                        mUsageCircle.setWaveColor(getResources().getColor(R.color.colorPrimary));
                                        mUsageCircle.setBorderColor(R.color.colorPrimary);
                                        mUsageCircle.setCenterTitleColor(R.color.white);
                                    } else {
                                        mUsageCircle.setWaveColor(getResources().getColor(R.color.cpu_usage_lower_bg));
                                        mUsageCircle.setBorderColor(R.color.cpu_usage_lower_bg);
                                        mUsageCircle.setCenterTitleColor(R.color.textcolor_dark);
                                    }

                                    mUsageCircle.setProgressValue(Math.round(100 - usage[0]));
                                    mUsageCircle.setCenterTitle(String.valueOf(Math.round(usage[0])));
                                    if (mCoreCheckBox != null && mCoreFreqText != null) {
                                        // List<Integer> range = CPU.getBigCoreRange();
                                        for (int i = 0; i < mCoreCheckBox.length; i++) {
                                            int cur = CPU.getCurFreq(i);
                                            if (mCoreCheckBox[i] != null)
                                                mCoreCheckBox[i].setChecked(cur != 0);
                                            if (mCoreFreqText[i] != null)
                                                mCoreFreqText[i].setText(cur == 0 ? getString(R.string.offline) : cur / 1000 +
                                                        getString(R.string.mhz));
                                        }
                                    }
                                    if (mCoreUsageText != null) {
                                        //List<Integer> cores = CPU.getCoreCount();
                                        for (int i = 0; i < mCoreUsageText.length; i++) {

                                            String message = Math.round(usage[i + 1]) + "%";
                                            if (mCoreUsageText[i] != null)
                                                mCoreUsageText[i].setMax(100);
                                            mCoreUsageText[i].setProgress(Math.round(usage[i + 1]));
                                            //mCoreUsageText[i].setWaveColor(Color.LTGRAY);
                                            // mCoreUsageText[i].setBorderColor(Color.LTGRAY);
                                            // mCoreUsageText[i].setCenterTitleColor(R.color.textcolor_dark);
                                        }
                                    }


                                }
                            }
                        });
                    } catch (NullPointerException ignored) {
                    }
                }
            }).start();

            handler.postDelayed(cpuUsage, 1000);
        }
    };

    private LinearLayout coreInit(LayoutInflater inflater, ViewGroup container) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);
        //Add Cpu Usage
        View v1 = inflater.inflate(R.layout.usage_view_item, container, false);
        v1.setBackgroundColor(Color.LTGRAY);
        layout.addView(v1);
        mUsageCircle = (WaveLoadingView) v1.findViewById(R.id.waveLoadingView);


        mCoreCheckBox = new SwitchCompat[CPU.getCoreCount()];
        mCoreUsageText = new CircleChart[mCoreCheckBox.length];
        mCoreLable = new TextView[mCoreCheckBox.length];
        mCoreFreqText = new AppCompatTextView[mCoreCheckBox.length];
        for (int i = 0; i < mCoreCheckBox.length; i++) {
            View view = inflater.inflate(R.layout.core_view_item, container, false);
            view.setBackgroundColor(Color.LTGRAY);
            mCoreCheckBox[i] = (SwitchCompat) view.findViewById(R.id.core_checkbox);
            mCoreCheckBox[i].setOnCheckedChangeListener(new mOnCheckedChanged(i));
            mCoreLable[i] = (TextView) view.findViewById(R.id.core_lable);
            mCoreLable[i].setText(getString(R.string.core, i + 1));

            mCoreUsageText[i] = (CircleChart) view.findViewById(R.id.circle_itemcore);

            mCoreFreqText[i] = (AppCompatTextView) view.findViewById(R.id.freq);

            layout.addView(view);
        }
        return layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        handler = new Handler();
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);
        View v = inflater.inflate(R.layout.cpu_tw, container, false);
        ScrollView scrollView = (ScrollView) v.findViewById(R.id.cpu_scrollView);

        layout.addView(coreInit(inflater, container));
        layout.addView(sliderBarinit(inflater, container));
        scrollView.addView(layout);
        //return super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null) handler.post(cpuUsage);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) handler.removeCallbacks(cpuUsage);
    }

    private LinearLayout sliderBarinit(LayoutInflater inflater, ViewGroup container) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);

        View view_max = inflater.inflate(R.layout.slider_bar_item, container, false);
        // view_max.setPadding(10,10,10,10);
        View view_min = inflater.inflate(R.layout.slider_bar_item, container, false);
        // view_min.setPadding(10,10,10,10);
        view_max.setBackgroundColor(Color.LTGRAY);
        view_min.setBackgroundColor(Color.LTGRAY);
        tvmax = (TextView) view_max.findViewById(R.id.slider_bar_text);

        tvmin = (TextView) view_min.findViewById(R.id.slider_bar_text);
        getFreqLs();
        dseekbarmax = (Spinner) view_max.findViewById(R.id.m_spinner);
        dseekbarmax.setAdapter(freqs_adapter);
        dseekbarmin = (Spinner) view_min.findViewById(R.id.m_spinner);
        dseekbarmin.setAdapter(freqs_adapter);

        layout.addView(view_max);
        layout.addView(inflater.inflate(R.layout.div_view, container, false));
        layout.addView(view_min);
        return layout;
    }

    private void getFreqLs() {
        List<String> freqs = new ArrayList<>();
        for (int freq : CPU.getFreqs()) {
            freqs.add(freq / 1000 + getString(R.string.mhz));
            //freqs_adapter.add(freq / 1000 + getString(R.string.mhz));
            Log.d("HiepTHb", freq / 1000 + getString(R.string.mhz));
        }
        tvmin.setText("Minimum CPU frequency: " + freqs.get(0));
        tvmax.setText("Maximum CPU frequency: " + freqs.get(freqs.size() - 1));
        freqs_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, freqs);
        freqs_adapter.setNotifyOnChange(true);
    }

    class mOnCheckedChanged implements SwitchCompat.OnCheckedChangeListener {
        private int core;

        public mOnCheckedChanged(int core) {
            this.core = core;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("HiepTHb", core + " " + buttonView.isChecked());
            CPU.activateCore(core, buttonView.isChecked(), getActivity());
        }
    }
}

