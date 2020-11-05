package org.maktab.photogallery.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.maktab.photogallery.model.GalleryItem;
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
    private MutableLiveData<List<GalleryItem>> mPopularItemsLiveData = new MutableLiveData<>();

    public MutableLiveData<List<GalleryItem>> getPopularItemsLiveData() {
        return mPopularItemsLiveData;
    }


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
    public void fetchItemsAsync() {
        Call<List<GalleryItem>> call = mFlickrService.listItems(NetworkParams.POPULAR_OPTIONS);
        call.enqueue(new Callback<List<GalleryItem>>() {

            //this run on main thread
            @Override
            public void onResponse(Call<List<GalleryItem>> call, Response<List<GalleryItem>> response) {
                List<GalleryItem> items = response.body();

                //update adapter of recyclerview
                mPopularItemsLiveData.setValue(items);
            }

            @Override
            public void onFailure(Call<List<GalleryItem>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }
}