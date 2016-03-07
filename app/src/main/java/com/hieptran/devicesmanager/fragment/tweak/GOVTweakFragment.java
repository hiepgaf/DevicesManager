package com.hieptran.devicesmanager.fragment.tweak;

import android.os.Bundle;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.ViewPagerItemCommon;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.fragment.tweak.gov.DefaultFragment;
import com.hieptran.devicesmanager.fragment.tweak.gov.IOFragment;
import com.hieptran.devicesmanager.utils.tweak.CPU;

/**
 * Created by hieptran on 11/01/2016.
 */
public class GOVTweakFragment extends ViewPagerFragment {
    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        String default_title = getString(R.string.tunning);

        addFragment(new ViewPagerItemCommon(new BigLitteGovFrag(), default_title));
        //addFragment(new ViewPagerItemCommon(new HotPlugFragment(), "HotPlugFragment"));
        addFragment(new ViewPagerItemCommon(new IOFragment(), "I/O"));
    }

    public static class BigLitteGovFrag extends ViewPagerFragment {
        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            String default_title = getString(R.string.tunning);
            if (CPU.isBigCluster()) {
                default_title = getString(R.string.litter_core_title);
            }
            addFragment(new ViewPagerItemCommon(new DefaultFragment(0), default_title));
            showTabs(false);
            if (CPU.isBigCluster()) {
                addFragment(new ViewPagerItemCommon(new DefaultFragment(4), getString(R.string.big_core_title)));
                showTabs(true);
            }

        }
    }
}
