package com.hieptran.devicesmanager.fragment.tweak.rom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
/**
 * Created by hiepth on 30/03/2016.
 */
public class StatusbarFragment extends Fragment implements IXposedHookLoadPackage{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
            if(loadPackageParam.equals("com.android.systemui")) {
                findAndHookMethod("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "updateClock", new XC_MethodHook() {


                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d("HiepTHB","set STTT");
                        TextView tv = (TextView) param.thisObject;
                        String text = tv.getText().toString();
                        tv.setText(text + " :)");
                        tv.setTextColor(Color.RED);
                    }
                });

                }
            }
    }

