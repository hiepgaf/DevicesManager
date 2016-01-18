package com.hieptran.devicesmanager.fragment.tweak;

import android.os.Bundle;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.fragment.tweak.cpu.AdvanceFragment;
import com.hieptran.devicesmanager.fragment.tweak.cpu.CPUFragment;
import com.hieptran.devicesmanager.fragment.tweak.cpu.CPUTimeTable;
import com.hieptran.devicesmanager.fragment.tweak.cpu.GeneralFragment;

/**
 * Created by hieptran on 11/01/2016.
 */
public class CPUTweakFragment extends ViewPagerFragment {
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        addFragment(new ViewPagerItemCommon(new CPUTimeTable(), getResources().getString(R.string.time_table_title)));
        addFragment(new ViewPagerItemCommon(new CPUFragment(), getString(R.string.basic_title)));
        addFragment(new ViewPagerItemCommon(new AdvanceFragment(), getString(R.string.advance_title)));
    }
}
