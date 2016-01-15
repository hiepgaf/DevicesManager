package com.hieptran.devicesmanager.fragment.tweak.cpu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hieptran.devicesmanager.R;

/**
 * Created by hieptran on 15/01/2016.
 */
public class AdvanceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.title_header_view, container, false);
    }
}
