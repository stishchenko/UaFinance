package com.tish.db.connectors;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.R;
import com.tish.db.bases.Category;
import com.tish.db.bases.DBContract.Costs;
import com.tish.db.bases.DBContract.Geolocations;
import com.tish.db.bases.DBHelper;
import com.tish.models.StatisticsItem;

import java.util.ArrayList;
import java.util.List;

public class StatisticsConnector {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor statisticsCursor;
    private Context context;

    public StatisticsConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
        this.context = context;
    }

    public List<StatisticsItem> getCategoryMarketStatistics(char type) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select ");
        StringBuilder group = new StringBuilder(" group by ");
        if (type == 'm') {
            query.append(Costs.COLUMN_MARKET_NAME);
            group.append(Costs.COLUMN_MARKET_NAME);
        } else {
            query.append(Costs.COLUMN_CATEGORY);
            group.append(Costs.COLUMN_CATEGORY);
        }

        statisticsCursor = db.rawQuery(query + ", sum(" + Costs.COLUMN_AMOUNT + "), "
                + "round(sum(" + Costs.COLUMN_AMOUNT + ")/(select sum(" + Costs.COLUMN_AMOUNT + ") from " + Costs.TABLE_NAME + ")*100, 2) "
                + "from " + Costs.TABLE_NAME + group + " order by sum(" + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String category = statisticsCursor.getString(column);
            if (category == null)
                category = context.getResources().getString(R.string.no_place);
            double amount = statisticsCursor.getDouble(column + 1);
            double percent = statisticsCursor.getDouble(column + 2);
            if (type == 'm')
                itemList.add(new StatisticsItem(category, -amount, percent));
            else
                itemList.add(new StatisticsItem(context.getString(Category.valueOf(category).getCategoryName()), -amount, percent));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getCategoryMarketStatisticsByDate(String currentDate, char type) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select ");
        StringBuilder group = new StringBuilder(" group by ");
        if (type == 'm') {
            query.append(Costs.COLUMN_MARKET_NAME);
            group.append(Costs.COLUMN_MARKET_NAME);
        } else {
            query.append(Costs.COLUMN_CATEGORY);
            group.append(Costs.COLUMN_CATEGORY);
        }
        statisticsCursor = db.rawQuery(query + ", sum(" + Costs.COLUMN_AMOUNT + "), "
                + "round(sum(" + Costs.COLUMN_AMOUNT + ")/(select sum(" + Costs.COLUMN_AMOUNT + ") from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%')*100, 2) "
                + "from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%'"
                + group + " order by sum(" + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String category = statisticsCursor.getString(column);
            if (category == null)
                category = context.getResources().getString(R.string.no_place);
            double amount = statisticsCursor.getDouble(column + 1);
            double percent = statisticsCursor.getDouble(column + 2);
            if (type == 'm')
                itemList.add(new StatisticsItem(category, -amount, percent));
            else
                itemList.add(new StatisticsItem(context.getString(Category.valueOf(category).getCategoryName()), -amount, percent));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getGeoStatistics() {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery("select g." + Geolocations.COLUMN_ADDRESS + "|| ', ' || g." + Geolocations.COLUMN_CITY
                + ", sum(c." + Costs.COLUMN_AMOUNT + "), "
                + "round(sum(c." + Costs.COLUMN_AMOUNT + ")/(select sum(" + Costs.COLUMN_AMOUNT + ") from " + Costs.TABLE_NAME + ")*100, 2) "
                + "from " + Costs.TABLE_NAME + " as c inner join " + Geolocations.TABLE_NAME + " as g "
                + "on c." + Costs.COLUMN_GEO_ID + " = g." + Geolocations.COLUMN_GEO_ID
                + " group by c." + Costs.COLUMN_GEO_ID
                + " order by sum(c." + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String address = statisticsCursor.getString(column);
            double amount = statisticsCursor.getDouble(column + 1);
            double percent = statisticsCursor.getDouble(column + 2);
            itemList.add(new StatisticsItem(address, -amount, percent));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getGeoStatisticsByDate(String currentDate) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery("select g." + Geolocations.COLUMN_ADDRESS + "|| ', ' || g." + Geolocations.COLUMN_CITY
                + ", sum(c." + Costs.COLUMN_AMOUNT + "), "
                + "round(sum(c." + Costs.COLUMN_AMOUNT + ")/(select sum(" + Costs.COLUMN_AMOUNT + ") from " + Costs.TABLE_NAME + " where " + Costs.COLUMN_DATE + " like '" + currentDate + "%')*100, 2) "
                + "from " + Costs.TABLE_NAME + " as c inner join " + Geolocations.TABLE_NAME + " as g "
                + "on c." + Costs.COLUMN_GEO_ID + " = g." + Geolocations.COLUMN_GEO_ID
                + " where c." + Costs.COLUMN_DATE + " like '" + currentDate + "%'"
                + " group by c." + Costs.COLUMN_GEO_ID
                + " order by sum(c." + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String address = statisticsCursor.getString(column);
            double amount = statisticsCursor.getDouble(column + 1);
            double percent = statisticsCursor.getDouble(column + 2);
            itemList.add(new StatisticsItem(address, -amount, percent));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getCategoryMarketStatisticsByYear(char type) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select ");
        StringBuilder dateForm = new StringBuilder(", strftime('%Y', date)");
        StringBuilder group = new StringBuilder(" group by ");
        if (type == 'm') {
            query.append(Costs.COLUMN_MARKET_NAME).append(dateForm);
            group.append(Costs.COLUMN_MARKET_NAME).append(dateForm);
        } else {
            query.append(Costs.COLUMN_CATEGORY).append(dateForm);
            group.append(Costs.COLUMN_CATEGORY).append(dateForm);
        }

        statisticsCursor = db.rawQuery(query + ", sum(" + Costs.COLUMN_AMOUNT + ") "
                + "from " + Costs.TABLE_NAME + group
                + " order by sum(" + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String category = statisticsCursor.getString(column);
            if (category == null)
                category = context.getResources().getString(R.string.no_place);
            String date = statisticsCursor.getString(column + 1);
            double amount = statisticsCursor.getDouble(column + 2);
            if (type == 'm')
                itemList.add(new StatisticsItem(category, date, -amount));
            else
                itemList.add(new StatisticsItem(context.getString(Category.valueOf(category).getCategoryName()), date, -amount));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getCategoryMarketStatisticsByMonthSeason(char type, char dateType, String... whereClause) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select ");
        StringBuilder monthDateForm = new StringBuilder(", strftime('%Y-%m', date)");
        StringBuilder seasonDateForm = new StringBuilder(", strftime('%Y', date)");
        StringBuilder where = new StringBuilder(" where strftime('%m', date) ");
        StringBuilder group = new StringBuilder(" group by ");
        if (type == 'm') {
            query.append(Costs.COLUMN_MARKET_NAME);
            group.append(Costs.COLUMN_MARKET_NAME);
        } else {
            query.append(Costs.COLUMN_CATEGORY);
            group.append(Costs.COLUMN_CATEGORY);
        }

        if (dateType == 'm') {
            query.append(monthDateForm);
            where.append("= '").append(whereClause[0]).append("'");
            group.append(monthDateForm);
        } else {
            query.append(seasonDateForm);
            where.append("in ('")
                    .append(whereClause[0]).append("', '")
                    .append(whereClause[1]).append("', '")
                    .append(whereClause[2]).append("')");
            group.append(seasonDateForm);
        }

        statisticsCursor = db.rawQuery(query + ", sum(" + Costs.COLUMN_AMOUNT + ") "
                + "from " + Costs.TABLE_NAME + where + group
                + " order by sum(" + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String category = statisticsCursor.getString(column);
            if (category == null)
                category = context.getResources().getString(R.string.no_place);
            String date = statisticsCursor.getString(column + 1);
            double amount = statisticsCursor.getDouble(column + 2);
            if (type == 'm')
                itemList.add(new StatisticsItem(category, date, -amount));
            else
                itemList.add(new StatisticsItem(context.getString(Category.valueOf(category).getCategoryName()), date, -amount));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getGeoStatisticsByYear() {
        List<StatisticsItem> itemList = new ArrayList<>();
        StringBuilder dateForm = new StringBuilder("strftime('%Y', c.");
        dateForm.append(Costs.COLUMN_DATE).append(")");
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery("select g." + Geolocations.COLUMN_ADDRESS + "|| ', ' || g." + Geolocations.COLUMN_CITY
                + ", " + dateForm + ", sum(c." + Costs.COLUMN_AMOUNT + "), "
                + "from " + Costs.TABLE_NAME + " as c inner join " + Geolocations.TABLE_NAME + " as g "
                + "on c." + Costs.COLUMN_GEO_ID + " = g." + Geolocations.COLUMN_GEO_ID
                + " group by c." + Costs.COLUMN_GEO_ID + ", " + dateForm
                + " order by sum(c." + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String address = statisticsCursor.getString(column);
            String date = statisticsCursor.getString(column + 1);
            double amount = statisticsCursor.getDouble(column + 2);
            itemList.add(new StatisticsItem(address, date, -amount));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<StatisticsItem> getGeoStatisticsByMonthSeason(char dateType, String... whereClause) {
        List<StatisticsItem> itemList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select g.");
        query.append(Geolocations.COLUMN_ADDRESS).append("|| ', ' || g.").append(Geolocations.COLUMN_CITY).append(", ");
        StringBuilder monthDateForm = new StringBuilder(" strftime('%Y-%m', c.date)");
        StringBuilder seasonDateForm = new StringBuilder(" strftime('%Y', c.date)");
        StringBuilder where = new StringBuilder(" where strftime('%m', c.date) ");
        StringBuilder group = new StringBuilder(" group by c.");
        group.append(Costs.COLUMN_GEO_ID).append(", ");

        if (dateType == 'm') {
            query.append(monthDateForm);
            where.append("= '").append(whereClause[0]).append("'");
            group.append(monthDateForm);
        } else {
            query.append(seasonDateForm);
            where.append("in ('")
                    .append(whereClause[0]).append("', '")
                    .append(whereClause[1]).append("', '")
                    .append(whereClause[2]).append("')");
            group.append(seasonDateForm);
        }

        statisticsCursor = db.rawQuery(query + ", sum(c." + Costs.COLUMN_AMOUNT + ") "
                + "from " + Costs.TABLE_NAME + " as c inner join " + Geolocations.TABLE_NAME + " as g "
                + "on c." + Costs.COLUMN_GEO_ID + " = g." + Geolocations.COLUMN_GEO_ID + where + group
                + " order by sum(c." + Costs.COLUMN_AMOUNT + ") desc", null);

        int column = 0;
        while (statisticsCursor.moveToNext()) {
            String address = statisticsCursor.getString(column);
            String date = statisticsCursor.getString(column + 1);
            double amount = statisticsCursor.getDouble(column + 2);
            itemList.add(new StatisticsItem(address, date, -amount));
        }
        statisticsCursor.close();
        db.close();
        return itemList;
    }

    public List<String> getDatesBySettingType(String dateType) {
        List<String> datesList = new ArrayList<>();
        StringBuilder query = new StringBuilder("select distinct strftime('%");
        StringBuilder from = new StringBuilder(Costs.COLUMN_DATE).append(") from ").append(Costs.TABLE_NAME);
        StringBuilder order = new StringBuilder(" order by strftime('%");
        StringBuilder by = new StringBuilder(Costs.COLUMN_DATE).append(")");
        switch (dateType) {
            case "m":
                query.append("m', ").append(from).append(order).append("m', ").append(by);
                break;
            case "y":
                query.append("Y', ").append(from).append(order).append("Y', ").append(by);
                break;
        }
        db = dbHelper.getReadableDatabase();
        statisticsCursor = db.rawQuery(query.toString(), null);
        while (statisticsCursor.moveToNext()) {
            datesList.add(statisticsCursor.getString(0));
        }
        statisticsCursor.close();
        db.close();
        return datesList;
    }
}
