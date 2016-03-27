package com.hieptran.devicesmanager.fragment.tweak.wakelock;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hieptran.commonlibs.andoid.common.contrib.Util;
import com.example.hieptran.commonlibs.android.common.kernelutils.NativeKernelWakelock;
import com.example.hieptran.commonlibs.android.common.kernelutils.WakeupSources;
import com.example.hieptran.commonlibs.android.common.privateapiproxies.StatElement;
import com.example.hieptran.commonlibs.android.common.privateapiproxies.Wakelock;
import com.example.hieptran.commonlibs.android.common.utils.DateUtils;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by HiepTran on 3/26/2016.
 */
public class WakeLockFragment extends Fragment {
    private ListViewCompat mWakeLockListView;
    private WakelockAdapter mWakeLockAdapter;
    private ProgressDialog mProgressDialog;
    private ArrayList<NativeKernelWakelock> mWakeupSource,mWakeupSourceFinal;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wakelocks_fragment,container,false);
        mWakeLockListView = (ListViewCompat) v.findViewById(R.id.wakelock_lv);
        mWakeLockListView.setAdapter(mWakeLockAdapter);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Getting Data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        new CustomAsync().execute();
      //  Log.d("HiepTHb", "");
        return v;
    }
    private void updateData() {
        mWakeupSourceFinal = new ArrayList<>();
        for (int i = 0; i < mWakeupSource.size(); i++) {
            if(mWakeupSource.get(i).getTtlTime()>100)
           //     return;
            mWakeupSourceFinal.add(mWakeupSource.get(i));// = new WakelockAdapter(mWakeupSource);
           // mWakeLockAdapter.add(mWakeupSource.get(i).getName());
            Log.d("HiepTHb1", WakeupSources.parseWakeupSources(getContext()).get(i).getName()+"-"+WakeupSources.parseWakeupSources(getContext()).get(i).getTtlTime() +"");
        }
       // mWakeLockAdapter.setNotifyOnChange(true);
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
            mWakeLockListView.setAdapter(mWakeLockAdapter);

            mWakeLockAdapter.notifyDataSetChanged();
           // updateData();

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //mWakeLockAdapter.add(values[0]);
          //  mWakeLockAdapter.setNotifyOnChange(true);

        }

        @Override
        protected Void doInBackground(Void... params) {

            mWakeupSource= WakeupSources.parseWakeupSources(getContext());
            updateData();
            mWakeLockAdapter = new WakelockAdapter(mWakeupSourceFinal);

            return null;
        }
    }
    class WakelockAdapter extends BaseAdapter {
        private TextView mLable, mValue;

        private ArrayList<NativeKernelWakelock> mLableList;
        String lable, value;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gov_itemt = inflater.inflate(R.layout.gov_item, null);
            mLable = (TextView) gov_itemt.findViewById(R.id.lable_gov_items);
            mValue = (TextView) gov_itemt.findViewById(R.id.value_gov_items);
            mLable.setText(mLableList.get(position).getName());
           // mValue.setText(Utils.formatSeconds( mLableList.get(position).getTtlTime()));
            mValue.setText((""+ mLableList.get(position).getTtlTime()));
            return gov_itemt;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mLableList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public WakelockAdapter(ArrayList<NativeKernelWakelock> lable) {
            //super(context, resource, objects);
            Collections.sort(lable, new Comparator<NativeKernelWakelock>() {
                @Override
                public int compare(NativeKernelWakelock lhs, NativeKernelWakelock rhs) {

                    return Long.compare(rhs.getTtlTime(),lhs.getTtlTime());
                }
            });
            this.mLableList = lable;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}
