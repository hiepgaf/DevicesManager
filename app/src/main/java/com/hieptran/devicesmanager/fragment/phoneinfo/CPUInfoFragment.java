package com.hieptran.devicesmanager.fragment.phoneinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Tools;

/**
 * Created by hieptran on 11/01/2016.
 */
public class CPUInfoFragment extends Fragment implements Const {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.default_cardview, container, false);
        ((TextView) v.findViewById(R.id.default_view)).setText(Tools.readFile(PROC_CPUINFO, true));
        return v;

    }
}
