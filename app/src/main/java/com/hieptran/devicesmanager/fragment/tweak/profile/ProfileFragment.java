package com.hieptran.devicesmanager.fragment.tweak.profile;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.services.ProfileService;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tools.ScreenOffAdminReceiver;

import java.util.ArrayList;

/**
 * Created by hieptran on 26/02/2016.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemClickListener, Const, View.OnClickListener, SwitchCompat.OnCheckedChangeListener, View.OnTouchListener{
    private ListView avaiProfile, cusProfile;
    private ArrayAdapter<String> aDapter;
    private ArrayList<String> lsProfile;
    private LinearLayout mainlayout;
    private SwitchCompat enableService;
    ///Test
    //variable for counting two successive up-down events
    int clickCount = 0;
    //variable for storing the time of first click
    long startTime;
    //variable for calculating the total time
    long duration;
    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 500;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        Log.d("HiepTHb","onclick");
                        turnScreenOff(getContext());

                        clickCount++;
                        break;
                    case MotionEvent.ACTION_UP:
                        long time = System.currentTimeMillis() - startTime;
                        duration=  duration + time;
                        if(clickCount == 2)
                        {
                           /* if(duration<= MAX_DURATION)
                            {*/
                                Log.d("HiepTHb", "doubletap");
                                turnScreenOff(getContext());
                                //Toast.makeText(captureActivity.this, "double tap",Toast.LENGTH_LONG).show();
                          //  }
                            clickCount = 0;
                            duration = 0;
                            break;
                        }
                        default:break;
                }
                return false;
            }
        });
        avaiProfile = (ListView) v.findViewById(R.id.avai_profiles);
        mainlayout = (LinearLayout) v.findViewById(R.id.main_frofile);
        mainlayout.setVisibility(View.GONE);
        //setViewAndChildrenEnabled(mainlayout,false);
        avaiProfile.setOnItemClickListener(this);
        avaiProfile.setSelector(R.color.color_primary);
       // enableService = (SwitchCompat) v.findViewById(R.id.start_profile_service);
        //enableService.setOnCheckedChangeListener(this);
        lsProfile = new ArrayList<>();
        lsProfile.add("Battery Level");
        lsProfile.add("Power Plug");
        lsProfile.add("Screen State");
        aDapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lsProfile);
        avaiProfile.setAdapter(aDapter);
        aDapter.setNotifyOnChange(true);
        setHasOptionsMenu(true);


        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utils.saveInt("active_prof", position, getActivity());

        switch (position) {
            case 0:
                Utils.sendIntent(getContext(), ACTION_PROFILE_BATTERY_LEVEL);
                Toast.makeText(getContext(), getString(R.string.toast_set, getString(R.string.bat_lv_prf)), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Utils.sendIntent(getContext(), ACTION_PROFILE_POWER_PLUG);
                Toast.makeText(getContext(), getString(R.string.toast_set, getString(R.string.pw_plug_prf)), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Utils.sendIntent(getContext(), ACTION_PROFILE_SCREEN_STATE);
                Toast.makeText(getContext(), getString(R.string.toast_set, getString(R.string.scr_state_prf)), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Utils.saveBoolean("profile_service", isChecked, getContext());
        if (buttonView.isChecked()) {
            mainlayout.setVisibility(View.VISIBLE);
            getActivity().startService(new Intent(getActivity(), ProfileService.class));
        } else mainlayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                Log.d("HiepTHb","touch");
                break;
        }
        return false;
    }
    static void turnScreenOff(final Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            Log.i("HiepTHb", "Going to sleep now.");
            policyManager.lockNow();
        } else {
            Log.i("HiepTHb", "Not an admin");

        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.set_on_boot, menu);
        MenuItem item = menu.findItem(R.id.set_on_boot);
        enableService = (SwitchCompat) MenuItemCompat.getActionView(item);
        if (Utils.getBoolean("profile_service", false, getContext())) {
            enableService.setChecked(true);
            mainlayout.setVisibility(View.VISIBLE);
            getActivity().startService(new Intent(getActivity(), ProfileService.class));
            avaiProfile.setSelection(Utils.getInt("active_prof", 0, getContext()));
        }
        enableService.setOnCheckedChangeListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }
}


