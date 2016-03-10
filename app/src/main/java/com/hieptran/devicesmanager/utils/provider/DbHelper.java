package com.hieptran.devicesmanager.utils.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hieptran on 10/03/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "log.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
       /* // TODO Auto-generated method stub
        String sql = "CREATE TABLE tblrecord0 (";
        sql+="time INTERGER, ";
        sql+="average_power TEXT, ";
        sql+="battery_consumed TEXT, ";
        sql+="average_voltage TEXT, ";
        sql+="average_current TEXT) ";
        db.execSQL(sql);*/
    }

    public void doCreateTable(String name) {
    /*At first you will need a Database object.Lets create it.*/
        SQLiteDatabase ourDatabase = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + name + "(";
        sql += "time INTERGER, ";
        sql += "average_power TEXT, ";
        sql += "battery_consumed TEXT, ";
        sql += "average_voltage TEXT, ";
        sql += "average_current TEXT) ";
    /*then call 'execSQL()' on it. Don't forget about using TableName Variable as tablename.*/
        ourDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public void doPutRecord(String table, int time, String avg_power, String batt_consumed, String avg_vol, String avg_cur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("time", time);
        cv.put("average_power", avg_power);
        cv.put("battery_consumed", batt_consumed);
        cv.put("average_voltage", avg_vol);
        cv.put("average_current", avg_cur);
        db.insert(table, null, cv);
    }
}
