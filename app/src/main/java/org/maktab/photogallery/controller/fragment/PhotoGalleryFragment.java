package org.maktab.photogallery.controller.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.maktab.photogallery.R;
import org.maktab.photogallery.controller.EndlessRecyclerViewScrollListener;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.repository.PhotoRepository;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final int SPAN_COUNT = 3;
    private RecyclerView mRecyclerView;
    private PhotoRepository mRepository;
    private int mCount;
    private ProgressBar mProgressBar;
    List<GalleryItem> mItems;
    private int mCurrentItem;
    private EndlessRecyclerViewScrollListener scrollListener;
    GridLayoutManager mGridLayoutManager;

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
        mCurrentItem = 0;
        mRepository = new PhotoRepository();
        mCount = 1;
        fetchItemFromRepository();
    }

    private void fetchItemFromRepository() {
        mRepository.fetchItemsAsync(new PhotoRepository.Callbacks() {
            @Override
            public void onItemResponse(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        findViews(view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        initViews();
        scrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                mCurrentItem = totalItemsCount;
                loadNextDataFromApi(page);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        /*mCount = offset;
        mProgressBar.setVisibility(View.VISIBLE);
        if (mCount >= 10)
            mProgressBar.setVisibility(View.GONE);
        else {
            fetchItemFromRepository();
            mProgressBar.setVisibility(View.GONE);
        }*/
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_photo_gallery);
    }

    private void initViews() {
        mGridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
    }

    private void setupAdapter(List<GalleryItem> items) {
        PhotoAdapter adapter = new PhotoAdapter(items);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mCurrentItem);
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mImageViewItem;


        public PhotoHolder(@NonNull View itemView) {
            super(itemView);

            mImageViewItem = itemView.findViewById(R.id.item_image_view);
        }

        public void bindGalleryItem(GalleryItem item) {

            Glide.with(itemView)  //2
                    .load(item.getUrl()) //3
                    .centerCrop() //4
                    .placeholder(R.mipmap.ic_android_placeholder) //5
                    .into(mImageViewItem); //8
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mItems;

        public List<GalleryItem> getItems() {
            return mItems;
        }

        public void setItems(List<GalleryItem> items) {
            mItems = items;
        }

        public PhotoAdapter(List<GalleryItem> items) {
            mItems = items;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_photo_gallery,
                    parent,
                    false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            holder.bindGalleryItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

}
