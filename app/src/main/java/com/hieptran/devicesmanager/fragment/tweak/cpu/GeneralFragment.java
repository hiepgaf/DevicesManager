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
import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;
import java.util.List;

import comboseekbar.ComboSeekBar;
import me.itangqi.waveloadingview.WaveLoadingView;


/**
 * Created by hieptran on 11/01/2016.
 */
public class GeneralFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, Const {
    TextView tvmax, tvmin;
    ComboSeekBar mSliderFreqs_max;
    ComboSeekBar mSliderFreqs_min;
    List<String> ls_freqs = new ArrayList<>();
    TextView tvmax_big, tvmin_big;
    ComboSeekBar mSliderFreqs_max_big;
    ComboSeekBar mSliderFreqs_min_big;
    List<String> ls_freqs_big = new ArrayList<>();
    private CircleChart[] mCoreUsageText;

    //For big cpu (cores > 4)
    private SwitchCompat[] mCoreCheckBox;
    private AppCompatTextView[] mCoreFreqText;
    private TextView[] mCoreLable;
    private SwitchCompat[] mCoreCheckBox_big;
    private CircleChart[] mCoreUsageText_big;
    private AppCompatTextView[] mCoreFreqText_big;
    private TextView[] mCoreLable_big;

   /* //Header
    private View default_header_core, big_header_core;*/

    private Handler handler;
    private WaveLoadingView mUsageCircle;
    private boolean isHaveBigCluster = CPU.isBigCluster();
    private int mCore;
    /**
     * Thay doi cau truc ham khoi tao
     * @param core day vao core bat dau cua cluster
     */
    public GeneralFragment(int core) {
        this.mCore =core;
    }



    private final Runnable cpuUsage = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final float[] usage = CPU.getCpuUsage();
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Mac dinh thiet lap cho ca CPU

