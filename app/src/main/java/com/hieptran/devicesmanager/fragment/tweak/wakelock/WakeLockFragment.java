package com.hieptran.devicesmanager.fragment.tweak.wakelock;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hieptran.commonlibs.andoid.common.contrib.Util;
import com.example.hieptran.commonlibs.android.common.kernelutils.NativeKernelWakelock;
import com.example.hieptran.commonlibs.android.common.kernelutils.WakeupSources;
import com.example.hieptran.commonlibs.android.common.privateapiproxies.StatElement;
import com.example.hieptran.commonlibs.android.common.privateapiproxies.Wakelock;
import com.example.hieptran.commonlibs.android.common.utils.DateUtils;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.SampleDialogFragment;
import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;

/**
 * Created by HiepTran on 3/26/2016.
 */
public class WakeLockFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListViewCompat mWakeLockListView;
    private WakelockAdapter mWakeLockAdapter;
    private ProgressDialog mProgressDialog;
    private ArrayList<NativeKernelWakelock> mWakeupSource,mWakeupSourceFinal;
    static Context mContext;
    SampleDialogFragment fragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wakelocks_fragment,container,false);
        mWakeLockListView = (ListViewCompat) v.findViewById(R.id.wakelock_lv);
        mWakeLockListView.setOnItemClickListener(this);
        mWakeLockListView.setAdapter(mWakeLockAdapter);

        fragment
                = SampleDialogFragment.newInstance(
                5,
                5,
                false,
                false,
                "", getString(R.string.getting_data)
        );


        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Getting Data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        new CustomAsync().execute();
        mContext = getContext();
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
         //   Log.d("HiepTHb1", WakeupSources.parseWakeupSources(getContext()).get(i).getName()+"-"+WakeupSources.parseWakeupSources(getContext()).get(i).getTtlTime() +"");
        }
       // mWakeLockAdapter.setNotifyOnChange(true);
    }
    class CustomAsync extends AsyncTask<Void,String,Void> {
        @Override
        protected void onPreExecute() {
            fragment.show(getActivity().getFragmentManager(), "blur_sample");
            fragment.setmType(0);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fragment.setDismiss();
          //  mProgressDialog.dismiss();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ShowTextDialog  fragment
                = ShowTextDialog.newInstance(
                3,
                3,
                false,
                false,
                mWakeupSourceFinal.get(position).getName(),mWakeupSourceFinal.get(position).toString());
        fragment.show(getActivity().getFragmentManager(),"blur");
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
            if(!Utils.getBoolean("dark_theme",false,mContext))
                gov_itemt.setBackgroundResource(R.drawable.bg_shadow_white);
            else             gov_itemt.setBackgroundResource(R.drawable.bg_shadow_);
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
    static class ShowTextDialog extends BlurDialogFragment {

        /**
         * Bundle key used to start the blur dialog with a given scale factor (float).
         */
        private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

        /**
         * Bundle key used to start the blur dialog with a given blur radius (int).
         */
        private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

        /**
         * Bundle key used to start the blur dialog with a given dimming effect policy.
         */
        private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

        /**
         * Bundle key used to start the blur dialog with a given debug policy.
         */
        private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";

        private int mRadius;
        private float mDownScaleFactor;
        private boolean mDimming;
        private boolean mDebug;
        static String mLable = "", mValue = "";
        static int mCores;

        /**
         * Retrieve a new instance of the sample fragment.
         *
         * @param radius          blur radius.
         * @param downScaleFactor down scale factor.
         * @param dimming         dimming effect.
         * @param debug           debug policy.
         * @return well instantiated fragment.
         */
        public static ShowTextDialog newInstance(int radius,
                                                 float downScaleFactor,
                                                 boolean dimming,
                                                 boolean debug,
                                                 String file, String value) {
            ShowTextDialog fragment = new ShowTextDialog();
            Bundle args = new Bundle();

            mLable = file;
            mValue = value;
            args.putInt(
                    BUNDLE_KEY_BLUR_RADIUS,
                    radius
            );
            args.putFloat(
                    BUNDLE_KEY_DOWN_SCALE_FACTOR,
                    downScaleFactor
            );
            args.putBoolean(
                    BUNDLE_KEY_DIMMING,
                    dimming
            );
            args.putBoolean(
                    BUNDLE_KEY_DEBUG,
                    debug
            );

            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            Bundle args = getArguments();
            mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
            mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
            mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
            mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.textview_diaglog,null);


            final TextView editText = (TextView) layout.findViewById(R.id.context_msg);
            final TextView mTextView = (TextView) layout.findViewById(R.id.header_dialog);
            mTextView.setText(mLable);
            editText.setText(mValue);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(layout)
                  .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dismiss();

                      }
                  });
            AlertDialog dialog = builder.create();
            //             dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_shadow_);
            return dialog;
        }

        @Override
        protected boolean isDebugEnable() {
            return mDebug;
        }

        @Override
        protected boolean isDimmingEnable() {
            return mDimming;
        }

        @Override
        protected boolean isActionBarBlurred() {
            return true;
        }

        @Override
        protected float getDownScaleFactor() {
            return mDownScaleFactor;
        }

        @Override
        protected int getBlurRadius() {
            return mRadius;
        }
    }
}
