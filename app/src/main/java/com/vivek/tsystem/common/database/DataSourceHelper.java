package com.vivek.tsystem.common.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.vivek.tsystem.common.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;


public class DataSourceHelper {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    private static final String TAG = DataSourceHelper.class.getSimpleName();

    @Inject
    public DataSourceHelper(Context context){
        dbHelper = new DBHelper(context);
        open();
    }
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public void insertData(String query, String webPath, String filePath)
    {
        try {
            ContentValues values = new ContentValues();
            values.put(DbSchema.FlickrQuery.Column.QUERY, query);
            values.put(DbSchema.FlickrQuery.Column.WEB_PATH, webPath);
            values.put(DbSchema.FlickrQuery.Column.DATA, filePath);
            long n = database.insert(DbSchema.FlickrQuery.TABLE_NAME, null, values);
            LogUtil.d(TAG, "Inserted Row: " + n);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            database.endTransaction();
        }
    }

    public List<String> getAllImagePaths(String query){
        String[] args = {query};
        List<String> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(DbSchema.FlickrQuery.TABLE_NAME,null,DbSchema.FlickrQuery.Column.QUERY+"=?",args,null,null,null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    list.add(cursor.getString(cursor.getColumnIndex(DbSchema.FlickrQuery.Column.DATA)));
                    cursor.moveToNext();
                }
            }
            return list;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return list;
    }

    public String getLocalImagePath(String query,String webUrl){
        String[] args = {query,webUrl};
        String path = null;
        Cursor cursor = null;
        try {
            cursor = database.query(DbSchema.FlickrQuery.TABLE_NAME,null,DbSchema.FlickrQuery.Column.QUERY+"=? AND "+ DbSchema.FlickrQuery.Column.WEB_PATH +"=?"
                    ,args,null,null,null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(DbSchema.FlickrQuery.Column.DATA));

            }
            return path;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return null;
    }

    public long deleteAllFilesRelatedToQuery(String query)
    {
        String[] args = {query};
        return database.delete(DbSchema.FlickrQuery.TABLE_NAME, DbSchema.FlickrQuery.Column.QUERY + "=?",args);
    }



}
