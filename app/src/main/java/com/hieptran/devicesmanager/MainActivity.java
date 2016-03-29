package com.hieptran.devicesmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hieptran.devicesmanager.common.SampleDialogFragment;
import com.hieptran.devicesmanager.common.SplashView;
import com.hieptran.devicesmanager.common.root.RootUtils;
import com.hieptran.devicesmanager.fragment.AboutFragment;
import com.hieptran.devicesmanager.fragment.others.SettingFragment;
import com.hieptran.devicesmanager.fragment.phoneinfo.PhoneInfoFragment;
import com.hieptran.devicesmanager.fragment.tweak.CPUTweakFragment;
import com.hieptran.devicesmanager.fragment.tweak.GOVTweakFragment;
import com.hieptran.devicesmanager.fragment.tweak.battery.BatteryInfoFragment;
import com.hieptran.devicesmanager.fragment.tweak.profile.ProfileFragment;
import com.hieptran.devicesmanager.fragment.tweak.wakelock.WakeLockFragment;
import com.hieptran.devicesmanager.services.DumpLogService;
import com.hieptran.devicesmanager.utils.Utils;

import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 500;
    public static Intent i;
    boolean pressAgain = true;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    Toolbar toolbar;
    SplashView mSplashView;
    Handler hand;
    AdView mAdView;
    AdRequest adRequest;
    private ProgressDialog progressDialog;
    TextView tv;
    String warning = "#include <std_disclaimer.h>\n" +
            "/*\n" +
            " * Your warranty is now void.\n" +
            " *\n" +
            " * I am not responsible for bricked devices, dead SD cards,\n" +
            " * thermonuclear war, or you getting fired because the alarm app failed. Please\n" +
            " * do some research if you have any concerns about features included in this ROM\n" +
            " * before flashing it! YOU are choosing to make these modifications, and if\n" +
            " * you point the finger at me for messing up your device, I will laugh at you.\n" +
            " */";
    ///Test
    //variable for counting two successive up-down events
    int clickCount = 0;
    //variable for storing the time of first click
    long startTime;
    //variable for calculating the total time
    long duration;
    //private ScrimInsetsFrameLayout mScrimInsetsFrameLayout;
    MaterialDialog mSflashDialog;
    private InterstitialAd mInterstitialAd;
    SampleDialogFragment fragment;
    Activity mActivity;
   // Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad="vi";
        if(Utils.getBoolean("force_eng",false,this)) {
            languageToLoad  = "en"; // your language

        }
        if (Utils.getBoolean("dark_theme",false,this)) {
            Log.d("HiepTHb", "Nhay vao day");
            super.setTheme(R.style.AppBaseThemeDark);
            // navigationView.setItemBackgroundResource(R.drawable.nav_text_item_bg);
            // getWindow().getDecorView().getRootView().setBackgroundColor(getResources().getColor(R.color.black));
        } else
        super.setTheme(R.style.AppBaseThemeLight);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);




        toolbar = (Toolbar) findViewById(R.id.toolbar);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.waiting_for_init));
        mActivity = this;
        //tf = Typeface.createFromAsset(getAssets(),"fonts/cour.ttf");
        // tv = (TextView) findViewById(R.id.warningmsg);
        // tv.setTypeface(tf);
        // tv.setText(warning);
        // Defined in res/values/strings.xml

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSupportActionBar(toolbar);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           // window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } else {
            //getSupportActionBar().hide();
            setSupportActionBar(toolbar);
        }

         fragment
                = SampleDialogFragment.newInstance(
                5,
                5,
                false,
                true,
                "",getString(R.string.waiting_for_init)
                );
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
     //   mSplashView = (SplashView) findViewById(R.id.splash_view);
        new Task().execute();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        int[][] state = new int[][] {
                new int[] { }, // disabled
                new int[] {android.R.attr.state_checked}, // enabled
                new int[] {android.R.attr.state_checked}, // unchecked
                new int[] {android.R.attr.state_pressed}  // pressed

        };

        int[] color = new int[] {
                Color.WHITE,
                Color.RED,
                Color.RED,
                Color.RED
        };

        ColorStateList csl = new ColorStateList(state, color);
        if (Utils.getBoolean("dark_theme",false,this)) {
            Log.d("HiepTHb", "Nhay vao day");
            navigationView.setBackgroundColor(Color.parseColor("#404040"));
            // super.setTheme(R.style.AppBaseThemeDark);
          //  navigationView.setItemTextColor(csl);
            // getWindow().getDecorView().getRootView().setBackgroundColor(getResources().getColor(R.color.black));
        }
        else {

        }
//        mAdView = (AdView) findViewById(R.id.adView);
//        adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.splash_banner_));
//
//        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        } else {
//            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
//            //startGame();
//        }
        //http://stackoverflow.com/questions/31394265/navigation-drawer-item-icon-not-showing-original-colour
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);

//For test
        i = new Intent(MainActivity.this, DumpLogService.class);
        // startService(i);
        Fragment setting = new AboutFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, setting).commit();
        setTitle("About");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(i);
    }

    @Override
    public void onBackPressed() {
        Log.d("HiepTHb", "onBackPressed");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            pressAgain = false;
            Toast.makeText(this, "press back again to leave", Toast.LENGTH_LONG).show();
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
        } else if (id == R.id.nav_battery_tw) {
            Fragment bat_tw = new BatteryInfoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, bat_tw).commit();
            setTitle(getString(R.string.battery));
        } else if (id == R.id.nav_set_pr) {
            Fragment setting = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, setting).commit();
            setTitle(getString(R.string.profile_summary));
        } else if (id == R.id.nav_wakelock) {
            Fragment setting = new WakeLockFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, setting).commit();
            setTitle(getString(R.string.wake_lock_title));
        } else if (id == R.id.nav_setting) {
            Fragment setting = new SettingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, setting).commit();
            setTitle(getString(R.string.nav_setting));
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setInterface() {
        if (drawer != null) {
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0) {
                @Override
                public void onDrawerClosed(View view) {
                    invalidateOptionsMenu();
                   // mAdView.removeAllViews();
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu();

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
        protected void onPreExecute() {
        //    fragment.show(getFragmentManager(), "blur_sample");
        //    fragment.setmType(1);
            Utils.setFullscreen(mActivity);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Check root access and busybox installation
            //  Toast.makeText(MainActivity.this,"Device rooted",Toast.LENGTH_LONG).show();
//                Toast.makeText(MainActivity.this, "Device unrooted", Toast.LENGTH_LONG).show();
            hasRoot = RootUtils.rootAccess();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Utils.exitFullscreen(mActivity);
          //  fragment.setDismiss();
          progressDialog.dismiss();

            // if (hasRoot) {
               // mSflashDialog.dismiss();
              //  mSplashView.finish();
            setInterface();
            //fragment.onDestroyView();
           // fragment.dismissAllowingStateLoss();

            //  }
            if (hasRoot) {
                Toast.makeText(MainActivity.this, "Root Mode", Toast.LENGTH_LONG).show();
                Utils.saveBoolean("rooted", true, getApplicationContext());
            } else {
                Toast.makeText(MainActivity.this, "NonRoot Mode", Toast.LENGTH_LONG).show();
                Utils.saveBoolean("rooted", false, getApplicationContext());
            }
            return;

        }
    }

}
