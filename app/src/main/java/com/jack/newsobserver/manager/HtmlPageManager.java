package com.jack.newsobserver.manager;

import android.content.Context;
import android.net.Uri;

import com.jack.newsobserver.parser.NewsHtmlPageMinimizer;
import com.jack.newsobserver.util.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class HtmlPageManager {
    private static Context mContext;
    private static String mUrl;

    public static String getHtmlPage(Context context, String pageUrl) throws IOException {
        mContext = context;
        mUrl = pageUrl;
        return loadPageData();
    }

    public static void clearStoredHtmlPages(Context context) {
        mContext = context;
        File cacheDir = htmlPageCacheDir();
        for (File cachedHtmlPage : cacheDir.listFiles()) {
            if (cachedHtmlPage.getAbsolutePath().endsWith(Constants.CACHE_FILE_EXTENSION)) {
                cachedHtmlPage.delete();
            }
        }
    }

    public static String getHtmlPageFileLocalPath(Context context, String url)throws IOException{
        mContext = context;
        return "file:///"+getHtmlPageFile(url).getAbsolutePath();
    }

    private static File getHtmlPageFile(String url) throws IOException {
        String fileName = Uri.parse(url).getLastPathSegment() + Constants.CACHE_FILE_EXTENSION;
        File cacheFile = new File(htmlPageCacheDir(), fileName);
        if (!cacheFile.exists()) {
            savePageData(NewsHtmlPageMinimizer.getMinimizedHtml(url), cacheFile);
        }
        return cacheFile;
    }

    private static void savePageData(String data, File cacheFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(cacheFile)));
        writer.write(data);
        writer.close();
    }

    private static String loadPageData() throws IOException {
        StringBuilder pageData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(getHtmlPageFile(mUrl))));
        String data = "";
        while ((data = reader.readLine()) != null) {
            pageData.append(data).append("\n");
        }
        return pageData.toString();
    }

    private static File htmlPageCacheDir() {
        File cacheDir = new File(mContext.getCacheDir() + File.separator + Constants.CACHE_DIR);
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdir()) {
                cacheDir = mContext.getCacheDir();
            }
        }
        return cacheDir;
    }
}
