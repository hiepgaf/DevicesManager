package com.hieptran.devicesmanager.fragment.tweak.battery;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by hieptran on 02/03/2016.
 */
public class PowerInfoFragment extends Fragment implements Const{
    int time;
    double batteryCapacity;
    private TextView current_now_tv,voltage_now_tv;
    private ListView voltage_now_lv;
    private ArrayList<String> voltage_now_al;
    private ArrayAdapter<String> voltage_now_ad;
    private  android.os.Handler hand;
    private List<String> lsRunningApp;
    private Runnable updateView = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Date date = new Date(1000 * (time++)); //260000 milliseconds

                            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                            Object mPowerProfile_ = null;

                            final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

                            try {
                                mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                                        .getConstructor(Context.class).newInstance(getContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS).getMethod("getAveragePower", java.lang.String.class).invoke(mPowerProfile_, "battery.capacity");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String result = sdf.format(date);
                            current_now_tv.setText("Current now: " + readCurrentNow() + "\t\t\t\t\t\t\t" + " Time: " + result);
                            voltage_now_tv.setText("Voltage now: " + readVoltageNow() + "\t\t\t\t\t\t\t\t\t" + batteryCapacity + " mah");
                            voltage_now_ad.add("\t\t\t\t\t" + readVoltageNow() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + readCurrentNow());
                            voltage_now_ad.setNotifyOnChange(true);
                            lsRunningApp.add(listRunning());

                        }
                    });
                }
            }).start();
            hand.postDelayed(updateView, 1000);
        }

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.power_info_fragment,container,false);
        current_now_tv = (TextView) v.findViewById(R.id.current_now_tv);
        voltage_now_tv = (TextView) v.findViewById(R.id.voltage_now_tv);
        voltage_now_lv = (ListView) v.findViewById(R.id.voltage_now_lv);
        lsRunningApp = new ArrayList<>();
        voltage_now_al = new ArrayList<>();
        voltage_now_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getBatteryCapacity();
                Log.d("HiepTHb",lsRunningApp.get(position));
                final MaterialDialog runDialog= new MaterialDialog(getContext());
                runDialog.setTitle("PackageRunning");
                runDialog.setMessage(lsRunningApp.get(position));
                runDialog.setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runDialog.dismiss();
                    }
                });runDialog.show();
            }
        });
        voltage_now_lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        voltage_now_ad = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,voltage_now_al);
        voltage_now_lv.setAdapter(voltage_now_ad);
        Log.d("HiepTHb",""+readCurrentNow() + "-" + readVoltageNow());
        hand = new android.os.Handler();

        return v;
    }

    private String readCurrentNow() {
        return Utils.readFileRoot(BATTERY_CURRENT_NOW);
    }

    private String readVoltageNow() {
        return Utils.readFileRoot(BATTERY_VOLTAGE_NOW);
    }

    private String listRunning() {
        StringBuilder builder = new StringBuilder();
        ActivityManager am =(ActivityManager)  getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo rap: am.getRunningAppProcesses()
             ) {
            builder.append(rap.processName+ "\n");
        }
        return builder.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hand != null) hand.post(updateView);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hand != null) hand.removeCallbacks(updateView);
    }
    public void getBatteryCapacity() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            Toast.makeText(getActivity(), batteryCapacity + " mah",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
