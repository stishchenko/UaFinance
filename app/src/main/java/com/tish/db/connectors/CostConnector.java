package com.tish.db.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.mikephil.charting.data.PieEntry;
import com.tish.R;
import com.tish.db.bases.Category;
import com.tish.db.bases.DBContract.*;
import com.tish.db.bases.DBHelper;
import com.tish.models.Cost;
import com.tish.models.GeoPair;
import com.tish.models.Geolocation;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostConnector {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor costCursor;
    private AccPhoConnector accPhoConnector;
    private GeoConnector geoConnector;
    private Context context;

    public CostConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
        accPhoConnector = new AccPhoConnector(context);
        geoConnector = new GeoConnector(context);
        this.context = context;
    }


    public boolean costsExist() {
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select count(*) from " + Costs.TABLE_NAME, null);
        costCursor.moveToFirst();
        if (costCursor.getInt(0) == 0)
            return false;
        else
            return true;
    }

    public ArrayList<Cost> getCosts() {
        ArrayList<Cost> costs = new ArrayList<>();
        Cost tempCost;
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select * from " + Costs.TABLE_NAME, null);

        while (costCursor.moveToNext()) {
            int costId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_COST_ID));
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            double amount = costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_AMOUNT));
            String date = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_DATE));
            String marketName = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_MARKET_NAME));
            Integer accountId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_ACCOUNT_ID));
            Integer geoId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_GEO_ID));
            tempCost = new Cost(costId, Category.valueOf(category), amount, date, marketName);
            if (accountId != null) {
                tempCost.setAccountNumber(accPhoConnector.getAccountById(accountId));
            }
            if (geoId != null) {
                Geolocation geo = geoConnector.getGeoById(geoId);
                tempCost.setGeo(geo);
            }
            costs.add(0, tempCost);
        }

        costCursor.close();
        db.close();
        return costs;
    }

    public List<Cost> getCostsByGeoId(int geoId) {
        List<Cost> costsByGeo = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select " + Costs.COLUMN_CATEGORY + ", " + Costs.COLUMN_AMOUNT + ", "
                + Costs.COLUMN_DATE + ", " + Costs.COLUMN_MARKET_NAME +
                " from " + Costs.TABLE_NAME +
                " where " + Costs.COLUMN_GEO_ID + " = " + geoId +
                " order by " + Costs.COLUMN_DATE + " desc", null);

        while (costCursor.moveToNext()) {
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            double amount = costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_AMOUNT));
            String date = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_DATE));
            String marketName = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_MARKET_NAME));
            costsByGeo.add(new Cost(Category.valueOf(category), amount, date, marketName));
        }

        costCursor.close();
        db.close();
        return costsByGeo;
    }

    public ArrayList<Cost> getCostsByDate(String currentDate) {
        ArrayList<Cost> costsByDate = new ArrayList<>();
        Cost tempCost;
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select * from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'", null);

        while (costCursor.moveToNext()) {
            int costId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_COST_ID));
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            double amount = costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_AMOUNT));
            String date = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_DATE));
            String marketName = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_MARKET_NAME));
            int accountId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_ACCOUNT_ID));
            int geoId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_GEO_ID));
            tempCost = new Cost(costId, Category.valueOf(category), amount, date, marketName);
            if (accountId != 0) {
                tempCost.setAccountNumber(accPhoConnector.getAccountById(accountId));
            }
            if (geoId != 0) {
                Geolocation geo = geoConnector.getGeoById(geoId);
                tempCost.setGeo(geo);
            }
            costsByDate.add(0, tempCost);
        }

        costCursor.close();
        db.close();
        return costsByDate;
    }

    public ArrayList<Cost> getCostsByDateAccount(String currentDate, String currentAccount) {
        ArrayList<Cost> costsByDate = new ArrayList<>();
        Cost tempCost;
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select * from " + Costs.TABLE_NAME + " as c inner join "
                + Accounts.TABLE_NAME + " as a on c." + Costs.COLUMN_ACCOUNT_ID + "= a." + Accounts.COLUMN_ACCOUNT_ID
                + " where c." + Costs.COLUMN_DATE + " like '" + currentDate + "%' and a." + Accounts.COLUMN_NUMBER + " like '" + currentAccount + "'", null);

        while (costCursor.moveToNext()) {
            int costId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_COST_ID));
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            double amount = costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_AMOUNT));
            String date = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_DATE));
            String marketName = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_MARKET_NAME));
            int geoId = costCursor.getInt(costCursor.getColumnIndexOrThrow(Costs.COLUMN_GEO_ID));
            tempCost = new Cost(costId, Category.valueOf(category), amount, date, marketName, currentAccount);
            if (geoId != 0) {
                Geolocation geo = geoConnector.getGeoById(geoId);
                tempCost.setGeo(geo);
            }
            costsByDate.add(0, tempCost);
        }

        costCursor.close();
        db.close();
        return costsByDate;
    }

    public List<PieEntry> getTotalAmountsForCategoriesByDate(String currentDate) {
        List<PieEntry> pieEntries = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select " + Costs.COLUMN_CATEGORY + ", sum(" + Costs.COLUMN_AMOUNT + ") from "
                + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'" + " group by " + Costs.COLUMN_CATEGORY, null);

        while (costCursor.moveToNext()) {
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            float totalAmount = (float) costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY) + 1);
            pieEntries.add(new PieEntry(totalAmount, context.getString(Category.valueOf(category).getCategoryName())));
        }

        costCursor.close();
        db.close();
        return pieEntries;
    }

    public ArrayList<PieEntry> getTotalAmountsForCategoriesByDateAccount(String currentDate, String currentAccount) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select c." + Costs.COLUMN_CATEGORY + ", sum(c." + Costs.COLUMN_AMOUNT
                + ") from " + Costs.TABLE_NAME + " as c inner join "
                + Accounts.TABLE_NAME + " as a on c." + Costs.COLUMN_ACCOUNT_ID + "= a." + Accounts.COLUMN_ACCOUNT_ID
                + " where c." + Costs.COLUMN_DATE + " like '" + currentDate + "%' and a." + Accounts.COLUMN_NUMBER + " like '" + currentAccount + "'"
                + " group by " + Costs.COLUMN_CATEGORY, null);

        while (costCursor.moveToNext()) {
            String category = costCursor.getString(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY));
            float totalAmount = (float) costCursor.getDouble(costCursor.getColumnIndexOrThrow(Costs.COLUMN_CATEGORY) + 1);
            pieEntries.add(new PieEntry(totalAmount, context.getString(Category.valueOf(category).getCategoryName())));
        }
        costCursor.close();
        db.close();
        return pieEntries;
    }

    public int[] getCategoriesColorsByDate(String currentDate) {
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select distinct " + Costs.COLUMN_CATEGORY + " from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'", null);
        int[] colors = new int[costCursor.getCount()];
        int i = 0;

        while (costCursor.moveToNext()) {
            String category = costCursor.getString(0);
            colors[i] = Category.valueOf(category).getColorResource();
            i++;
        }
        costCursor.close();
        db.close();
        return colors;
    }

    public int[] getCategoriesColorsByDateAccount(String currentDate, String currentAccount) {
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select distinct c." + Costs.COLUMN_CATEGORY + " from " + Costs.TABLE_NAME + " as c inner join "
                + Accounts.TABLE_NAME + " as a on c." + Costs.COLUMN_ACCOUNT_ID + "= a." + Accounts.COLUMN_ACCOUNT_ID
                + " where c." + Costs.COLUMN_DATE + " like '" + currentDate + "%' and a." + Accounts.COLUMN_NUMBER + " like '" + currentAccount + "'", null);
        int[] colors = new int[costCursor.getCount()];
        int i = 0;

        while (costCursor.moveToNext()) {
            String category = costCursor.getString(0);
            colors[i] = Category.valueOf(category).getColorResource();
            i++;
        }
        costCursor.close();
        db.close();
        return colors;
    }

    public List<YearMonth> getCostDates(int indicator) {
        List<YearMonth> dateList = new ArrayList<>();
        YearMonth ym;
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select distinct strftime('%Y-%m'," + Costs.COLUMN_DATE + ") from " + Costs.TABLE_NAME, null);

        while (costCursor.moveToNext()) {
            ym = YearMonth.parse(costCursor.getString(0));
            if (indicator == 0)
                dateList.add(ym);
            else
                dateList.add(0, ym);
        }
        costCursor.close();
        db.close();
        return dateList;
    }

    public HashMap<Integer, GeoPair> getGeoPairs() {
        HashMap<Integer, GeoPair> geoPairs = new HashMap<>();
        db = dbHelper.getReadableDatabase();
        costCursor = db.rawQuery("select " + Costs.COLUMN_GEO_ID + ", count(*), sum(" + Costs.COLUMN_AMOUNT
                + ") from " + Costs.TABLE_NAME
                + " group by " + Costs.COLUMN_GEO_ID, null);

        int column = costCursor.getColumnIndexOrThrow(Costs.COLUMN_GEO_ID);
        while (costCursor.moveToNext()) {
            int geoId = costCursor.getInt(column);
            int number = costCursor.getInt(column + 1);
            int amount = costCursor.getInt(column + 2);
            geoPairs.put(geoId, new GeoPair(number, amount));
        }
        costCursor.close();
        db.close();
        return geoPairs;
    }

    public long insertNewCost(Cost cost) {
        ContentValues cvNewCost = new ContentValues();
        cvNewCost.put(Costs.COLUMN_CATEGORY, cost.getCategory().toString());
        cvNewCost.put(Costs.COLUMN_AMOUNT, cost.getAmount());
        cvNewCost.put(Costs.COLUMN_DATE, cost.getDate());
        cvNewCost.put(Costs.COLUMN_MARKET_NAME, cost.getMarketName());
        if (cost.getAccountNumber() != null) {
            cvNewCost.put(Costs.COLUMN_ACCOUNT_ID, accPhoConnector.getAccountIdByNumber(cost.getAccountNumber()));
        }
        if (cost.getGeo() != null) {
            cvNewCost.put(Costs.COLUMN_GEO_ID, geoConnector.insertGeolocationToGetId(cost.getGeo()));
        }
        db = dbHelper.getWritableDatabase();
        long result = db.insert(Costs.TABLE_NAME, null, cvNewCost);
        db.close();
        return result;
    }

    public int updateCost(Cost editCost, boolean updateAmount) {
        ContentValues cvEditCost = new ContentValues();
        cvEditCost.put(Costs.COLUMN_CATEGORY, editCost.getCategory().toString());
        cvEditCost.put(Costs.COLUMN_AMOUNT, editCost.getAmount());
        cvEditCost.put(Costs.COLUMN_DATE, editCost.getDate());
        cvEditCost.put(Costs.COLUMN_MARKET_NAME, editCost.getMarketName());
        if (updateAmount) {
            if (editCost.getAccountNumber() != null)
                cvEditCost.put(Costs.COLUMN_ACCOUNT_ID, accPhoConnector.getAccountIdByNumber(editCost.getAccountNumber()));
            else
                cvEditCost.put(Costs.COLUMN_ACCOUNT_ID, editCost.getAccountNumber());
        }
        db = dbHelper.getWritableDatabase();
        int result = db.update(Costs.TABLE_NAME, cvEditCost, Costs.COLUMN_COST_ID + "=" + editCost.getCostId(), null);
        db.close();
        return result;
    }

    public int updateGeoInCost(Geolocation editGeo, int costId) {
        int geoId = geoConnector.insertGeolocationToGetId(editGeo);
        int result = 0;
        if (geoId != editGeo.getGeoId()) {
            ContentValues cvEditGeoInCost = new ContentValues();
            cvEditGeoInCost.put(Costs.COLUMN_GEO_ID, geoId);
            db = dbHelper.getWritableDatabase();
            result = db.update(Costs.TABLE_NAME, cvEditGeoInCost, Costs.COLUMN_COST_ID + "=" + costId, null);
            db.close();
        }
        return result;
    }

    public int deleteCost(int costId) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete(Costs.TABLE_NAME, Costs.COLUMN_COST_ID + "=?", new String[]{String.valueOf(costId)});
        db.close();
        return result;
    }


}
