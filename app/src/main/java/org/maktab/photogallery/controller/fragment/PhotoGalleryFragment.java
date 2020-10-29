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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.maktab.photogallery.R;
import org.maktab.photogallery.controller.EndlessRecyclerViewScrollListener;
import org.maktab.photogallery.model.GalleryItem;
import org.maktab.photogallery.repository.PhotoRepository;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final int SPAN_COUNT = 3;
    private static final String TAG = "PGF";
    private RecyclerView mRecyclerView;
    private PhotoRepository mRepository;
    private String mPage;
    private int mCount;
    private ProgressBar mProgressBar;
    private FlickrTask mFlickrTask;

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

        mRepository = new PhotoRepository();
        mCount = 1;


        /*FlickrTask flickrTask = new FlickrTask();
        flickrTask.execute();*/

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FlickrFetcher flickrFetcher = new FlickrFetcher();
                try {
                    String response = flickrFetcher.getUrlString("https://www.digikala.com/");
                    Log.d(TAG, response);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(response);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        thread.start();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        findViews(view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        FlickrTask flickrTask = new FlickrTask();
        flickrTask.execute();
        initViews();
        scrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        mCount ++;
        mProgressBar.setVisibility(View.VISIBLE);
        if (mCount >= 10)
            mProgressBar.setVisibility(View.GONE);
        else {
            FlickrTask flickrTask = new FlickrTask();
            flickrTask.execute();
        }
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
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private GalleryItem mItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mItem = item;
            mTextView.setText(mItem.getTitle());
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
            TextView textView = new TextView(getContext());
            return new PhotoHolder(textView);
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

    private class FlickrTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override protected void onPreExecute() {
        }

        //this method runs on background thread
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            List<GalleryItem> items = mRepository.fetchItems(mCount);
            return items;
        }

        //this method run on main thread
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);

            setupAdapter(items);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}