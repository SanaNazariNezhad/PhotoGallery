package org.maktab.photogallery.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.network.FlickrFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoRepository {

    private static final String TAG = "PhotoRepository";
    private FlickrFetcher mFetcher;
    private List<GalleryItem> mItems;

    public List<GalleryItem> getItems() {
        return mItems;
    }

    public void setItems(List<GalleryItem> items) {
        mItems = items;
    }


    public PhotoRepository() {
        mFetcher = new FlickrFetcher();
        mItems = new ArrayList<>();
    }

    //this method must run on background thread.
    public List<GalleryItem> fetchItems(int page) {
        String url;
        if (page == 1) {
            url = mFetcher.getPopularUrl();
        }else {
            url = mFetcher.getRecentUrl(page);
        }
        try {
            String response = mFetcher.getUrlString(url);
            Log.d(TAG, "response: " + response);

            JSONObject bodyObject = new JSONObject(response);
            List<GalleryItem> items = parseJson(bodyObject);
            return items;
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private List<GalleryItem> parseJson(JSONObject bodyObject) throws JSONException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JSONObject photosObject = bodyObject.getJSONObject("photos");
        JSONArray photoArray = photosObject.getJSONArray("photo");

        for (int i = 0; i < photoArray.length(); i++) {
            JSONObject photoObject = photoArray.getJSONObject(i);

            if (!photoObject.has("url_s"))
                continue;

            /*String id = photoObject.getString("id");
            String title = photoObject.getString("title");
            String url = photoObject.getString("url_s");

            GalleryItem item = new GalleryItem(id, title, url);*/
            GalleryItem item = gson.fromJson(String.valueOf(photoObject),GalleryItem.class);
            mItems.add(item);
        }

        return mItems;
    }
}