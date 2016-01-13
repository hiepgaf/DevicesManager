package com.hieptran.devicesmanager.fragment.tweak.cpu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.CircleChart;
import com.hieptran.devicesmanager.common.cpuspy.CpuSpyApp;
import com.hieptran.devicesmanager.common.cpuspy.CpuStateMonitor;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HiepTran on 1/12/2016.
 */
public class CPUTimeTable extends Fragment implements Const{

    private CpuSpyApp _app = null;
    LinearLayout mainView;
    // Default View
    private LinearLayout _uiStatesView = null;
    private TextView _uiAdditionalStates = null;
    private TextView _uiTotalStateTime = null;
    private TextView _uiHeaderAdditionalStates = null;
    private TextView _uiHeaderTotalStateTime = null;
    // Default View
    private LinearLayout _uiStatesView_big = null;
    private TextView _uiAdditionalStates_big = null;
    private TextView _uiTotalStateTime_big = null;
    private TextView _uiHeaderAdditionalStates_big = null;
    private TextView _uiHeaderTotalStateTime_big = null;
    /**
     * whether or not we're updating the data in the background
     */
    private boolean _updatingData = false;

    /**
     * @return A nicely formatted String representing tSec seconds
     */
    private static String sToString(long tSec) {
        long h = (long) Math.floor(tSec / (60 * 60));
        long m = (long) Math.floor((tSec - h * 60 * 60) / 60);
        long s = tSec % 60;
        String sDur;
        sDur = h + ":";
        if (m < 10)
            sDur += "0";
        sDur += m + ":";
        if (s < 10)
            sDur += "0";
        sDur += s;

        return sDur;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View scroll_main = inflater.inflate(R.layout.scroll_tmp, container, false);
        scroll_main.setBackgroundColor(getResources().getColor(R.color.main_background_dark));
        mainView = (LinearLayout) scroll_main.findViewById(R.id.main_view_cpu_time);
        View v_default = inflater.inflate(R.layout.cpu_time_table, container, false);
        v_default.setBackgroundColor(getResources().getColor(R.color.item_background_dark));

        _app = new CpuSpyApp();
        findViews(v_default);
       mainView.addView(v_default);
        LayoutInflater inf = LayoutInflater.from(getActivity());
        LinearLayout v_big = (LinearLayout) inf.inflate(
                R.layout.cpu_time_table, container, false);
        v_big.setBackgroundColor(getResources().getColor(R.color.item_background_dark));

        if(CPU.isBigCluster()) {

           // View v_big = inflater.inflate(R.layout.cpu_time_table, container, false);
            findViews_big(v_big);
            mainView.addView(v_big);

        }

        // see if we're updating data during a config change (rotate screen)
        if (savedInstanceState != null) {
            _updatingData = savedInstanceState.getBoolean("updatingData");
        }
        return scroll_main;
    }

