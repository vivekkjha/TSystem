package com.vivek.tsystem.common.database;

/**
 * Created by mds on 28/08/17.
 */

public class DbSchema {

    public static final String DB_SCHEMA = "tsystem.db";
    public static final int DB_VERSION = 1;

    public static class FlickrQuery {
        public static final String TABLE_NAME = "flickr_query";

        public static final String CREATE_TABLE_QRY = "CREATE TABLE" + " " + TABLE_NAME + "(" +
                Column.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Column.QUERY + " TEXT, " +
                Column.WEB_PATH + " TEXT, " +
                Column.DATA + " TEXT " +
                ")";

        public static class Column {
            public static final String ID = "_id";
            public static final String QUERY = "query";
            public static final String WEB_PATH = "web_path";
            public static final String DATA = "data";
        }
    }}
