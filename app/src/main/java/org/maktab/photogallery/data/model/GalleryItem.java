package org.maktab.photogallery.data.model;

import com.google.gson.annotations.SerializedName;

public class GalleryItem {
    @SerializedName("id")
    private String mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("url_s")
    private String mUrl;
    @SerializedName("owner")
    private String mOwner;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }


    public GalleryItem() {
    }

    public GalleryItem(String id, String title, String url, String owner) {
        mId = id;
        mTitle = title;
        mUrl = url;
        mOwner = owner;
    }
}
