package com.hieptran.devicesmanager.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hieptran.devicesmanager.R;

/**
 * Created by hiepth on 28/03/2016.
 */
public class AboutFragment extends Fragment{
    TextView tv;
    AdView mAdView;
    AdRequest adRequest;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about, container, false);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/cour.ttf");
        mAdView = (AdView) v.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        InterstitialAd   mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.splash_banner_));

        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
          //  Toast.makeText(getActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
            //startGame();
        }
//         tv = (TextView) v.findViewById(R.id.warningmsg);
//         tv.setTypeface(tf);
//         tv.setText(warning);
        return v;
    }
}
