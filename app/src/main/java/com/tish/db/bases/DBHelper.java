package com.tish.db.bases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ua_finance.db";
    private static final int DB_VERSION = 1;

    private static DBHelper dbHelper = null;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper newInstance(Context appContext) {
        if (dbHelper == null)
            dbHelper = new DBHelper(appContext);
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.Costs.CREATE_TABLE_COSTS);
        //db.execSQL(DBContract.Incomes.CREATE_TABLE_INCOMES);
        db.execSQL(DBContract.Geolocations.CREATE_TABLE_GEOLOCATIONS);
        db.execSQL(DBContract.Accounts.CREATE_TABLE_ACCOUNTS);
        //db.execSQL(DBContract.Photos.CREATE_TABLE_PHOTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
