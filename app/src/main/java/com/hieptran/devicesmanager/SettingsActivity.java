package com.hieptran.devicesmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hieptran.devicesmanager.fragment.others.SettingFragment;
import com.lb.material_preferences_library.AppCompatPreferenceActivity;
import com.lb.material_preferences_library.PreferenceActivity;
import com.lb.material_preferences_library.custom_preferences.CheckBoxPreference;

/**
 * Created by hiepth on 01/04/2016.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private boolean mSetonBoot,mForceEng,mDark;
    private CheckBoxPreference mBoxPreferenceBoot,mBoxPreferenceEng,mBoxPreferenceDark;
    SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       // getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mBoxPreferenceBoot = (CheckBoxPreference) findPreference("checkbox_on_boot");
        mBoxPreferenceDark = (CheckBoxPreference) findPreference("checkbox_dark_theme");
        mBoxPreferenceEng = (CheckBoxPreference) findPreference("checkbox_for_eng");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       if(sharedPreferences == mBoxPreferenceDark) {
           Log.d("HiepTHb", "" + mSharedPreferences.getBoolean(key, false));
       }
    }
    @Override

    protected int getPreferencesXmlId()
    {
        return R.xml.settings;
    }
}
