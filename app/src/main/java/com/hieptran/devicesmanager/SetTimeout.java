package com.hieptran.devicesmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hieptran.devicesmanager.common.CircleChart;
import com.hieptran.devicesmanager.common.view.WheelView;

import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;

public class SetTimeout extends AppCompatActivity {
    WindowManager wm;

    View mViewMain;
    LinearLayout layoutMain;
    private int mW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //http://stackoverflow.com/questions/8073803/android-multi-touch-and-type-system-overlay
        mViewMain = layoutInflater.inflate(R.layout.wheel_piker, null);



       /* mInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mViewMain, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });*/

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mW = wm.getDefaultDisplay().getWidth() * 3 / 4;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888);
       /* mWheelHours = (WheelView) findViewById(R.id.hours);
        mWheelMin = (WheelView) findViewById(R.id.minutes);
        mWheelSec = (WheelView) findViewById(R.id.seconds);
        mWheelHours.setData(getHourData());
        mWheelMin.setData(getMinData());
        mWheelSec.setData(getSecondData());*/
        wm.addView(mViewMain, params);

    }

  /*  private ArrayList<String> getSecondData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            list.add(i+"");
        }
        return list;
    }

    private ArrayList<String> getMinData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            list.add(i+"");
        }
        return list;
    }

    private ArrayList<String> getHourData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            list.add(i+"");
        }
        return list;
    }*/
}