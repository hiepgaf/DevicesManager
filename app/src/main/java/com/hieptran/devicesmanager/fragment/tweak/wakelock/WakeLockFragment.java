package com.hieptran.devicesmanager.fragment.tweak.wakelock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.hieptran.commonlibs.android.common.kernelutils.WakeupSources;
import com.example.hieptran.commonlibs.android.common.privateapiproxies.StatElement;
import com.hieptran.devicesmanager.R;

import java.util.ArrayList;

/**
 * Created by HiepTran on 3/26/2016.
 */
public class WakeLockFragment extends Fragment {
    private ListViewCompat mWakeLockListView;
    private ArrayAdapter<String> mWakeLockAdapter;
    private ProgressDialog mProgressDialog;
    private ArrayList<StatElement> mWakeupSource;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wakelocks_fragment,container,false);
        mWakeLockAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,new ArrayList<String>());
        mWakeLockListView = (ListViewCompat) v.findViewById(R.id.wakelock_lv);
        mWakeLockListView.setAdapter(mWakeLockAdapter);
        mProgressDialog = new ProgressDialog(getContext());
        mWakeupSource= WakeupSources.parseWakeupSources(getContext());
        mProgressDialog.setMessage("Getting Data");
        mProgressDialog.setCanceledOnTouchOutside(false);
      //  updateData();
        new CustomAsync().execute();
      //  Log.d("HiepTHb", "");
        return v;
    }
    private void updateData() {
        for (int i = 0; i < mWakeupSource.size(); i++) {

            mWakeLockAdapter.add(mWakeupSource.get(i).getName());
            //Log.d("HiepTHb", WakeupSources.parseWakeupSources(getContext()).get(i).toString());
        }
        mWakeLockAdapter.setNotifyOnChange(true);
    }
    class CustomAsync extends AsyncTask<Void,String,Void> {
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressDialog.dismiss();

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mWakeLockAdapter.add(values[0]);
            mWakeLockAdapter.setNotifyOnChange(true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < mWakeupSource.size(); i++) {
                publishProgress(mWakeupSource.get(i).getName());
              //  Log.d("HiepTHb", WakeupSources.parseWakeupSources(getContext()).get(i).toString());
            }
            return null;
        }
    }
}
