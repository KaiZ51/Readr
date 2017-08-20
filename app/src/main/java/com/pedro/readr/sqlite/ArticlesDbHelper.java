package com.pedro.readr.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ArticlesDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ArticlesContract.ArticleEntry.TABLE_NAME + " (" +
                    ArticlesContract.ArticleEntry._ID + " INTEGER PRIMARY KEY," +
                    ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ArticlesContract.ArticleEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ArticlesContract.ArticleEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    ArticlesContract.ArticleEntry.COLUMN_NAME_URLTOIMAGE + TEXT_TYPE + COMMA_SEP +
                    ArticlesContract.ArticleEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ArticlesContract.ArticleEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Articles.db";

    public ArticlesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}