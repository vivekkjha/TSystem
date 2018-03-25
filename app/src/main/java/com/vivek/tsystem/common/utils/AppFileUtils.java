package com.vivek.tsystem.common.utils;

import android.content.Context;
import android.net.Uri;

import com.vivek.tsystem.common.value.Constants;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by vivek on 25/03/18.
 */


public class AppFileUtils {

    @Inject
    AppFileUtils(){

    }

    public File createFileAt(Context context, String fileName,String extension) {
        File externalFilesDir  = context.getExternalFilesDir(null);
        String directoryPath = (externalFilesDir == null ? context.getCacheDir() : externalFilesDir) + File.separator + Constants.AppConstants.FOLDER_CACHE_APP_CAPTURED;
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(directoryPath + File.separator  + "cached_"+System.currentTimeMillis() +"." + extension);
    }


    public File getFile(Context context, Uri uri, File des) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if(inputStream == null) return null;
            IOUtils.copy(inputStream, new FileOutputStream(des));
            IOUtils.closeQuietly(inputStream);
            return des;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getFile(String url, Map<String, String> params, File des) {
        try {
            InputStream inputStream = getInputStream(url, params, null);
            IOUtils.copy(inputStream, new FileOutputStream(des));
            IOUtils.closeQuietly(inputStream);
            return des;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static InputStream getInputStream(String url, Map<String, String> params, String auth) throws IOException {
        URL urlObject = new URL(buildURL(url, params));
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        if (auth != null) connection.setRequestProperty("Authorization", auth);
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException(connection.getResponseMessage());
        }

        return connection.getInputStream();
    }

    private static String buildURL(String base, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(base);
        String sep = base.contains("?") ? "&" : "?";
        toPostData(params, sb, sep);
        return sb.toString();
    }

    private static String toPostData(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        toPostData(params, sb, "");
        return sb.toString();
    }

    private static void toPostData(Map<String, String> params, StringBuilder sb, String sep) {
        if (params == null) return;
        for (String name : params.keySet()) {
            sb.append(sep);
            sep = "&";
            sb.append(name);
            sb.append("=");
            try {
                sb.append(URLEncoder.encode(params.get(name), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


}
