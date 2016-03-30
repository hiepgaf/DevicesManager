package com.hieptran.devicesmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by hiepth on 30/03/2016.
 */
public class Clock implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Log.d("HiepTHB", "set STTT1");
            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "updateClock", new XC_MethodHook() {


                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d("HiepTHB", "set STTT");
                    TextView tv = (TextView) param.thisObject;
                    String text = tv.getText().toString();
                    tv.setText(text + " :)");
                    tv.setTextColor(Color.RED);
                }
            });


    }
}