package org.maktab.photogallery.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.network.FlickrFetcher;
import org.maktab.photogallery.network.NetworkParams;
import org.maktab.photogallery.network.retrofit.FlickrService;
import org.maktab.photogallery.network.retrofit.RetrofitInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PhotoRepository {

    private static final String TAG = "PhotoRepository";
    private FlickrService mFlickrService;

//    private FlickrFetcher mFetcher;
    /*private List<GalleryItem> mItems;

    public List<GalleryItem> getItems() {
        return mItems;
    }

    public void setItems(List<GalleryItem> items) {
        mItems = items;
    }*/


    public PhotoRepository() {
        Retrofit retrofit = RetrofitInstance.getInstance().getRetrofit();
        mFlickrService = retrofit.create(FlickrService.class);
    }

    //this method must run on background thread.
    public List<GalleryItem> fetchItems() {
        Call<List<GalleryItem>> call = mFlickrService.listItems(NetworkParams.POPULAR_OPTIONS);
        try {
            Response<List<GalleryItem>> response = call.execute();
            return response.body();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    //this method can be run in any thread.
    public void fetchItemsAsync(Callbacks callBacks) {
        Call<List<GalleryItem>> call = mFlickrService.listItems(NetworkParams.POPULAR_OPTIONS);
        call.enqueue(new Callback<List<GalleryItem>>() {
            @Override
            public void onResponse(Call<List<GalleryItem>> call, Response<List<GalleryItem>> response) {
                List<GalleryItem> items = response.body();
                //update adapter of recyclerview
                callBacks.onItemResponse(items);
            }

            @Override
            public void onFailure(Call<List<GalleryItem>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    public interface Callbacks {
        void onItemResponse(List<GalleryItem> items);
    }

    //this method must run on background thread.
 /*   public List<GalleryItem> fetchItems(int page) {
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
    }*/

/*    private List<GalleryItem> parseJson(JSONObject bodyObject) throws JSONException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JSONObject photosObject = bodyObject.getJSONObject("photos");
        JSONArray photoArray = photosObject.getJSONArray("photo");

        for (int i = 0; i < photoArray.length(); i++) {
            JSONObject photoObject = photoArray.getJSONObject(i);

            if (!photoObject.has("url_s"))
                continue;

            GalleryItem item = gson.fromJson(String.valueOf(photoObject),GalleryItem.class);
            mItems.add(item);
        }

        return mItems;
    }*/
}