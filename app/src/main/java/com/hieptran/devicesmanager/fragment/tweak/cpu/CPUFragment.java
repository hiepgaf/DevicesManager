package com.hieptran.devicesmanager.fragment.tweak.cpu;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.CircleChart;
import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by hiepth on 18/01/2016.
 */
public class CPUFragment extends Fragment {
    private Handler handler;
    private WaveLoadingView mUsageCircle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cpu_tw,container,false);
        //Add Cpu Usage
        handler = new Handler();
        mUsageCircle = (WaveLoadingView) v.findViewById(R.id.waveLoadingView);
        getFragmentManager().beginTransaction().add(R.id.fragment_pager,new controlFragment()).commit();
        return v;
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
                                    if (usage[0] > 75) {
                                        mUsageCircle.setWaveColor(Color.RED);
                                        mUsageCircle.setBorderColor(Color.RED);
                                        mUsageCircle.setCenterTitleColor(Color.WHITE);
                                    } else {
                                        mUsageCircle.setWaveColor(getResources().getColor(R.color.colorPrimary));
                                        mUsageCircle.setBorderColor(R.color.color_primary);
                                        mUsageCircle.setCenterTitleColor(Color.WHITE);
                                    }

                                    mUsageCircle.setProgressValue(Math.round(100 - usage[0]));
                                    mUsageCircle.setCenterTitle(String.valueOf(Math.round(usage[0])));

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
    /*   @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        View v1 = inflater.inflate(R.layout.usage_view_item, container, false);
        layout.addView(v1);
        mUsageCircle = (WaveLoadingView) v1.findViewById(R.id.waveLoadingView);

        if(CPU.isBigCluster()) {
            addFragment(new ViewPagerItemCommon(new GeneralFragment(0),"Litte"));
            addFragment(new ViewPagerItemCommon(new GeneralFragment(4),"Big"));
        } else {
            showTabs(false);
            addFragment(new ViewPagerItemCommon(new GeneralFragment(0), getString(R.string.default_core)));
        }

    }*/

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

    public static class controlFragment extends ViewPagerFragment {
        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(CPU.isBigCluster()) {
                        setTabsColor(Color.GRAY);
                        addFragment(new ViewPagerItemCommon(new GeneralFragment(0), "Litte"));
                        addFragment(new ViewPagerItemCommon(new GeneralFragment(4), "Big"));
                    } else {
                        showTabs(false);
                        addFragment(new ViewPagerItemCommon(new GeneralFragment(0), getString(R.string.default_core)));
                    }
                }
            });

        }
    }
}
