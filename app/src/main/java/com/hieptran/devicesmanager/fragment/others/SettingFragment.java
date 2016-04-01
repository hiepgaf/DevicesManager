package com.hieptran.devicesmanager.fragment.others;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hieptran.commonlibs.andoid.common.contrib.Util;
import com.example.hieptran.commonlibs.android.common.kernelutils.NativeKernelWakelock;
import com.hieptran.devicesmanager.MainActivity;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hiepth on 21/01/2016.
 */
public class SettingFragment extends Fragment {
    ListViewCompat mSettingList;
    private ArrayList<String> mLable,mKey;
    private SettingAdapter mSettingAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment,container,false);
        mSettingList = (ListViewCompat) v.findViewById(R.id.settings_lv);
        mLable = new ArrayList<String>() ;
        mKey = new ArrayList<String>() ;

        mLable.add(getString(R.string.set_on_boot_lable));
        mLable.add(getString(R.string.force_english));
        mLable.add(getString(R.string.dark_theme));
        mKey.add("set_on_boot");
        mKey.add("force_eng");
        mKey.add("dark_theme");
        mLable.add(getString(R.string.blur_effect));
        mKey.add("blur_navi");
        mSettingAdapter = new SettingAdapter(mLable,mKey,getContext());
        mSettingList.setAdapter(mSettingAdapter);
        mSettingAdapter.notifyDataSetChanged();
        return v;
    }
}

 class SettingAdapter extends BaseAdapter implements SwitchCompat.OnCheckedChangeListener{
     private ArrayList<String> mTitleList, mKeyList;
     private  ArrayList<Boolean> mValueList;
     private Context mContext;
     private TextView mTitle;
     private SwitchCompat switchCompat;
    @Override
    public int getCount() {
        return mTitleList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.switch_settings_item,null);
        if(!Utils.getBoolean(mKeyList.get(0),false,mContext))
            v.setBackgroundResource(R.drawable.bg_shadow_white);
        else             v.setBackgroundResource(R.drawable.bg_shadow_);

        mTitle = (TextView) v.findViewById(R.id.description_view);
        mTitle.setText(mTitleList.get(position));
        switchCompat = (SwitchCompat) v.findViewById(R.id.switchcompat_view) ;
        switchCompat.setChecked(Utils.getBoolean(mKeyList.get(position), false, mContext));

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (position != 0) {
                    Utils.saveBoolean(mKeyList.get(position), isChecked, mContext);
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                }
                else
                Toast.makeText(mContext, "Dumplog Services will be started on Boot event", Toast.LENGTH_LONG).show();

            }
        });

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public SettingAdapter(ArrayList<String> lableList,ArrayList<String> mKeyList,Context context) {
        this.mContext = context;
        this.mTitleList = lableList;
        this.mKeyList = mKeyList;
    }

     @Override
     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

     }
 }
















       /* extends Fragment implements Switch.OnCheckedChangeListener {
    private Switch mSwitchCompatSetOnBoot, mSwitchCompatForceEng, mSwitchCompatDark;
    boolean isInit =false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment,container, false);
        mSwitchCompatSetOnBoot = (Switch) v.findViewById(R.id.switchcompat_view);
        mSwitchCompatForceEng = (Switch) v.findViewById(R.id.switchcompat_view_lang);
        mSwitchCompatDark = (Switch) v.findViewById(R.id.switch_dark);
        mSwitchCompatDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("dark_theme",isChecked,getContext());
             //   getContext().startActivity(new Intent(getActivity().getApplication(),MainActivity.class));
            }
        });
        mSwitchCompatForceEng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("force_eng",isChecked,getContext());
            }
        });
        mSwitchCompatSetOnBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("set_on_boot",isChecked,getContext());

            }
        });
        mSwitchCompatDark.setOnCheckedChangeListener(null);
        mSwitchCompatForceEng.setOnCheckedChangeListener(null);
        mSwitchCompatSetOnBoot.setOnCheckedChangeListener(null);
        mSwitchCompatDark.setChecked(Utils.getBoolean("dark_theme", false, getContext()));
        mSwitchCompatSetOnBoot.setChecked(Utils.getBoolean("set_on_boot",false,getContext()));
        mSwitchCompatForceEng.setChecked(Utils.getBoolean("force_eng",true,getContext()));
        mSwitchCompatDark.setOnCheckedChangeListener(this);
        mSwitchCompatForceEng.setOnCheckedChangeListener(this);
        mSwitchCompatSetOnBoot.setOnCheckedChangeListener(this);
isInit =false;
        return v;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isInit) isInit =true;
        if(isInit) {
            if (buttonView == mSwitchCompatForceEng){
                Utils.saveBoolean("force_eng", isChecked, getContext());
                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
          //      Log.d("HiepTHb", "Onchecked-mSwitchCompatDark");
            }
            if (buttonView == mSwitchCompatSetOnBoot){
               // Utils.saveBoolean("dark_theme",true,getContext());
               // getActivity().startActivity(new Intent(getContext(),MainActivity.class));
            }
            if (buttonView == mSwitchCompatDark) {
                Utils.saveBoolean("dark_theme", isChecked, getContext());
                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
            }
        }
    }
}
*/

