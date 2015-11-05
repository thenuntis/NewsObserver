package com.jack.newsobserver;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCache {

    private static ImageCache ourInstance = new ImageCache();

    private static LruCache<String, Bitmap> imgMemoryCache;

    public static ImageCache getInstance() {
        return ourInstance;
    }
    private ImageCache() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        final int cacheSize = maxMemory / 8;
        imgMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            imgMemoryCache.put(key, bitmap);
        }
    }
    public static Bitmap getBitmapFromMemCache(String key) {
        return imgMemoryCache.get(key);
    }
    public static void clearCache () {
        imgMemoryCache.evictAll();
    }
}
