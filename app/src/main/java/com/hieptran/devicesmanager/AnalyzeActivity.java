package com.hieptran.devicesmanager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.provider.DbHelper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class AnalyzeActivity extends AppCompatActivity {
    public static boolean REAL_TIME = false;
    ArrayList<DataPoint> avg_power_db = new ArrayList<>();
    ArrayList<DataPoint> bat_db = new ArrayList<>();
    ArrayList<DataPoint> avg_vol_db = new ArrayList<>();
    ArrayList<DataPoint> avg_cur_db = new ArrayList<>();
    DataPoint[] avg_power_array;
    GraphView graph, graph_bat, graph_vol, graph_cur;
    LineGraphSeries<DataPoint> series, series1, series2, series3;
    Handler hand = new Handler();
    private int total_time;
    Runnable timer1 = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (REAL_TIME) {
                                Log.d("HiepTHb", "get moi" + Utils.getInt("time_record", 0, getApplicationContext()) + " total cu" + total_time);
                                series.appendData(new DataPoint(Utils.getInt("time_record", total_time++, getApplicationContext()), Double.parseDouble(Utils.getString("average_power", "10.0", getApplicationContext()).replace(",", "."))), true, 100000);
                                series1.appendData(new DataPoint(Utils.getInt("time_record", total_time++, getApplicationContext()), Double.parseDouble(Utils.getString("battery_consumed", "10.0", getApplicationContext()).replace(",", "."))), true, 100000);
                                series2.appendData(new DataPoint(Utils.getInt("time_record", total_time++, getApplicationContext()), Double.parseDouble(Utils.getString("average_voltage", "10.0", getApplicationContext()).replace(",", "."))), true, 100000000);
                                series3.appendData(new DataPoint(Utils.getInt("time_record", total_time++, getApplicationContext()), Double.parseDouble(Utils.getString("average_current", "10.0", getApplicationContext()).replace(",", "."))), true, 100000000);
                                graph.getViewport().setXAxisBoundsManual(true);
                                graph.getViewport().setMaxX(Utils.getInt("time_record", total_time++, getApplicationContext()) + 10);
                                graph.getViewport().setMinX(0);
                                graph_bat.getViewport().setXAxisBoundsManual(true);
                                graph_bat.getViewport().setMaxX(Utils.getInt("time_record", total_time++, getApplicationContext()) + 10);
                                graph_bat.getViewport().setMinX(0);
                                graph_vol.getViewport().setXAxisBoundsManual(true);
                                graph_vol.getViewport().setMaxX(Utils.getInt("time_record", total_time++, getApplicationContext()) + 10);
                                graph_vol.getViewport().setMinX(0);
                                graph_cur.getViewport().setXAxisBoundsManual(true);
                                graph_cur.getViewport().setMaxX(Utils.getInt("time_record", total_time++, getApplicationContext()) + 10);
                                graph_cur.getViewport().setMinX(0);
                            }
                        }
                    });

                }
            }).start();
            hand.postDelayed(timer1, 1000);
        }
    };
    private ArrayList<Double> avg_power;
    private ArrayList<Double> power_consumed;
    private ArrayList<Double> avg_vol;
    private ArrayList<Double> avg_cur;
    private String table_name;
    private DbHelper mDBHelper;

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
        for (int i = 0; i < total_time; i++) {
            avg_power_db.add(i, new DataPoint(i, avg_power.get(i)));
            bat_db.add(i, new DataPoint(i, power_consumed.get(i)));
            avg_vol_db.add(i, new DataPoint(i, avg_vol.get(i)));
            avg_cur_db.add(i, new DataPoint(i, avg_cur.get(i)));
        }
        avg_power_array = new DataPoint[total_time];
        doGraph();
        // new updateView().execute();
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

    private void doGraph() {
        graph = (GraphView) findViewById(R.id.graph_power);
        graph_bat = (GraphView) findViewById(R.id.graph_bat);
        graph_vol = (GraphView) findViewById(R.id.graph_vol);
        graph_cur = (GraphView) findViewById(R.id.graph_cur);


        series = new LineGraphSeries<>(avg_power_db.toArray(avg_power_array));
        series1 = new LineGraphSeries<>(bat_db.toArray(new DataPoint[total_time]));
        series2 = new LineGraphSeries<>(avg_vol_db.toArray(new DataPoint[total_time]));
        series3 = new LineGraphSeries<>(avg_cur_db.toArray(new DataPoint[total_time]));

       /* series = new LineGraphSeries<>();
        series1 = new LineGraphSeries<>();
        series2 = new LineGraphSeries<>();
        series3 = new LineGraphSeries<>();*/

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

    @Override
    protected void onResume() {
        super.onResume();
        if (REAL_TIME)
            hand.post(timer1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hand.removeCallbacks(timer1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hand.removeCallbacks(timer1);

    }

    private void resetData() {
        avg_power = new ArrayList<>();
        power_consumed = new ArrayList<>();
        avg_vol = new ArrayList<>();
        avg_cur = new ArrayList<>();

        mDBHelper = new DbHelper(this);
        getDatabase();
        for (int i = 0; i < total_time; i++) {
            avg_power_db.add(i, new DataPoint(i, avg_power.get(i)));
            bat_db.add(i, new DataPoint(i, power_consumed.get(i)));
            avg_vol_db.add(i, new DataPoint(i, avg_vol.get(i)));
            avg_cur_db.add(i, new DataPoint(i, avg_cur.get(i)));
        }
        series.resetData(avg_power_db.toArray(new DataPoint[total_time]));
        series1.resetData(bat_db.toArray(new DataPoint[total_time]));
        series2.resetData(avg_vol_db.toArray(new DataPoint[total_time]));
        series3.resetData(avg_cur_db.toArray(new DataPoint[total_time]));
    }

    class updateView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {
                    resetData();
                    Thread.sleep(1000);
                } catch (Exception ex) {

                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
