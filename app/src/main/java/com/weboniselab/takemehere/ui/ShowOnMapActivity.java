package com.weboniselab.takemehere.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weboniselab.takemehere.R;
import com.weboniselab.takemehere.adapters.PhotoAdapter;
import com.weboniselab.takemehere.network.Location;
import com.weboniselab.takemehere.util.Constants;
import com.weboniselab.takemehere.util.GPSTracker;

import java.util.ArrayList;

/**
 * Created by tejas.alsi on 9/6/2016.
 */
public class ShowOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private Intent mIntent;
    private String mPLaceName;
    private RecyclerView photosListView;
    private PhotoAdapter mPhotoAdapter;
    private ArrayList mPhotosList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_on_map);

        initUI();
    }

    private void initUI() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        supportMapFragment.getMapAsync(this);
        mIntent = getIntent();
        mPLaceName = mIntent.getStringExtra(Constants.PLACE_NAME);
        mPhotosList = mIntent.getParcelableArrayListExtra(Constants.PHOTOS);

        PhotoAdapter photoAdapter = PhotoAdapter.getInstance(this);
        photoAdapter.setPhotosList(mPhotosList);

        photosListView = (RecyclerView) findViewById(R.id.photos_list);
        photosListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosListView.hasFixedSize();
        photosListView.setItemAnimator(new DefaultItemAnimator());
        photosListView.setAdapter(photoAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        GPSTracker gpsTracker = new GPSTracker(ShowOnMapActivity.this);
        LatLng currentLatLang = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(getLocationDetails()).title(mPLaceName));
        mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(currentLatLang).title("My Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocationDetails(), 12.0f));

    }

    private LatLng getLocationDetails() {
        double lat = mIntent.getDoubleExtra(Constants.LATITUDE, 0.0);
        double lang = mIntent.getDoubleExtra(Constants.LONGITUDE, 0.0);
        LatLng latLng = new LatLng(lat, lang);

        return latLng;
    }
}
