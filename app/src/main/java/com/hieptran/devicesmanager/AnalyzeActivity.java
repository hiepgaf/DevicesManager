package com.hieptran.devicesmanager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hieptran.devicesmanager.utils.provider.DbHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AnalyzeActivity extends AppCompatActivity {
    private int total_time;
    private ArrayList<Double> avg_power;
    private ArrayList<Double> power_consumed;
    private ArrayList<Double> avg_vol;
    private ArrayList<Double> avg_cur;
    private String table_name;
    private DbHelper mDBHelper;
    ArrayList<DataPoint> avg_power_db = new ArrayList<>();
    ArrayList<DataPoint> bat_db = new ArrayList<>();
    ArrayList<DataPoint> avg_vol_db = new ArrayList<>();
    ArrayList<DataPoint> avg_cur_db = new ArrayList<>();
    DataPoint[] avg_power_array;
    GraphView graph,graph_bat,graph_vol,graph_cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);

        //Init
        avg_power = new ArrayList<>();
        power_consumed = new ArrayList<>();
        avg_vol = new ArrayList<>();
        avg_cur = new ArrayList<>();
        mDBHelper = new DbHelper(this);
        table_name = getIntent().getStringExtra("TABLE_NAME");
        getDatabase();
        for(int i = 0;i<total_time;i++) {
            avg_power_db.add(i, new DataPoint(i,avg_power.get(i)));
            bat_db.add(i, new DataPoint(i,power_consumed.get(i)));
            avg_vol_db.add(i, new DataPoint(i,avg_vol.get(i)));
            avg_cur_db.add(i, new DataPoint(i,avg_cur.get(i)));


        }
        avg_power_array = new DataPoint[total_time];
        doGraph();
         //new updateView().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_analysis, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getDatabase() {
        avg_cur = mDBHelper.getAvgCur(table_name);
        avg_power = mDBHelper.getAvgPower(table_name);
        avg_vol = mDBHelper.getAvgVol(table_name);
        power_consumed = mDBHelper.getBatConsumed(table_name);
        total_time = mDBHelper.getAvgCur(table_name).size();
    }

    class updateView extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            while (true)doGraph();
        }

        @Override
        protected Void doInBackground(Void... params) {
while(true) {
    getDatabase();
}
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    private void doGraph() {
        graph = (GraphView) findViewById(R.id.graph_power);
        graph_bat = (GraphView) findViewById(R.id.graph_bat);
        graph_vol = (GraphView) findViewById(R.id.graph_vol);
        graph_cur = (GraphView) findViewById(R.id.graph_cur);

        /*graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(Collections.max(avg_power));
        graph.getViewport().setMinX(0);
        graph.getViewport().setScalable(true);
        graph_bat.getViewport().setYAxisBoundsManual(true);
        graph_bat.getViewport().setMaxY(Collections.max(power_consumed));
        graph_bat.getViewport().setMinX(0);
        graph_bat.getViewport().setScalable(true);

        graph_vol.getViewport().setYAxisBoundsManual(true);
        graph_vol.getViewport().setMaxY(Collections.max(avg_vol));
       graph_vol.getViewport().setMinX(0);
        graph_vol.getViewport().setScalable(true);

        graph_cur.getViewport().setYAxisBoundsManual(true);
        graph_cur.getViewport().setMaxY(Collections.max(avg_cur));
        graph_cur.getViewport().setMinX(0);
        graph_cur.getViewport().setScalable(true);*/

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(avg_power_db.toArray(avg_power_array));
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(bat_db.toArray(new DataPoint[total_time]));
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(avg_vol_db.toArray(new DataPoint[total_time]));
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(avg_cur_db.toArray(new DataPoint[total_time]));

        graph.addSeries(series);
        // legend
        series.setTitle("Average Power (mW)");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        graph_bat.addSeries(series1);
        // legend
        series1.setTitle("Battery Consumed (mAh)");
        graph_bat.getLegendRenderer().setVisible(true);
        graph_bat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        graph_vol.addSeries(series2);
        // legend
        series2.setTitle("Average Voltage (uV)");
        graph_vol.getLegendRenderer().setVisible(true);
        graph_vol.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        graph_cur.addSeries(series3);
        // legend
        series3.setTitle("Average Current (uA)");
        graph_cur.getLegendRenderer().setVisible(true);
        graph_cur.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
    }
}
