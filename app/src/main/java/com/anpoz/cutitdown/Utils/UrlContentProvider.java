package com.anpoz.cutitdown.Utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by anpoz on 2015/8/17.
 */
public class UrlContentProvider extends ContentProvider {

    private static HashMap<String, String> sUrlProjectionMap;

    private static final int URLS = 1;
    private static final int URLS_ID = 2;

    private static final UriMatcher sUriMatcher;

    private SQLiteHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        mOpenHelper = new SQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Provider.UrlColumns.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case URLS:
                qb.setProjectionMap(sUrlProjectionMap);
                break;
            case URLS_ID:
                qb.setProjectionMap(sUrlProjectionMap);
                qb.appendWhere(Provider.UrlColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        //如果排序参数没有指定
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Provider.UrlColumns.DEFAULT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        //获取数据库并查询
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case URLS:
                return Provider.CONTENT_TYPE;
            case URLS_ID:
                return Provider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != URLS) {
            throw new IllegalArgumentException("Unknown URI" + uri);
        }

        ContentValues cv;
        if (values != null) {
            cv = new ContentValues(values);
        } else {
            cv = new ContentValues();
        }
        //确保每一项都设置好
        if (cv.containsKey(Provider.UrlColumns.URL_SHORT) == false) {
            cv.put(Provider.UrlColumns.URL_SHORT, "");
        }
        if (cv.containsKey(Provider.UrlColumns.URL_LONG) == false) {
            cv.put(Provider.UrlColumns.URL_LONG, "");
        }
        if (cv.containsKey(Provider.UrlColumns.URL_STARED) == false) {
            cv.put(Provider.UrlColumns.URL_STARED, "");
        }
        if (cv.containsKey(Provider.UrlColumns.URL_DATE) == false) {
            cv.put(Provider.UrlColumns.URL_DATE, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID = db.insert(Provider.UrlColumns.TABLE_NAME, Provider.UrlColumns.URL_SHORT, cv);
        if (rowID > 0) {
            Uri noteUri = ContentUris.withAppendedId(Provider.UrlColumns.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        try {
            throw new SQLException("Failed to insert row into " + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case URLS:
                count = db.delete(Provider.UrlColumns.TABLE_NAME, selection, selectionArgs);
                break;
            case URLS_ID:
                String noteID = uri.getPathSegments().get(1);
                count = db.delete(Provider.UrlColumns.TABLE_NAME, Provider.UrlColumns._ID + "=" + noteID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case URLS:
                count = db.update(Provider.UrlColumns.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URLS_ID:
                String noteID=uri.getPathSegments().get(1);
                count = db.delete(Provider.UrlColumns.TABLE_NAME, Provider.UrlColumns._ID + "=" + noteID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //这个地方的urls要和UrlColumns.CONTENT_URI中最后面的一个Segment一致
        sUriMatcher.addURI(Provider.AUTHORITY, "urls", URLS);
        sUriMatcher.addURI(Provider.AUTHORITY, "urls/#", URLS_ID);

        sUrlProjectionMap = new HashMap<String, String>();
        sUrlProjectionMap.put(Provider.UrlColumns._ID, Provider.UrlColumns._ID);
        sUrlProjectionMap.put(Provider.UrlColumns.URL_SHORT, Provider.UrlColumns.URL_SHORT);
        sUrlProjectionMap.put(Provider.UrlColumns.URL_LONG, Provider.UrlColumns.URL_LONG);
        sUrlProjectionMap.put(Provider.UrlColumns.URL_STARED, Provider.UrlColumns.URL_STARED);
        sUrlProjectionMap.put(Provider.UrlColumns.URL_DATE, Provider.UrlColumns.URL_DATE);

    }
}
