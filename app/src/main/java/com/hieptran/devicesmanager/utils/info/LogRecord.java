package com.hieptran.devicesmanager.utils.info;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.hieptran.devicesmanager.utils.Const;

/**
 * Created by hieptran on 10/03/2016.
 */
public class LogRecord implements Const {
    SQLiteDatabase database;
    private int mTime;
    private String mAvgPower;
    private String mBatteryConsumed;
    private String mAvgVoltage;
    private String mAvgCurrent;

    public LogRecord() {
    }

    public int getmTime() {
        return mTime;
    }

    public void setmTime(int mTime) {
        this.mTime = mTime;
    }

    public String getmAvgPower() {
        return mAvgPower;
    }

    public void setmAvgPower(String mAvgPower) {
        this.mAvgPower = mAvgPower;
    }

    public String getmBatteryConsumed() {
        return mBatteryConsumed;
    }

    public void setmBatteryConsumed(String mBatteryConsumed) {
        this.mBatteryConsumed = mBatteryConsumed;
    }

    public String getmAvgVoltage() {
        return mAvgVoltage;
    }

    public void setmAvgVoltage(String mAvgVoltage) {
        this.mAvgVoltage = mAvgVoltage;
    }

    public String getmAvgCurrent() {
        return mAvgCurrent;
    }

    public void setmAvgCurrent(String mAvgCurrent) {
        this.mAvgCurrent = mAvgCurrent;
    }

    public void doCreateDatabase(String time) {
        database = SQLiteDatabase.openDatabase("log.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        String sql = "CREATE TABLE tblrecord (";
        sql += "time INTERGER, ";
        sql += "average_power TEXT, ";
        sql += "battery_consumed TEXT, ";
        sql += "average_voltage TEXT, ";
        sql += "average_current TEXT) ";
        database.execSQL(sql);
        //sql+="average_power TEXT, ";
    }

    public void doPutRecord(int time, String avg_power, String batt_consumed, String avg_vol, String avg_cur) {
        ContentValues cv = new ContentValues();
        cv.put("time", time);
        cv.put("average_power", avg_power);
        cv.put("battery_consumed", batt_consumed);
        cv.put("average_voltage", avg_vol);
        cv.put("average_current", avg_cur);
        database.insert("tblrecord", null, cv);
    }
}
