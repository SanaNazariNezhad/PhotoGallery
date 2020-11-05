package org.maktab.photogallery.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import org.maktab.photogallery.R;
import org.maktab.photogallery.adaptert.PhotoAdapter;
import org.maktab.photogallery.view.EndlessRecyclerViewScrollListener;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.viewmodel.PhotoGalleryViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final int SPAN_COUNT = 3;
    private RecyclerView mRecyclerView;
    private int mCount;
    private ProgressBar mProgressBar;
    List<GalleryItem> mItems;
    private int mCurrentItem;
    private EndlessRecyclerViewScrollListener scrollListener;
    GridLayoutManager mGridLayoutManager;
    private PhotoGalleryViewModel mViewModel;

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
        mCount = 1;
        mViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);

        mViewModel.fetchPopularItemsAsync();
        mViewModel.getPopularItemsLiveData().observe(this, new Observer<List<GalleryItem>>() {
            @Override
            public void onChanged(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });
//        fetchItemFromRepository();
    }

    /*private void fetchItemFromRepository() {
        mRepository.fetchItemsAsync(new PhotoRepository.Callbacks() {
            @Override
            public void onItemResponse(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });
    }*/

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
        PhotoAdapter adapter = new PhotoAdapter(getContext(),items);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mCurrentItem);
    }

/*    private class FlickrTask extends AsyncTask<Void, Void, List<GalleryItem>> {


        //this method runs on background thread
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            List<GalleryItem> items = mRepository.fetchItems(mCount);

            for (int index = 0; index <items.size() ; index++) {
                mItems.add(items.get(index));
            }

            return mItems;
        }

        //this method run on main thread
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);

            setupAdapter(items);
            mProgressBar.setVisibility(View.GONE);
        }

    }*/
}
