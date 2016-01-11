package com.hieptran.devicesmanager.fragment.tweak.cpu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.tweak.CPU;


/**
 * Created by hieptran on 11/01/2016.
 */
public class CPUFragment extends Fragment implements View.OnClickListener {
    private SwitchCompat[] mCoreCheckBox;
    private ProgressBar[] mCoreProgressBar;
    private AppCompatTextView[] mCoreUsageText;
    private AppCompatTextView[] mCoreFreqText;
    private TextView[] mCoreLable;

    private LinearLayout coreInit(LayoutInflater inflater, ViewGroup container) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        mCoreCheckBox = new SwitchCompat[CPU.getBigCoreRange().size()];
        mCoreProgressBar = new ProgressBar[mCoreCheckBox.length];
        mCoreUsageText = new AppCompatTextView[mCoreCheckBox.length];
        mCoreLable = new TextView[mCoreCheckBox.length];
        mCoreFreqText = new AppCompatTextView[mCoreCheckBox.length];
        for (int i = 0; i < mCoreCheckBox.length; i++) {
            View view = inflater.inflate(R.layout.coreview, container, false);

            mCoreCheckBox[i] = (SwitchCompat) view.findViewById(R.id.core_checkbox);
            mCoreLable[i] = (TextView) view.findViewById(R.id.core_lable);
            mCoreLable[i].setText(getString(R.string.core, i + 1));
            mCoreCheckBox[i].setOnClickListener(this);

            mCoreProgressBar[i] = (ProgressBar) view.findViewById(R.id.progressbar);
            mCoreProgressBar[i].setMax(CPU.getFreqs().size());

            mCoreUsageText[i] = (AppCompatTextView) view.findViewById(R.id.usage);

            mCoreFreqText[i] = (AppCompatTextView) view.findViewById(R.id.freq);

            layout.addView(view);
        }
        return layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return coreInit(inflater, container);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }
}
