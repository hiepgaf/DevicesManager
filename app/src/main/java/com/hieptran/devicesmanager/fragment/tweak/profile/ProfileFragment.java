package com.hieptran.devicesmanager.fragment.tweak.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

import java.util.ArrayList;

/**
 * Created by hieptran on 26/02/2016.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemClickListener, Const, View.OnClickListener, SwitchCompat.OnCheckedChangeListener{
    private ListView avaiProfile, cusProfile;
    private ArrayAdapter<String> aDapter;
    private ArrayList<String> lsProfile;
    private LinearLayout mainlayout;
    private SwitchCompat enableService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment,container,false);
        avaiProfile = (ListView) v.findViewById(R.id.avai_profiles);
        mainlayout = (LinearLayout) v.findViewById(R.id.main_frofile);
        mainlayout.setVisibility(View.GONE);
        //setViewAndChildrenEnabled(mainlayout,false);
        avaiProfile.setOnItemClickListener(this);
        avaiProfile.setSelector(R.color.color_primary);
        enableService = (SwitchCompat) v.findViewById(R.id.start_profile_service);
        enableService.setOnCheckedChangeListener(this);
        lsProfile = new ArrayList<>();
        lsProfile.add("Battery Level");
        lsProfile.add("Power Plug");
        lsProfile.add("Screen State");
        aDapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lsProfile);
        avaiProfile.setAdapter(aDapter);
        aDapter.setNotifyOnChange(true);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    Utils.sendIntent(getContext(),ACTION_PROFILE_BATTERY_LEVEL);
                    Toast.makeText(getContext(),getString(R.string.toast_set,getString(R.string.bat_lv_prf)),Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Utils.sendIntent(getContext(),ACTION_PROFILE_POWER_PLUG);
                    Toast.makeText(getContext(),getString(R.string.toast_set,getString(R.string.pw_plug_prf)),Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Utils.sendIntent(getContext(),ACTION_PROFILE_SCREEN_STATE);
                    Toast.makeText(getContext(),getString(R.string.toast_set,getString(R.string.scr_state_prf)),Toast.LENGTH_SHORT).show();
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
        if(buttonView.isChecked())
            mainlayout.setVisibility(View.VISIBLE);
        else mainlayout.setVisibility(View.GONE);
    }
}
