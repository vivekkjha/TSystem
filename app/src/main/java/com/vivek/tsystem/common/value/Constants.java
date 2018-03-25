package com.vivek.tsystem.common.value;



/**
 * Created by vk on 14-03-2015.
 */
public class Constants {


    public static class AppConstants {


        public static final String FLICKR_API_KEY = "3e1da1ab7062b54b4a63030b7630000d";
        public static final String OUTPUT_FORMAT = "json";
        public static final String FLICKR_SEARCH_METHOD = "flickr.photos.search";
        public static final int NO_JSON_CALLBACK = 1;
        public static  final String FOLDER_CACHE_APP_CAPTURED = ".appCaptured";

    }

    public static class Api {
        public static final int NETWORK_CONNECTION_TIMEOUT = 30; // 30 sec
        public static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
        public static final int CACHE_MAX_AGE = 2; // 2 min
        public static final int CACHE_MAX_STALE = 7; // 7 day
        public static final int API_RETRY_COUNT = 3;
    }

    public static class Bundle {
        public static final String URL = "url";
        public static final String PAGE = "page";
        public static final String LIMIT = "limit";
        public static final String TEXT = "text" ;
    }
}

