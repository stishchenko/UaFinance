package com.tish.db.bases;

import android.provider.BaseColumns;

public final class DBContract {
    private DBContract() {
    }

    public static class Costs implements BaseColumns {
        public static final String TABLE_NAME = "costs";
        public static final String COLUMN_COST_ID = "cost_id";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MARKET_NAME = "market_name";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_GEO_ID = "geo_id";

        public static final String CREATE_TABLE_COSTS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_COST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_AMOUNT + " REAL NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_MARKET_NAME + " TEXT, " +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                COLUMN_GEO_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + Accounts.TABLE_NAME + ", " +
                "FOREIGN KEY (" + COLUMN_GEO_ID + ") REFERENCES " + Geolocations.TABLE_NAME + ")";
    }

    public static class Geolocations implements BaseColumns {
        public static final String TABLE_NAME = "geolocations";
        public static final String COLUMN_GEO_ID = "geo_id";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_ADDRESS = "address";

        public static final String CREATE_TABLE_GEOLOCATIONS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_GEO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LONGITUDE + " REAL NOT NULL, " +
                COLUMN_LATITUDE + " REAL NOT NULL, " +
                COLUMN_COUNTRY + " TEXT, " +
                COLUMN_CITY + " TEXT, " +
                COLUMN_ADDRESS + " TEXT NOT NULL)";
    }

    public static class Accounts implements BaseColumns {
        public static final String TABLE_NAME = "accounts";
        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_NUMBER = "number";

        public static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NUMBER + " TEXT NOT NULL UNIQUE)";
    }
}
