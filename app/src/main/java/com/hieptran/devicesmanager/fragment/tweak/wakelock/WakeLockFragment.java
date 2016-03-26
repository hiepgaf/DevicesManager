package com.hieptran.devicesmanager.fragment.tweak.wakelock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hieptran.commonlibs.android.common.kernelutils.WakeupSources;

/**
 * Created by HiepTran on 3/26/2016.
 */
public class WakeLockFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        for (int i = 0; i < WakeupSources.parseWakeupSources(getContext()).size(); i++) {
            Log.d("HiepTHb", WakeupSources.parseWakeupSources(getContext()).get(i).toString());
        }
        Log.d("HiepTHb", "");
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
