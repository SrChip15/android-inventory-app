package com.example.android.stockkeepingassistant.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.stockkeepingassistant.model.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "products.db";

    // SQL queries
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
            ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProductEntry.UUID + ", " +
            ProductEntry.COLUMN_PRODUCT_TITLE + " TEXT, " +
            ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            ProductEntry.COLUMN_PRODUCT_PRICE + " TEXT, " +
            ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT, " +
            ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT);";

    ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Currently, there is only one version of the db
        // So leave empty
    }
}
