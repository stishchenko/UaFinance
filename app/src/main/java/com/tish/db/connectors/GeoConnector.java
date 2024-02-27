package com.tish.db.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.db.bases.DBContract.Geolocations;
import com.tish.db.bases.DBHelper;
import com.tish.models.Geolocation;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GeoConnector {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor geoCursor;

    public GeoConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
    }

    public List<Geolocation> getGeos() {
        List<Geolocation> geoList = new ArrayList<>();
        Geolocation temp;
        db = dbHelper.getReadableDatabase();
        geoCursor = db.rawQuery("select * from " + Geolocations.TABLE_NAME, null);

        while (geoCursor.moveToNext()) {
            temp = new Geolocation();
            temp.setGeoId(geoCursor.getInt(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_GEO_ID)));
            temp.setLongitude(geoCursor.getDouble(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_LONGITUDE)));
            temp.setLatitude(geoCursor.getDouble(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_LATITUDE)));
            temp.setCountry(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_COUNTRY)));
            temp.setCity(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_CITY)));
            temp.setAddress(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_ADDRESS)));
            geoList.add(temp);
        }
        geoCursor.close();
        db.close();
        return geoList;
    }

    public Geolocation getGeoById(int geoId) {
        Geolocation geo = new Geolocation();
        db = dbHelper.getReadableDatabase();
        geoCursor = db.rawQuery("select * from " + Geolocations.TABLE_NAME + " where " + Geolocations.COLUMN_GEO_ID + "=" + geoId, null);
        geoCursor.moveToFirst();
        geo.setGeoId(geoCursor.getInt(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_GEO_ID)));
        geo.setLongitude(geoCursor.getDouble(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_LONGITUDE)));
        geo.setLatitude(geoCursor.getDouble(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_LATITUDE)));
        geo.setCountry(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_COUNTRY)));
        geo.setCity(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_CITY)));
        geo.setAddress(geoCursor.getString(geoCursor.getColumnIndexOrThrow(Geolocations.COLUMN_ADDRESS)));
        geoCursor.close();
        db.close();
        return geo;
    }

    public int insertGeolocationToGetId(Geolocation geo) {
        int geoId = -1;
        db = dbHelper.getWritableDatabase();
        geoCursor = db.rawQuery("select " + Geolocations.COLUMN_GEO_ID + " from " + Geolocations.TABLE_NAME + " where "
                        + Geolocations.COLUMN_LONGITUDE + "=? and "
                        + Geolocations.COLUMN_LATITUDE + "=? and "
                        + Geolocations.COLUMN_ADDRESS + "=?",
                new String[]{String.valueOf(geo.getLongitude()), String.valueOf(geo.getLatitude()), geo.getAddress()});
        if (geoCursor.getCount() == 0) {
            ContentValues cvGeo = new ContentValues();
            cvGeo.put(Geolocations.COLUMN_LONGITUDE, geo.getLongitude());
            cvGeo.put(Geolocations.COLUMN_LATITUDE, geo.getLatitude());
            cvGeo.put(Geolocations.COLUMN_COUNTRY, geo.getCountry());
            cvGeo.put(Geolocations.COLUMN_CITY, geo.getCity());
            cvGeo.put(Geolocations.COLUMN_ADDRESS, geo.getAddress());
            geoId = (int) db.insert(Geolocations.TABLE_NAME, null, cvGeo);
        } else {
            geoCursor.moveToFirst();
            geoId = geoCursor.getInt(0);
        }
        geoCursor.close();
        db.close();
        return geoId;
    }

}
