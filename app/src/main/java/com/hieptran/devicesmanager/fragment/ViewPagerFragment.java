package com.hieptran.devicesmanager.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.PagerAdapter;
import com.hieptran.devicesmanager.common.SwipeableViewPager;
import com.hieptran.devicesmanager.common.ViewPagerItemCommon;

import java.util.ArrayList;
import java.util.List;

import io.karim.MaterialTabs;

/**
 * Created by hieptran on 09/01/2016.
 */
public class ViewPagerFragment extends CommonFragment {
    protected LayoutInflater inflater;
    protected ViewGroup container;
    private PagerAdapter adapter;
    private SwipeableViewPager mViewPager;
    protected MaterialTabs mTabs;
    private final List<ViewPagerItemCommon> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  final @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        items.clear();
        View view = getParentView();
        mViewPager = (SwipeableViewPager) view.findViewById(R.id.view_pager);
        mTabs = (MaterialTabs) view.findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onSwipe(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                adapter = new PagerAdapter(getChildFragmentManager(), items);
                try {
                    if (isAdded()) preInit(savedInstanceState);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (isAdded()) init(savedInstanceState);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mViewPager.setAdapter(adapter);
                mViewPager.setOffscreenPageLimit(items.size());
                mViewPager.setCurrentItem(0);
                try {
                    if (isAdded()) postInit(savedInstanceState);

                    mTabs.setTextColorSelected(getResources().getColor(R.color.white));
                    mTabs.setTextColorUnselected(getResources().getColor(R.color.textcolor_dark));
                    if (getCount() > 1)
                        mTabs.setIndicatorColor(getResources().getColor(R.color.white));
                    else
                        mTabs.setIndicatorColor(getResources().getColor(R.color.color_primary));
                    mTabs.setViewPager(mViewPager);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
      return view;
    }
    public View getParentView() {
        return inflater.inflate(R.layout.view_pager, container, false);
    }
    public void preInit(Bundle savedInstanceState) {
    }

    public void init(Bundle savedInstanceState) {
    }

    public void postInit(Bundle savedInstanceState) {
    }

    public void onSwipe(int page) {
    }

    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item);
    }

    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    public void allowSwipe(boolean swipe) {
        mViewPager.allowSwipe(swipe);
    }

    public void addFragment(ViewPagerItemCommon item) {
        if (items.indexOf(item) < 0) {
            items.add(item);
            adapter.notifyDataSetChanged();
        }
        if (getCount() > 1) {
            Activity activity;
            if ((activity = getActivity()) != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTabs != null)
                            mTabs.setIndicatorColor(getResources().getColor(R.color.white));
                    }
                });
        }
    }

    public int getCount() {
        return items.size();
    }

    public Fragment getFragment(int position) {
        return items.get(position).getFragment();
    }

    public void showTabs(boolean visible) {
        mTabs.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public List<ViewPagerItemCommon> getItems() {
        return items;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
