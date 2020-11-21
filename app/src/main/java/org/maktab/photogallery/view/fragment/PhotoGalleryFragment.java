package org.maktab.photogallery.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.maktab.photogallery.R;
import org.maktab.photogallery.adaptert.PhotoAdapter;
import org.maktab.photogallery.databinding.FragmentPhotoGalleryBinding;
import org.maktab.photogallery.view.EndlessRecyclerViewScrollListener;
import org.maktab.photogallery.data.model.GalleryItem;
import org.maktab.photogallery.viewmodel.PhotoGalleryViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final int SPAN_COUNT = 3;
    private FragmentPhotoGalleryBinding mBinding;
    private PhotoGalleryViewModel mViewModel;
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

        setHasOptionsMenu(true);
        mViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);
        mViewModel.fetchItems();
        setLiveDataObservers();
        mItems = new ArrayList<>();
        mCurrentItem = 0;
        mCount = 1;
//        fetchItemFromRepository();
    }

//    private void fetchItemFromRepository() {
//        mRepository.fetchItemsAsync(new PhotoRepository.Callbacks() {
//            @Override
//            public void onItemResponse(List<GalleryItem> items) {
//                setupAdapter(items);
//            }
//        });
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_photo_gallery,
                container,
                false);

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
        mBinding.recyclerViewPhotoGallery.addOnScrollListener(scrollListener);
        return mBinding.getRoot();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        setSearchViewListeners(searchView);

        MenuItem togglePollingItem = menu.findItem(R.id.menu_item_poll_toggling);
        if (mViewModel.isTaskScheduled()) {
            togglePollingItem.setTitle(R.string.stop_polling);
        } else {
            togglePollingItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                mViewModel.setQueryInPreferences(null);
                mViewModel.fetchItems();
                return true;
            case R.id.menu_item_poll_toggling:
                mViewModel.togglePolling();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        mGridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        mBinding.recyclerViewPhotoGallery.setLayoutManager(mGridLayoutManager);
    }

    private void setSearchViewListeners(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.fetchSearchItemsAsync(query);
                mViewModel.setQueryInPreferences(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = mViewModel.getQueryFromPreferences();
                if (query != null)
                    searchView.setQuery(query, false);
            }
        });
    }

    private void setLiveDataObservers() {
        mViewModel.getPopularItemsLiveData().observe(this, new Observer<List<GalleryItem>>() {
            @Override
            public void onChanged(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });
        mViewModel.getSearchItemsLiveData().observe(this, new Observer<List<GalleryItem>>() {
            @Override
            public void onChanged(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });
    }

    private void setupAdapter(List<GalleryItem> items) {
        PhotoAdapter adapter = new PhotoAdapter(mViewModel);
        mBinding.recyclerViewPhotoGallery.setAdapter(adapter);
        mBinding.recyclerViewPhotoGallery.scrollToPosition(mCurrentItem);
    }
}
