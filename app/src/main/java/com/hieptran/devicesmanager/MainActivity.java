package com.hieptran.devicesmanager;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hieptran.devicesmanager.common.SplashView;
import com.hieptran.devicesmanager.common.root.RootUtils;
import com.hieptran.devicesmanager.fragment.others.SettingFragment;
import com.hieptran.devicesmanager.fragment.phoneinfo.PhoneInfoFragment;
import com.hieptran.devicesmanager.fragment.tweak.CPUTweakFragment;
import com.hieptran.devicesmanager.fragment.tweak.GOVTweakFragment;
import com.hieptran.devicesmanager.fragment.tweak.profile.ProfileFragment;
import com.hieptran.devicesmanager.utils.Utils;

import me.drakeet.materialdialog.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    boolean pressAgain = true;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    Toolbar toolbar;
    SplashView mSplashView;
    Handler hand;
    AdView mAdView;
    AdRequest adRequest;
    //private ScrimInsetsFrameLayout mScrimInsetsFrameLayout;
    MaterialDialog mSflashDialog;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
         mAdView = (AdView) findViewById(R.id.adView);
         adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.splash_banner_));
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            //startGame();
        }

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           setSupportActionBar(toolbar);
           Window window = getWindow();
           window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           window.setStatusBarColor(getResources().getColor(R.color.color_primary_dark));
       }
        else {
           //getSupportActionBar().hide();
           setSupportActionBar(toolbar);
/*
           getApplication().setTheme(android.R.style.Theme_Material_NoActionBar);
*/
       }
        //mScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       // mSplashView = (SplashView) findViewById(R.id.splash_view);
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
        if(Utils.DARK) {
            drawer.setBackgroundColor(getResources().getColor(R.color.card_background_dark));
            mSflashDialog.setBackgroundResource(R.color.card_background_dark);
        }
    }


    @Override
    public void onBackPressed() {
        Log.d("HiepTHb", "onBackPressed");
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

/*
        if (mScrimInsetsFrameLayout != null) drawer.closeDrawer(mScrimInsetsFrameLayout);
*/
        if (id == R.id.nav_devices_menu) {
            Fragment phone_info_fragment = new PhoneInfoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, phone_info_fragment).commit();
            setTitle(getString(R.string.nav_devices));
        } else if (id == R.id.nav_cpu_tw) {
            Fragment cpu_tw = new CPUTweakFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, cpu_tw).commit();
            setTitle(getString(R.string.nav_cpu));

        } else if (id == R.id.nav_governor_tw) {
            Fragment gov_tw = new GOVTweakFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, gov_tw).commit();
            setTitle(getString(R.string.nav_gov));
        } else if (id == R.id.nav_setting) {
            Fragment setting = new SettingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, setting).commit();
            setTitle(getString(R.string.nav_gov));
            setTitle(getString(R.string.nav_setting));

        }
        else if (id == R.id.nav_set_pr) {
            Fragment setting = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, setting).commit();


        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setInterface() {
        if (drawer != null) {
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0){
                @Override
                public void onDrawerClosed(View view) {
                    invalidateOptionsMenu();
                    mAdView.removeAllViews();
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu();
                    mAdView.loadAd(adRequest);

                }
            };
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

          //      mSplashView.finish();
                mAdView.removeAllViews();
                setInterface();
                return;
            }

        }
    }

}
