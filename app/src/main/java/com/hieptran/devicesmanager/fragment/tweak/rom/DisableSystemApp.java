package com.hieptran.devicesmanager.fragment.tweak.rom;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hiepth on 08/04/2016.
 */
public class DisableSystemApp extends Fragment {
    private ListView mListView;
    private ArrayList<PackageInfo> mArrayList;
    private SystemAppAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_listview, container, false);
        mListView = (ListView) v.findViewById(R.id.main_listview);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        mArrayList = new ArrayList<>();
        mAdapter = new SystemAppAdapter(mArrayList, getContext(), getContext().getPackageManager());
        mListView.setAdapter(mAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new updateView().execute();
            }
        });
        new updateView().execute();
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return v;
    }

    private void update() {
        PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        for (PackageInfo pi : list) {
            try {
                ApplicationInfo ai = pm.getApplicationInfo(pi.packageName, 0);

                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    mArrayList.add(pi);
                }
            } catch (Exception e) {
            }
        }
    }

    class updateView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
            if(swipeContainer != null)
            swipeContainer.setRefreshing(false);

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            update();
            return null;
        }
    }

    class SystemAppAdapter extends BaseAdapter {

        class ViewHolder {
            private ImageView mIconApp;
            private TextView mLableApp, mPackageName;
            private AppCompatCheckBox appCompatCheckBox;
        }

        private ArrayList<PackageInfo> mArrayListSystem;
        private Context mContext;
        /* private ImageView mIconApp;
         private TextView mLableApp, mPackageName;
         private AppCompatCheckBox appCompatCheckBox;*/
        private PackageManager packageManager;

        public SystemAppAdapter(ArrayList<PackageInfo> lsSystemApp, Context context, PackageManager pm) {
            this.mArrayListSystem = lsSystemApp;
            this.mContext = context;
            this.packageManager = pm;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mArrayListSystem.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // http://stackoverflow.com/questions/4600740/getting-app-icon-in-android
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checkbox_item, null);
                holder.mIconApp = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.mLableApp = (TextView) convertView.findViewById(R.id.app_name);
                holder.mPackageName = (TextView) convertView.findViewById(R.id.package_name);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            try {
                PackageInfo item = mArrayListSystem.get(position);
                ApplicationInfo appinfo = packageManager.getApplicationInfo(item.packageName, 0);
                holder.mIconApp.setImageDrawable(getIconFromPackageName(item.packageName, mContext));
                holder.mLableApp.setText(packageManager.getApplicationLabel(appinfo));
                holder.mPackageName.setText(item.packageName);
            } catch (Exception e) {
            }
            return convertView;
        }
    }


    public static Drawable getIconFromPackageName(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            try {
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                Context otherAppCtx = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);

                int displayMetrics[] = {DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_TV};

                for (int displayMetric : displayMetrics) {
                    try {
                        Drawable d = otherAppCtx.getResources().getDrawableForDensity(pi.applicationInfo.icon, displayMetric);
                        if (d != null) {
                            return d;
                        }
                    } catch (Resources.NotFoundException e) {
//                      Log.d(TAG, "NameNotFound for" + packageName + " @ density: " + displayMetric);
                        continue;
                    }
                }

            } catch (Exception e) {
                // Handle Error here
            }
        }

        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        return appInfo.loadIcon(pm);
    }


}

