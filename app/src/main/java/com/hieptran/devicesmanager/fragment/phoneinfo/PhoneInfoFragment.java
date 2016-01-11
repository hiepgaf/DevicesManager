package com.hieptran.devicesmanager.fragment.phoneinfo;

import android.os.Bundle;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;

/**
 * Created by hieptran on 11/01/2016.
 */
public class PhoneInfoFragment extends ViewPagerFragment {
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        addFragment(new ViewPagerItemCommon(new DeviceFragment(), getString(R.string.pif_devices)));
        addFragment(new ViewPagerItemCommon(new CPUInfoFragment(), getString(R.string.pif_cpu)));
        addFragment(new ViewPagerItemCommon(new DeviceFragment(), getString(R.string.pif_devices)));

    }
}
