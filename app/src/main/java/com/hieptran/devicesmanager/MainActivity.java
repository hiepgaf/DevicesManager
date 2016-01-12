package com.hieptran.devicesmanager;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hieptran.devicesmanager.common.SplashView;
import com.hieptran.devicesmanager.common.root.RootUtils;
import com.hieptran.devicesmanager.fragment.phoneinfo.PhoneInfoFragment;
import com.hieptran.devicesmanager.fragment.tweak.CPUTweakFragment;

import me.drakeet.materialdialog.MaterialDialog;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean pressAgain = true;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    Toolbar toolbar;
    SplashView mSplashView;
    Handler hand;
    //private ScrimInsetsFrameLayout mScrimInsetsFrameLayout;
    MaterialDialog mSflashDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //mScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSplashView = (SplashView) findViewById(R.id.splash_view);
        mSflashDialog = new MaterialDialog(this)
                .setTitle("MaterialDialog")
                .setMessage("Hello world!")
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSflashDialog.dismiss();

                        new Task().execute();

                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        mSflashDialog.show();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        Log.d("HiepTHb", "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            pressAgain = false;
            Toast.makeText(this,"press back again to leave",Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        pressAgain = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            super.onBackPressed();
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if (mScrimInsetsFrameLayout != null)
            mScrimInsetsFrameLayout.setLayoutParams(getDrawerParams());*/
        if (toggle != null) toggle.onConfigurationChanged(newConfig);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        setTitle("Phone Info");
/*
        if (mScrimInsetsFrameLayout != null) drawer.closeDrawer(mScrimInsetsFrameLayout);
*/
        if (id == R.id.nav_devices_menu) {
            Fragment phone_info_fragment = new PhoneInfoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, phone_info_fragment).commit();
        } else if (id == R.id.nav_cpu_tw) {
            Fragment cpu_tw = new CPUTweakFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, cpu_tw).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setInterface() {
        if (drawer != null) {
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
            drawer.setDrawerListener(toggle);

            drawer.post(new Runnable() {
                @Override
                public void run() {
                    if (toggle != null) toggle.syncState();
                }
            });
        }
    }
    public interface OnBackButtonListener {
        boolean onBackPressed();
    }

    private class Task extends AsyncTask<Void, Void, Void> {

        private boolean hasRoot;

        @Override
        protected Void doInBackground(Void... params) {
            //init handler
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hand = new android.os.Handler();
                }
            });
            // Check root access and busybox installation
            if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();

            return null;
        }

        public final Handler getHandler() {
            return hand;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (hasRoot) {
                mSflashDialog.dismiss();

                mSplashView.finish();

                setInterface();
                return;
            }

        }
    }
}
