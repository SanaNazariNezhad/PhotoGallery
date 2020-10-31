package org.maktab.photogallery.controller.fragment;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MyCache {

    private static MyCache instance;
    private LruCache<String, Bitmap> lru;

    private MyCache() {

        lru = new LruCache<String , Bitmap>(1024);

    }

    public static MyCache getInstance() {

        if (instance == null) {
            instance = new MyCache();
        }
        return instance;

    }

    public LruCache<String, Bitmap> getLru() {
        return lru;
    }

    public void saveBitmapToCache(String key, Bitmap bitmap){

        MyCache.getInstance().getLru().put(key, bitmap);
    }

    public Bitmap retrieveBitmapFromCache(String key){

        Bitmap bitmap = MyCache.getInstance().getLru().get(key);
        return bitmap;
    }

}
