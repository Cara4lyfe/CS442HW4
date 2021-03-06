package com.example.chandler.cs442hw4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Chandler on 3/3/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DataHandler";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "StockDatabase";
    private static final String TABLE_NAME = "StockTable";
    private static final String SYMBOL = "Symbol";
    private static final String COMPANY = "Company";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + SYMBOL + " TEXT not null unique, "
                    + COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addStock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompanyName());

        database.insert(TABLE_NAME, null, values);
        dumpDbToLog();
    }

    public void deleteStock(String symbol) {
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[] { symbol });
    }

    public boolean existsStock(String symbol) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + SYMBOL + " = \"" + symbol + "\"", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 1 && cursor.getString(0).equals(symbol)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public ArrayList<String[]> loadStocks() {
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new String[] {symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-6s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    public void shutDown(){
        database.close();
    }

}
