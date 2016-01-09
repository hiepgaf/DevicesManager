package com.hieptran.devicesmanager.common;

import android.support.v4.app.Fragment;

/**
 * Created by hieptran on 09/01/2016.
 */
public class ViewPagerItemCommon {


        private final Fragment fragment;
        private final String title;

        public ViewPagerItemCommon(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public String getTitle() {
            return title;
        }


}
