package com.hieptran.devicesmanager.fragment.tweak.gov;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.tweak.CPUHotplug;

/**
 * Created by hieptran on 11/01/2016.
 */
public class HotPlugFragment extends Fragment implements Const {
    private LinearLayout main_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HiepTHb", CPUHotplug.isMsmHotplugActive() + "-H");
        main_view = new LinearLayout(getActivity());
        main_view.setOrientation(LinearLayout.VERTICAL);
        main_view.setPadding(16, 16, 16, 16);
        initMsmHotPlug();
        View v = inflater.inflate(R.layout.default_cardview, container, false);
        return main_view;
    }

    private void initMsmHotPlug() {

        LayoutInflater inf = LayoutInflater.from(getActivity());
        LinearLayout msmHotplugEnabledView = (LinearLayout) inf.inflate(
                R.layout.checkbox_items, main_view, false);
        ((TextView) msmHotplugEnabledView.findViewById(R.id.description_view)).setText("TESSSSSSSSSs");
        ((SwitchCompat) msmHotplugEnabledView.findViewById(R.id.switchcompat_view)).setChecked(CPUHotplug.isMsmHotplugActive());
        main_view.addView(msmHotplugEnabledView);

    }
}
