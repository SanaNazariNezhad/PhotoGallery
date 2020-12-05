package org.maktab.photogallery.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;
import org.maktab.photogallery.DirectionsJSONParser;
import org.maktab.photogallery.R;
import org.maktab.photogallery.data.model.GalleryItem;
import org.maktab.photogallery.viewmodel.LocatrViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocatrFragment extends SupportMapFragment {

    public static final String TAG = "LocatrFragment";
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 0;
    private static final int NUMBER_OF_IMAGES = 3;

    private LocatrViewModel mViewModel;
    private LatLng mItemLatLng;
    private LatLng mEnd;
    private LatLng mStart;
    private Bitmap mItemBitmap;
    private GoogleMap mMap;
    ArrayList markerPoints = new ArrayList();

    public LocatrFragment() {
        // Required empty public constructor
    }

    public static LocatrFragment newInstance() {
        LocatrFragment fragment = new LocatrFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(LocatrViewModel.class);
        mViewModel.getSearchItemsLiveData().observe(this, new Observer<List<GalleryItem>>() {
            @Override
            public void onChanged(List<GalleryItem> galleryItems) {
                if (galleryItems == null || galleryItems.size() == 0)
                    return;

                GalleryItem[] images = new GalleryItem[NUMBER_OF_IMAGES];
                for (int i = 0; i < images.length; i++) {
                    images[i] = galleryItems.get(i);
                }
                for (int i = 0; i < images.length; i++) {
                    GalleryItem item = images[i];
                    mItemLatLng = new LatLng(item.getLat(), item.getLng());
                    Picasso.get()
                            .load(item.getUrl())
                            .placeholder(R.mipmap.ic_android_placeholder)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    mItemBitmap = bitmap;
                                    updateUI();
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                }

                updateUI();
            }

            /*public void onChanged(List<GalleryItem> galleryItems) {
                if (galleryItems == null || galleryItems.size() == 0)
                    return;

                GalleryItem item = galleryItems.get(0);
                mItemLatLng = new LatLng(item.getLat(), item.getLng());
                Picasso.get()
                        .load(item.getUrl())
                        .placeholder(R.mipmap.ic_android_placeholder)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                mItemBitmap = bitmap;
                                updateUI();
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                updateUI();
            }*/
        });

        mViewModel.getMyLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                updateUI();
            }
        });

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();
                listeners();
            }
        });
    }

    private void listeners() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mEnd = latLng;

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                /*if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }*/

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
//                if (markerPoints.size() >= 2) {
//                LatLng origin = (LatLng) markerPoints.get(0);
//                LatLng dest = (LatLng) markerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(mStart, mEnd);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
//                }

            }
        });
    }

    /*private void listeners() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mEnd = latLng;
                mMap.addMarker(new MarkerOptions().position(mEnd).title("destination"));
            }
        });
    }*/

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_locatr, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_location:
                if (hasLocationAccess()) {
                    requestLocation();
                } else {
                    //request Location access permission
                    requestLocationAccessPermission();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults == null || grantResults.length == 0)
                    return;

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestLocation();
                /*else
                    Toast.makeText(
                            getContext(),
                            "We do not have the location permission",
                            Toast.LENGTH_LONG).show();*/

                return;
        }
    }

    private boolean hasLocationAccess() {
        boolean isFineLocation = ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean isCoarseLocation = ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return isFineLocation && isCoarseLocation;
    }

    private void requestLocationAccessPermission() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        requestPermissions(permissions, REQUEST_CODE_PERMISSION_LOCATION);
    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        if (!hasLocationAccess())
            return;

        mViewModel.requestLocation();
    }

    private void updateUI() {
        Location location = mViewModel.getMyLocation().getValue();
        if (location == null || mMap == null || mItemLatLng == null || mItemBitmap == null)
            return;

        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mStart = myLatLng;
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(myLatLng)
                .include(mItemLatLng)
                .build();

        MarkerOptions myMarkerOptions = new MarkerOptions()
                .position(myLatLng)
                .title("My Location");

        MarkerOptions itemMarkerOptions = new MarkerOptions()
                .position(mItemLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(mItemBitmap))
                .title("Nearest Picture");

        mMap.addMarker(myMarkerOptions);
        mMap.addMarker(itemMarkerOptions);

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, margin);
        mMap.animateCamera(cameraUpdate);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}