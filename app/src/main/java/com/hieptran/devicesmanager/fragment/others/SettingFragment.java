package com.hieptran.devicesmanager.fragment.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hieptran.devicesmanager.R;

/**
 * Created by hiepth on 21/01/2016.
 */
public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment,container, false);
        return v;
    }

}


