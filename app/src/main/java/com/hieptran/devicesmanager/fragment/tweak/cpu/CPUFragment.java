package com.hieptran.devicesmanager.fragment.tweak.cpu;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.CircleChart;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;
import java.util.List;

import comboseekbar.ComboSeekBar;
import me.itangqi.waveloadingview.WaveLoadingView;


/**
 * Created by hieptran on 11/01/2016.
 */
public class CPUFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    TextView tvmax, tvmin;
    ComboSeekBar mSliderFreqs_max;
    ComboSeekBar mSliderFreqs_min;
    List<String> ls_freqs = new ArrayList<>();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        getFreqLs();
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
        mSliderFreqs_max = (ComboSeekBar) view_max.findViewById(R.id.freqs_slider_bar);
        mSliderFreqs_min = (ComboSeekBar) view_min.findViewById(R.id.freqs_slider_bar);
        tvmin = (TextView) view_min.findViewById(R.id.slider_bar_text);
        setFreqsView();
        layout.addView(view_max);
        layout.addView(inflater.inflate(R.layout.div_view, container, false));
        layout.addView(view_min);
        return layout;
    }

    private void getFreqLs() {

        for (int freq : CPU.getFreqs()) {
            ls_freqs.add(freq / 1000 + "");
        }

    }

    private void setFreqsView() {
        tvmin.setText("Minimum CPU frequency: " + CPU.getMinFreq(true));
        tvmax.setText("Maximum CPU frequency: " + CPU.getMaxFreq(true));
        mSliderFreqs_max.setAdapter(ls_freqs);
        mSliderFreqs_max.setMax(ls_freqs.size());
        mSliderFreqs_max.setOnSeekBarChangeListener(this);
        //mSliderFreqs_max.setProgress(ls_freqs.indexOf(String.valueOf(CPU.getMaxFreq(true))));
        mSliderFreqs_min.setAdapter(ls_freqs);
        mSliderFreqs_min.setMax(ls_freqs.size());
        mSliderFreqs_min.setOnSeekBarChangeListener(this);
        //mSliderFreqs_max.setProgress(ls_freqs.indexOf(String.valueOf(CPU.getMinFreq(true))));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int value = Utils.stringToInt(ls_freqs.get(seekBar.getProgress()));
        if (seekBar == mSliderFreqs_max) {

            CPU.setMaxFreq(value * 1000, getActivity());
            tvmax.setText("Maximum CPU frequency: " + value + " MHz");

        } else {
            CPU.setMinFreq(value * 1000, getActivity());
            tvmin.setText("Minimum CPU frequency: " + value + " MHz");

        }
    }

    //Test - Hien tai chua xu ly dung
    private int getPos(int val) {
        int k = 0;
        for (String i : ls_freqs
                ) {
            if (!i.equals(val))
                k++;
            else
                return k;
        }
        return k;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.set_on_boot, menu);
        MenuItem item = menu.findItem(R.id.set_on_boot);
        SwitchCompat switchCompat = (SwitchCompat) MenuItemCompat.getActionView(item);
        super.onCreateOptionsMenu(menu, inflater);
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

