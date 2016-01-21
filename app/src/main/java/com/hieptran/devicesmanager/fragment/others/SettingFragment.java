package com.hieptran.devicesmanager.fragment.others;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.hieptran.devicesmanager.MainActivity;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Utils;
import com.leaking.slideswitch.SlideSwitch;

/**
 * Created by hiepth on 21/01/2016.
 */
public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment,container, false);
        SlideSwitch materialAnimatedSwitch = (SlideSwitch) v.findViewById(R.id.switch_dark);
        materialAnimatedSwitch.setState( Utils.DARK);
        materialAnimatedSwitch.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                Utils.DARK = true;
                startActivity(new Intent(getActivity(), MainActivity.class));
            }

            @Override
            public void close() {
                Utils.DARK = false;
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return v;
    }
}