                                if (usage != null) {
                                    List<Integer> range;
                                   /* if (usage[0] > 75) {
                                        mUsageCircle.setWaveColor(Color.RED);
                                        mUsageCircle.setBorderColor(Color.RED);
                                        mUsageCircle.setCenterTitleColor(Color.WHITE);
                                    } else {
                                        mUsageCircle.setWaveColor(getResources().getColor(R.color.colorPrimary));
                                        mUsageCircle.setBorderColor(R.color.color_primary);
                                        mUsageCircle.setCenterTitleColor(Color.BLACK);
                                    }

                                    mUsageCircle.setProgressValue(Math.round(100 - usage[0]));
                                    mUsageCircle.setCenterTitle(String.valueOf(Math.round(usage[0])));*/
                                    if(mCore == CPU.getDefaultcore())
                                       range= CPU.getDefaultCoreRange();
                                    else
                                        range = CPU.getBigCoreRange();
                                    //Mac dinh voi core 0-3

                                    if (mCoreCheckBox != null && mCoreFreqText != null) {


                                        for (int i = 0; i < mCoreCheckBox.length; i++) {
                                            int cur = CPU.getCurFreq(range.get(i));
                                            if (mCoreCheckBox[i] != null)
                                                mCoreCheckBox[i].setChecked(cur != 0);
                                            if (mCoreFreqText[i] != null)
                                                mCoreFreqText[i].setText(cur == 0 ? getString(R.string.offline) : cur / 1000 +
                                                        getString(R.string.mhz));
                                        }
                                    }


                                    if (mCoreUsageText != null) {
                                        for (int i = 0; i < mCoreUsageText.length; i++) {
                                            if (mCoreUsageText[i] != null)
                                                mCoreUsageText[i].setMax(100);
                                            mCoreUsageText[i].setProgress(Math.round(usage[range.get(i) + 1]));

                                        }
                                    }

                                    //Neu co bigcluster, thi thuc hien add view kia vao

                               /*     if (isHaveBigCluster) {
                                        if (mCoreCheckBox_big != null && mCoreFreqText_big != null) {
                                            List<Integer> range = CPU.getBigCoreRange();
                                            for (int i = 0; i < mCoreCheckBox_big.length; i++) {
                                                int cur = CPU.getCurFreq(range.get(i));
                                                if (mCoreCheckBox_big[i] != null)
                                                    mCoreCheckBox_big[i].setChecked(cur != 0);
                                                if (mCoreFreqText_big[i] != null)
                                                    mCoreFreqText_big[i].setText(cur == 0 ? getString(R.string.offline) : cur / 1000 +
                                                            getString(R.string.mhz));
                                            }
                                        }


                                        if (mCoreUsageText_big != null) {
                                            List<Integer> cores = CPU.getBigCoreRange();
                                            for (int i = 0; i < mCoreUsageText_big.length; i++) {
                                                if (mCoreUsageText_big[i] != null)
                                                    mCoreUsageText_big[i].setMax(100);
                                                mCoreUsageText_big[i].setProgress(Math.round(usage[cores.get(i) + 1]));

                                            }
                                        }
                                    }*/

                                }
                            }
                        });
                    } catch (NullPointerException ignored) {
                    }
                }
            }).start();

            handler.postDelayed(cpuUsage, 2000);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private LinearLayout defaultCoreInit(LayoutInflater inflater, ViewGroup container) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);
       // layout.addView(inflater.inflate(R.layout.div_view, container, false));

        //layout.addView(v2);
        //
        mCoreCheckBox = new SwitchCompat[CPU.getDefaultCoreRange().size()];
        mCoreUsageText = new CircleChart[mCoreCheckBox.length];
        mCoreLable = new TextView[mCoreCheckBox.length];
        mCoreFreqText = new AppCompatTextView[mCoreCheckBox.length];
        for (int i = 0; i < mCoreCheckBox.length; i++) {
            View view = inflater.inflate(R.layout.core_view_item, container, false);
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

  /*  private LinearLayout bigClusterCoreInit(LayoutInflater inflater, ViewGroup container) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);
        View v2 = inflater.inflate(R.layout.div_view, container, false);
        layout.addView(v2);
        //
        big_header_core = inflater.inflate(R.layout.title_header_view, container, false);
        ((TextView) big_header_core.findViewById(R.id.header_title)).setText(getResources().getString(R.string.big_core_tunning));
        layout.addView(big_header_core);
        layout.addView(inflater.inflate(R.layout.div_view, container, false));

        // layout.addView(v2);

        mCoreCheckBox_big = new SwitchCompat[CPU.getBigCoreRange().size()];
        mCoreUsageText_big = new CircleChart[mCoreCheckBox_big.length];
        mCoreLable_big = new TextView[mCoreCheckBox_big.length];
        mCoreFreqText_big = new AppCompatTextView[mCoreCheckBox_big.length];
        for (int i = 0; i < mCoreCheckBox_big.length; i++) {
            View view = inflater.inflate(R.layout.core_view_item, container, false);
            mCoreCheckBox_big[i] = (SwitchCompat) view.findViewById(R.id.core_checkbox);
            mCoreCheckBox_big[i].setOnCheckedChangeListener(new mOnCheckedChanged(CPU.getBigCoreRange().get(i)));
            mCoreLable_big[i] = (TextView) view.findViewById(R.id.core_lable);
            mCoreLable_big[i].setText(getString(R.string.core, i + 1));

            mCoreUsageText_big[i] = (CircleChart) view.findViewById(R.id.circle_itemcore);

            mCoreFreqText_big[i] = (AppCompatTextView) view.findViewById(R.id.freq);

            layout.addView(view);
        }
        return layout;
    }
*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        handler = new Handler();
        default_getFreqLs(mCore);
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);
      /*  View v = inflater.inflate(R.layout.cpu_tw_item, container, false);
        ScrollView scrollView = (ScrollView) v.findViewById(R.id.cpu_scrollView);*/
        layout.addView(defaultCoreInit(inflater, container));
        layout.addView(default_sliderBarInit(inflater, container));
/*
        scrollView.addView(layout);
*/
        //return super.onCreateView(inflater, container, savedInstanceState);
        return layout;
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

    private LinearLayout default_sliderBarInit(LayoutInflater inflater, ViewGroup container) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);

        View view_max = inflater.inflate(R.layout.slider_bar_item, container, false);
        View view_min = inflater.inflate(R.layout.slider_bar_item, container, false);
        tvmax = (TextView) view_max.findViewById(R.id.slider_bar_text);
        mSliderFreqs_max = (ComboSeekBar) view_max.findViewById(R.id.freqs_slider_bar);
        mSliderFreqs_min = (ComboSeekBar) view_min.findViewById(R.id.freqs_slider_bar);
        tvmin = (TextView) view_min.findViewById(R.id.slider_bar_text);
        default_setFreqView(mCore);
        layout.addView(view_max);
        layout.addView(view_min);
        return layout;
    }

    private void default_getFreqLs(int core) {

        for (int freq : CPU.getFreqs(core)) {
            ls_freqs.add(freq / 1000 + "");
        }

    }

    private void default_setFreqView(int core) {
        tvmin.setText(getResources().getString(R.string.min_cpu_freq) + CPU.getMinFreq(core, true) / 1000 + " MHz");
        tvmax.setText(getResources().getString(R.string.max_cpu_freqs) + CPU.getMaxFreq(core, true) / 1000 + " MHz");
        if (DEBUG) {
            Utils.showLog("min freq - default : " + CPU.getMinFreq(core, true));
            Utils.showLog("max freq - default : " + CPU.getMaxFreq(core, true));
            Utils.showLog("ls_freqs.size " + ls_freqs.size());
        }
        mSliderFreqs_max.setAdapter(ls_freqs);
        mSliderFreqs_max.setMax(ls_freqs.size() - 1);
        mSliderFreqs_max.setOnSeekBarChangeListener(this);
        mSliderFreqs_max.setProgress(ls_freqs.indexOf(String.valueOf(CPU.getMaxFreq(core, true) / 1000)));
        mSliderFreqs_min.setAdapter(ls_freqs);
        mSliderFreqs_min.setMax(ls_freqs.size() - 1);
        mSliderFreqs_min.setOnSeekBarChangeListener(this);
        mSliderFreqs_min.setProgress(ls_freqs.indexOf(String.valueOf(CPU.getMinFreq(core, true) / 1000)));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (DEBUG) {
            Utils.showLog(" Progress" + seekBar.getProgress());
        }
        if (seekBar == mSliderFreqs_max) {
            int value_default = Utils.stringToInt(ls_freqs.get(seekBar.getProgress()));
            CPU.setMaxFreq(CommandControl.CommandType.CPU_LITTLE, value_default * 1000, getActivity());
            tvmax.setText(getResources().getString(R.string.max_cpu_freqs) + value_default + " MHz");

        } else if (seekBar == mSliderFreqs_min) {
            int value_default = Utils.stringToInt(ls_freqs.get(seekBar.getProgress()));
            CPU.setMinFreq(CommandControl.CommandType.CPU_LITTLE, value_default * 1000, getActivity());
            tvmin.setText(getResources().getString(R.string.min_cpu_freq) + value_default + " MHz");

        } else if (seekBar == mSliderFreqs_max_big) {
            int value_big = Utils.stringToInt(ls_freqs_big.get(seekBar.getProgress()));
            CPU.setMaxFreq(CommandControl.CommandType.CPU, value_big * 1000, getActivity());
            tvmax_big.setText(getResources().getString(R.string.max_cpu_freqs) + value_big + " MHz");
        } else {
            int value_big = Utils.stringToInt(ls_freqs_big.get(seekBar.getProgress()));
            CPU.setMinFreq(CommandControl.CommandType.CPU, value_big * 1000, getActivity());
            tvmin_big.setText(getResources().getString(R.string.min_cpu_freq) + value_big + " MHz");
        }
    }
/*

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
*/


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

