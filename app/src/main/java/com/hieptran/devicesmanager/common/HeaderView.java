package com.hieptran.devicesmanager.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;

/**
 * Created by hiepth on 29/01/2016.
 */
public class HeaderView extends Fragment {
    private TextView header_text;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.header_cardview,container,false);
        header_text = (TextView) v.findViewById(R.id.header_view);
        return v;
    }
    public  void  setHeader(String mTile) {
        header_text.setText(mTile);
    }
}