    /**
     * When the activity is about to change orientation
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("updatingData", _updatingData);
    }

    /**
     * Update the view when the application regains focus
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void findViews(View v) {
        _uiStatesView = (LinearLayout) v.findViewById(R.id.ui_states_view);
        _uiAdditionalStates = (TextView) v.findViewById(
                R.id.ui_additional_states);
        _uiHeaderAdditionalStates = (TextView) v.findViewById(
                R.id.ui_header_additional_states);
        _uiHeaderTotalStateTime = (TextView) v.findViewById(
                R.id.ui_header_total_state_time);
        _uiTotalStateTime = (TextView) v.findViewById(R.id.ui_total_state_time);

    }
    private void findViews_big(View v) {
            _uiStatesView_big = (LinearLayout) v.findViewById(R.id.ui_states_view);
            _uiAdditionalStates_big = (TextView) v.findViewById(
                    R.id.ui_additional_states);
            _uiHeaderAdditionalStates_big = (TextView) v.findViewById(
                    R.id.ui_header_additional_states);
            _uiHeaderTotalStateTime_big = (TextView) v.findViewById(
                    R.id.ui_header_total_state_time);
            _uiTotalStateTime_big = (TextView) v.findViewById(R.id.ui_total_state_time);
    }

    /**
     * Generate and update all UI elements
     */
    public void updateView() {
        /** Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing) */
        CpuStateMonitor monitor = _app.getCpuStateMonitor();
        _uiStatesView.removeAllViews();
        List<String> extraStates = new ArrayList<String>();
        for (CpuStateMonitor.CpuState state : monitor.getStates()) {
            if (state.duration > 0) {
                generateStateRow(state, _uiStatesView);
            } else {
                if (state.freq == 0) {
                    extraStates.add("Deep Sleep");
                } else {
                    extraStates.add(state.freq / 1000 + " MHz");
                }
            }
        }

        // show the red warning label if no states found
        if (monitor.getStates().size() == 0) {
            _uiHeaderTotalStateTime.setVisibility(View.GONE);
            _uiTotalStateTime.setVisibility(View.GONE);
            _uiStatesView.setVisibility(View.GONE);
        }

        // update the total state time
        long totTime = monitor.getTotalStateTime() / 100;
        _uiTotalStateTime.setText(sToString(totTime));

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            String str = "";

            for (String s : extraStates) {
                if (n++ > 0)
                    str += ", ";
                str += s;
            }

            _uiAdditionalStates.setVisibility(View.VISIBLE);
            _uiHeaderAdditionalStates.setVisibility(View.VISIBLE);
            _uiAdditionalStates.setText(str);
        } else {
            _uiAdditionalStates.setVisibility(View.GONE);
            _uiHeaderAdditionalStates.setVisibility(View.GONE);
        }

    }
    public void updateView_big() {
        /** Get the CpuStateMonitor from the app, and iterate over all states,
         * creating a row if the duration is > 0 or otherwise marking it in
         * extraStates (missing) */
        CpuStateMonitor monitor = _app.getCpuStateMonitor();
        _uiStatesView_big.removeAllViews();
        List<String> extraStates = new ArrayList<>();
        for (CpuStateMonitor.CpuState state : monitor.getStates_big()) {
            if (state.duration > 0) {
                generateStateRow_big(state, _uiStatesView_big);
            } else {
                if (state.freq == 0) {
                    extraStates.add("Deep Sleep");
                } else {
                    extraStates.add(state.freq / 1000 + " MHz");
                }
            }
        }

        // show the red warning label if no states found
        if (monitor.getStates().size() == 0) {
            _uiHeaderTotalStateTime_big.setVisibility(View.GONE);
            _uiTotalStateTime_big.setVisibility(View.GONE);
            _uiStatesView_big.setVisibility(View.GONE);
        }

        // update the total state time
        long totTime = monitor.getTotalStateTime_big() / 100;
        _uiTotalStateTime_big.setText(sToString(totTime));

        // for all the 0 duration states, add the the Unused State area
        if (extraStates.size() > 0) {
            int n = 0;
            String str = "";

            for (String s : extraStates) {
                if (n++ > 0)
                    str += ", ";
                str += s;
            }

            _uiAdditionalStates_big.setVisibility(View.VISIBLE);
            _uiHeaderAdditionalStates_big.setVisibility(View.VISIBLE);
            _uiAdditionalStates_big.setText(str);
        } else {
            _uiAdditionalStates_big.setVisibility(View.GONE);
            _uiHeaderAdditionalStates_big.setVisibility(View.GONE);
        }

    }
    /**
     * Attempt to update the time-in-state info
     */
    public void refreshData() {
        if (!_updatingData) {
            new RefreshStateDataTask().execute((Void) null);
        }
    }

    /**
     * @return a View that correpsonds to a CPU freq state row as specified
     * by the state parameter
     */
    private View generateStateRow(CpuStateMonitor.CpuState state, ViewGroup parent) {
        // inflate the XML into a view in the parent
        LayoutInflater inf = LayoutInflater.from(getActivity());
        LinearLayout theRow = (LinearLayout) inf.inflate(
                R.layout.state_row, parent, false);

        // what percetnage we've got
        CpuStateMonitor monitor = _app.getCpuStateMonitor();
        float per = (float) state.duration * 100 /
                monitor.getTotalStateTime();

        // state name
        String sFreq;
        if (state.freq == 0) {
            sFreq = "Deep Sleep";
        } else {
            sFreq = state.freq / 1000 + " MHz";
        }

        // duration
        long tSec = state.duration / 100;
        String sDur = sToString(tSec);

        // map UI elements to objects
        TextView freqText = (TextView) theRow.findViewById(R.id.ui_freq_text);
        TextView durText = (TextView) theRow.findViewById(
                R.id.ui_duration_text);
        CircleChart perText = (CircleChart) theRow.findViewById(
                R.id.ui_percentage_text);


        // modify the row
        freqText.setText(sFreq);
        perText.setMax(100);
        perText.setProgress((int) per);
        durText.setText(sDur);


        // add it to parent and return
        parent.addView(theRow);
        return theRow;
    }
    private View generateStateRow_big(CpuStateMonitor.CpuState state, ViewGroup parent) {
        // inflate the XML into a view in the parent
        LayoutInflater inf = LayoutInflater.from(getActivity());
        LinearLayout theRow = (LinearLayout) inf.inflate(
                R.layout.state_row, parent, false);

        // what percetnage we've got
        CpuStateMonitor monitor = _app.getCpuStateMonitor();
        float per = (float) state.duration * 100 /
                monitor.getTotalStateTime_big();


        // state name
        String sFreq;
        if (state.freq == 0) {
            sFreq = "Deep Sleep";
        } else {
            sFreq = state.freq / 1000 + " MHz";
        }

        // duration
        long tSec = state.duration / 100;
        String sDur = sToString(tSec);

        TextView freqText = (TextView) theRow.findViewById(R.id.ui_freq_text);
        TextView durText = (TextView) theRow.findViewById(
                R.id.ui_duration_text);
        CircleChart perText = (CircleChart) theRow.findViewById(
                R.id.ui_percentage_text);


        // modify the row
        freqText.setText(sFreq);
        perText.setMax(100);
        perText.setProgress((int) per);
        durText.setText(sDur);



        // add it to parent and return
        parent.addView(theRow);
        return theRow;
    }
    /**
     * logging
     */
    private void log(String s) {
        Log.d(TAG, s);
    }

    /**
     * Keep updating the state data off the UI thread for slow devices
     */
    protected class RefreshStateDataTask extends AsyncTask<Void, Void, Void> {

        /**
         * Stuff to do on a seperate thread
         */
        @Override
        protected Void doInBackground(Void... v) {
            CpuStateMonitor monitor = _app.getCpuStateMonitor();
            try {
                monitor.updateStates();
                if(CPU.isBigCluster())
                    monitor.updateStates_Big();

            } catch (CpuStateMonitor.CpuStateMonitorException e) {
                Log.e(TAG, "Problem getting CPU states");
            }

            return null;
        }

        /**
         * Executed on the UI thread right before starting the task
         */
        @Override
        protected void onPreExecute() {
            log("starting data update");
            _updatingData = true;
        }

        /**
         * Executed on UI thread after task
         */
        @Override
        protected void onPostExecute(Void v) {
            log("finished data update");
            _updatingData = false;
            updateView();
            if(CPU.isBigCluster()) updateView_big();
        }
    }
}
