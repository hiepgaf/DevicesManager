package com.hieptran.devicesmanager.fragment.tweak.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;

import com.hieptran.devicesmanager.R;

import java.util.ArrayList;

/**
 * Created by hieptran on 26/02/2016.
 */
public class ProfileFragment extends Fragment{
    private ListView avaiProfile, cusProfile;
    private ArrayAdapter<String> aDapter;
    private ArrayList<String> lsProfile;
    private FloatingActionButton addProfile;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment,container,false);
        avaiProfile = (ListView) v.findViewById(R.id.avai_profiles);
        avaiProfile.setSelector(R.color.color_primary);
        cusProfile = (ListView) v.findViewById(R.id.custom_profiles);
        addProfile = (FloatingActionButton) v.findViewById(R.id.add_custom_profile);
        lsProfile = new ArrayList<>();
        lsProfile.add("Battery Level");
        lsProfile.add("Power Plug");
        lsProfile.add("Screen State");
        aDapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lsProfile);
        avaiProfile.setAdapter(aDapter);
        aDapter.setNotifyOnChange(true);
        return v;
    }
}
