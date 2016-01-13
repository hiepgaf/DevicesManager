package com.hieptran.devicesmanager.fragment.tweak;

import android.os.Bundle;

import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.fragment.tweak.cpu.CPUFragment;
import com.hieptran.devicesmanager.fragment.tweak.cpu.CPUTimeTable;

/**
 * Created by hieptran on 11/01/2016.
 */
public class CPUTweakFragment extends ViewPagerFragment {
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        addFragment(new ViewPagerItemCommon(new CPUTimeTable(), "CPUTimeTable"));
        addFragment(new ViewPagerItemCommon(new CPUFragment(), "CPUFragment"));

    }
}
