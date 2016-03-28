package com.hieptran.devicesmanager.fragment.others;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.hieptran.commonlibs.andoid.common.contrib.Util;
import com.hieptran.devicesmanager.MainActivity;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Utils;

/**
 * Created by hiepth on 21/01/2016.
 */
public class SettingFragment extends Fragment {
    private SwitchCompat mSwitchCompatSetOnBoot, mSwitchCompatForceEng, mSwitchCompatDark;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment,container, false);
        mSwitchCompatSetOnBoot = (SwitchCompat) v.findViewById(R.id.switchcompat_view);
        mSwitchCompatForceEng = (SwitchCompat) v.findViewById(R.id.switchcompat_view_lang);
        mSwitchCompatDark = (SwitchCompat) v.findViewById(R.id.switch_dark);
        mSwitchCompatDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("dark_theme",isChecked,getContext());
             //   getContext().startActivity(new Intent(getActivity().getApplication(),MainActivity.class));
            }
        });
        mSwitchCompatForceEng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("force_eng",isChecked,getContext());
            }
        });
        mSwitchCompatSetOnBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("set_on_boot",isChecked,getContext());

            }
        });
        mSwitchCompatDark.setChecked(Utils.getBoolean("dark_theme", false, getContext()));
        mSwitchCompatSetOnBoot.setChecked(Utils.getBoolean("set_on_boot",false,getContext()));
        mSwitchCompatForceEng.setChecked(Utils.getBoolean("force_eng",true,getContext()));

        return v;
    }


}


