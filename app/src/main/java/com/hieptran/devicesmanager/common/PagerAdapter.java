package com.hieptran.devicesmanager.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by hieptran on 09/01/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private final List<ViewPagerItemCommon> items;

    public PagerAdapter(FragmentManager fragmentManager, List<ViewPagerItemCommon> items) {
        super(fragmentManager);
        this.items = items;
    }

    @Override
    public Fragment getItem(int i) {
        return items.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).getTitle() != null ? items.get(position).getTitle() : super.getPageTitle(position);
    }

}
