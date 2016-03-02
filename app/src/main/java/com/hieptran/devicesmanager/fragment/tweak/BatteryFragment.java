package com.hieptran.devicesmanager.fragment.tweak;

import android.os.Bundle;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.fragment.tweak.battery.BatteryInfoFragment;
import com.hieptran.devicesmanager.fragment.tweak.battery.PowerInfoFragment;

/**
 * Created by hieptran on 02/03/2016.
 */
public class BatteryFragment extends ViewPagerFragment{
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        addFragment(new ViewPagerItemCommon(new BatteryInfoFragment(), getString(R.string.battery_info)));
        addFragment(new ViewPagerItemCommon(new PowerInfoFragment(), getString(R.string.power_info)));
    }
}
