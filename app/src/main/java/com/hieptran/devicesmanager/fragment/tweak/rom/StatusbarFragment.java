package com.hieptran.devicesmanager.fragment.tweak.rom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by hiepth on 30/03/2016.
 */
public class StatusbarFragment extends Fragment implements IXposedHookLoadPackage,IXposedHookInitPackageResources, AdapterView.OnItemSelectedListener{
    Spinner mSpinnerClockPos;
    protected boolean inhibit_spinner = true;
    InitPackageResourcesParam initPackageResourcesParam;
    ArrayAdapter<String> mArrayAdapterClockPos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rom_tweak_fragment, container, false);
        mSpinnerClockPos = (Spinner) v.findViewById(R.id.clock_pos);
        mArrayAdapterClockPos = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        mArrayAdapterClockPos.add("Left");
        mArrayAdapterClockPos.add("Center");
        mArrayAdapterClockPos.add("Right");
        mSpinnerClockPos.setAdapter(mArrayAdapterClockPos);
        mArrayAdapterClockPos.setNotifyOnChange(true);
        Utils.saveInt("clock_pos", 0, getContext());
        mSpinnerClockPos.setOnItemSelectedListener(this);
        return v;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
            if(loadPackageParam.equals("com.android.systemui")) {
                ClockPosition.initHandleLoadPackage(loadPackageParam.classLoader);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), ""+Utils.getInt("clock_pos", 1, getContext()),Toast.LENGTH_LONG).show();
            Utils.saveInt("clock_pos", 0, getContext());
            type = position;


//            mSflashDialog = new MaterialDialog(getContext())
//                    .setTitle("Set Governor")
//                    .setMessage("Change governor to " + ls_avai_gov.get(position) + "?")
//                    .setPositiveButton("OK", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mState = 1;
//                            setStaticView();
//                            getActivity().invalidateOptionsMenu();
//                            //   updateView();
//                            UpdateView2();
//                            mSflashDialog.dismiss();
//
//                        }
//                    })
//                    .setNegativeButton("CANCEL", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mSflashDialog.dismiss();
//                        }
//                    });
//            mSflashDialog.show();
//            if (mCores == CPU.getBigCore())
//                CPU.setGovernor(adap_avai_gov.getItem(position), getActivity());
//            else
//                CPU.setGovernor(CommandControl.CommandType.CPU_LITTLE, adap_avai_gov.getItem(position), getActivity());


    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {



        }

int type=1;
    @Override
    public void handleInitPackageResources(InitPackageResourcesParam initPackageResourcesParam) throws Throwable {

        ClockPosition.initPackageResources(initPackageResourcesParam,  Utils.getInt("clock_pos", 1, getContext()));
    }
}

