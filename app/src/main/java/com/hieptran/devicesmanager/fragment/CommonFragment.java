package com.hieptran.devicesmanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hieptran.devicesmanager.MainActivity;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

/**
 * Created by hieptran on 09/01/2016.
 */
public abstract class CommonFragment extends Fragment implements MainActivity.OnBackButtonListener {
    private static final String TAG = " CommonFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.showLog(TAG,"onCreate");
    }
    public ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }
    public void onViewCreated() {
    }
    public void onViewCreated(View view, Bundle saved) {
        super.onViewCreated(view, saved);
    }
}
