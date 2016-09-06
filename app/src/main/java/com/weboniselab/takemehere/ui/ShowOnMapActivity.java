package com.weboniselab.takemehere.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weboniselab.takemehere.R;
import com.weboniselab.takemehere.util.Constants;

/**
 * Created by tejas.alsi on 9/6/2016.
 */
public class ShowOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private Intent mIntent;
    private String mPLaceName;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.addMarker(new MarkerOptions().position(getLocationDetails()).title(mPLaceName));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                location.getLongitude())));

    }

    private LatLng getLocationDetails() {
        double lat = mIntent.getDoubleExtra(Constants.LATITUDE, 0.0);
        double lang = mIntent.getDoubleExtra(Constants.LONGITUDE, 0.0);
        LatLng latLng = new LatLng(lat, lang);

        return latLng;
    }
}
