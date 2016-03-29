package com.hieptran.devicesmanager.fragment.others;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.hieptran.commonlibs.andoid.common.contrib.Util;
import com.hieptran.devicesmanager.MainActivity;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Utils;

/**
 * Created by hiepth on 21/01/2016.
 */
public class SettingFragment extends Fragment implements Switch.OnCheckedChangeListener {
    private Switch mSwitchCompatSetOnBoot, mSwitchCompatForceEng, mSwitchCompatDark;
    boolean isInit =false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment,container, false);
        mSwitchCompatSetOnBoot = (Switch) v.findViewById(R.id.switchcompat_view);
        mSwitchCompatForceEng = (Switch) v.findViewById(R.id.switchcompat_view_lang);
        mSwitchCompatDark = (Switch) v.findViewById(R.id.switch_dark);
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
        mSwitchCompatDark.setOnCheckedChangeListener(null);
        mSwitchCompatForceEng.setOnCheckedChangeListener(null);
        mSwitchCompatSetOnBoot.setOnCheckedChangeListener(null);
        mSwitchCompatDark.setChecked(Utils.getBoolean("dark_theme", false, getContext()));
        mSwitchCompatSetOnBoot.setChecked(Utils.getBoolean("set_on_boot",false,getContext()));
        mSwitchCompatForceEng.setChecked(Utils.getBoolean("force_eng",true,getContext()));
        mSwitchCompatDark.setOnCheckedChangeListener(this);
        mSwitchCompatForceEng.setOnCheckedChangeListener(this);
        mSwitchCompatSetOnBoot.setOnCheckedChangeListener(this);
isInit =false;
        return v;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isInit) isInit =true;
        if(isInit) {
            if (buttonView == mSwitchCompatForceEng){
                Utils.saveBoolean("force_eng", isChecked, getContext());
                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
          //      Log.d("HiepTHb", "Onchecked-mSwitchCompatDark");
            }
            if (buttonView == mSwitchCompatSetOnBoot){
               // Utils.saveBoolean("dark_theme",true,getContext());
               // getActivity().startActivity(new Intent(getContext(),MainActivity.class));
            }
            if (buttonView == mSwitchCompatDark) {
                Utils.saveBoolean("dark_theme", isChecked, getContext());
                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
            }
        }
    }
}


