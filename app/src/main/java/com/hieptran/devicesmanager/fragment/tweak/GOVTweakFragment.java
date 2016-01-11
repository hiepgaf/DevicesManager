package com.hieptran.devicesmanager.fragment.tweak;

import android.os.Bundle;

import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.fragment.tweak.gov.GovernorFragment;
import com.hieptran.devicesmanager.fragment.tweak.gov.IOFragment;

/**
 * Created by hieptran on 11/01/2016.
 */
public class GOVTweakFragment extends ViewPagerFragment {
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        addFragment(new ViewPagerItemCommon(new GovernorFragment(), "GovernorFragment"));
        addFragment(new ViewPagerItemCommon(new IOFragment(), "IOFragment"));

    }
}
