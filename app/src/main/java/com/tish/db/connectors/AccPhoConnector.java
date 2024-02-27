package com.tish.db.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tish.R;
import com.tish.db.bases.DBContract.Accounts;
import com.tish.db.bases.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class AccPhoConnector {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor apCursor;
    private Context context;

    public AccPhoConnector(Context context) {
        this.dbHelper = DBHelper.newInstance(context);
        this.context = context;
    }

    public ArrayList<String> getAccounts() {
        ArrayList<String> accountList = new ArrayList<>();

        db = dbHelper.getReadableDatabase();
        apCursor = db.rawQuery("select * from " + Accounts.TABLE_NAME, null);
        if (apCursor.getCount() > 0) {

            while (apCursor.moveToNext()) {
                accountList.add(apCursor.getString(apCursor.getColumnIndexOrThrow(Accounts.COLUMN_NUMBER)));
            }
        }
        apCursor.close();
        db.close();
        return accountList;
    }

    public String getAccountById(int accountId) {
        db = dbHelper.getReadableDatabase();
        apCursor = db.rawQuery("select " + Accounts.COLUMN_NUMBER + " from " + Accounts.TABLE_NAME + " where " + Accounts.COLUMN_ACCOUNT_ID + "=" + accountId, null);
        apCursor.moveToFirst();
        String number = apCursor.getString(apCursor.getColumnIndexOrThrow(Accounts.COLUMN_NUMBER));
        apCursor.close();
        db.close();
        return number;
    }

    public int getAccountIdByNumber(String number) {
        db = dbHelper.getReadableDatabase();
        apCursor = db.rawQuery("select " + Accounts.COLUMN_ACCOUNT_ID + " from " + Accounts.TABLE_NAME + " where " + Accounts.COLUMN_NUMBER + "='" + number + "'", null);
        apCursor.moveToFirst();
        int accountId = -1;
        if (apCursor.getCount() > 0)
            accountId = apCursor.getInt(apCursor.getColumnIndexOrThrow(Accounts.COLUMN_ACCOUNT_ID));
        apCursor.close();
        db.close();
        return accountId;
    }

    public long insertAccount(String number) {
        ContentValues cvAccount = new ContentValues();
        cvAccount.put(Accounts.COLUMN_NUMBER, number);
        db = dbHelper.getWritableDatabase();
        long accountId = db.insert(Accounts.TABLE_NAME, null, cvAccount);
        db.close();
        return accountId;
    }

    public long updateAccount(int accountId, String newNumber) {
        ContentValues cvAccount = new ContentValues();
        cvAccount.put(Accounts.COLUMN_NUMBER, newNumber);
        db = dbHelper.getWritableDatabase();
        long result = db.update(Accounts.TABLE_NAME, cvAccount, Accounts.COLUMN_ACCOUNT_ID + "=" + accountId, null);
        db.close();
        return result;
    }

    public int deleteAccount(String number) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete(Accounts.TABLE_NAME, Accounts.COLUMN_NUMBER + "='" + number + "'", null);
        db.close();
        return result;
    }
}
