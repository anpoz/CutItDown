package com.anpoz.cutitdown.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anpoz on 2015/8/10.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "url_convert";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Provider.UrlColumns.TABLE_NAME + " ("
                + Provider.UrlColumns._ID + " INTEGER PRIMARY KEY,"
                + Provider.UrlColumns.URL_SHORT + " TEXT,"
                + Provider.UrlColumns.URL_LONG + " TEXT,"
                + Provider.UrlColumns.URL_STARED + " INTEGER,"
                + Provider.UrlColumns.URL_DATE + " LONG"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Provider.UrlColumns.TABLE_NAME);
        onCreate(db);
    }
}
