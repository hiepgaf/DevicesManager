package com.hieptran.devicesmanager.fragment.tweak.cpu;

import android.os.Bundle;

import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.utils.tweak.CPU;

/**
 * Created by hieptran on 15/01/2016.
 */
public class AdvanceFragment extends ViewPagerFragment {
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        if (CPU.isBigCluster()) {
            addFragment(new ViewPagerItemCommon(new CPUTimeTable(), "LITTE"));
            addFragment(new ViewPagerItemCommon(new CPUTimeTable(), "BIG"));
        } else {
            addFragment(new ViewPagerItemCommon(new CPUTimeTable(), " "));
            showTabs(false);
        }
    }
}
