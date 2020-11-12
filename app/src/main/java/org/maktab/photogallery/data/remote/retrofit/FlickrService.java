package org.maktab.photogallery.data.remote.retrofit;

import org.maktab.photogallery.data.model.GalleryItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface FlickrService {

    @GET(".")
    Call<List<GalleryItem>> listItems(@QueryMap Map<String, String> options);
}
