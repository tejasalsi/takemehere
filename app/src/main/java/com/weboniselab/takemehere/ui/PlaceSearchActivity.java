package com.weboniselab.takemehere.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.weboniselab.takemehere.R;
import com.weboniselab.takemehere.adapters.SearchAdapter;
import com.weboniselab.takemehere.network.APIConnector;
import com.weboniselab.takemehere.network.Result;
import com.weboniselab.takemehere.network.SearchResultResponse;
import com.weboniselab.takemehere.util.Constants;
import com.weboniselab.takemehere.util.SearchHistoryProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceSearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Location mLocation;
    private List<Result> mSearchResultList;
    private RecyclerView mPlaceListView;
    private SearchAdapter mSearchAdapter;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        saveSearchQuery(getIntent());
        initUI();
        checkForLocationServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setQueryRefinementEnabled(true);

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        saveSearchQuery(intent);
    }

    //initiate your first activity
    private void initUI() {

        mPlaceListView = (RecyclerView) findViewById(R.id.place_list);
        mPlaceListView.setLayoutManager(new LinearLayoutManager(this));
        mPlaceListView.hasFixedSize();
        mPlaceListView.setItemAnimator(new DefaultItemAnimator());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.FINE_LOCATION_REQUEST_CODE);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }




    private void saveSearchQuery(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mQuery = query;
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchHistoryProvider.AUTHORITY, SearchHistoryProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            generateQueryResult(query, mLocation);
        }

    }

    private void generateQueryResult(String query, Location location) {
        String position = null;
        if(location != null) {
            position = location.getLatitude() + "," + location.getLongitude();
        }
        Call<SearchResultResponse> call = APIConnector.getConnector().getSearchList(query, position,
                getResources().getString(R.string.places_web_service_key));

        call.enqueue(new Callback<SearchResultResponse>() {
            @Override
            public void onResponse(Call<SearchResultResponse> call, Response<SearchResultResponse> response) {
                mSearchResultList = response.body().getResults();
                mSearchAdapter = SearchAdapter.getInstance(PlaceSearchActivity.this);
                mSearchAdapter.setSearchList(mSearchResultList);
                mPlaceListView.setAdapter(mSearchAdapter);
            }

            @Override
            public void onFailure(Call<SearchResultResponse> call, Throwable t) {
                Toast.makeText(PlaceSearchActivity.this, getResources().getString(R.string.failed_response),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForLocationServices() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {

            final AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle(getResources().getString(R.string.location_service));
            alert.setMessage(getResources().getString(R.string.enable_gps));
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.dismiss();
                }
            });
            alert.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case Constants.FINE_LOCATION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(PlaceSearchActivity.this, getResources().getString(R.string.permission_error),
                            Toast.LENGTH_SHORT).show();
                }
        }

    }
}
