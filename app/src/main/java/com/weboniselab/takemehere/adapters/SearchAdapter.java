package com.weboniselab.takemehere.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.weboniselab.takemehere.R;
import com.weboniselab.takemehere.network.APIConnector;
import com.weboniselab.takemehere.network.Photo;
import com.weboniselab.takemehere.network.Result;
import com.weboniselab.takemehere.ui.ShowOnMapActivity;
import com.weboniselab.takemehere.util.Constants;
import com.weboniselab.takemehere.util.LoadBitmaps;
import com.weboniselab.takemehere.util.LoadPhotosTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tejas.alsi on 9/6/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements LoadBitmaps {

    private List<Result> searchList;
    private static SearchAdapter searchAdapter = new SearchAdapter();
    private static Context mContext;
    private static GoogleApiClient mGoogleAPIClient;
    private List<Bitmap> mPlacePhotos;
    private Intent mShowOnMapIntent;

    private SearchAdapter() {
        //nothing to do here.
    }

    public static SearchAdapter getInstance(Context context, GoogleApiClient googleApiClient) {
        mContext = context;
        mGoogleAPIClient = googleApiClient;
        return searchAdapter;
    }

    public void setSearchList(List<Result> searchList) {
        this.searchList = searchList;
    }
    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View placeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlist_item, parent, false);
        return new SearchViewHolder(placeView);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        final Result placeResult = searchList.get(position);
        holder.placeName.setText(placeResult.getName());
        holder.placeAddress.setText(placeResult.getFormattedAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = placeResult.getGeometry().getLocation().getLat();
                double lang = placeResult.getGeometry().getLocation().getLng();

                mShowOnMapIntent = new Intent(mContext, ShowOnMapActivity.class);
                mShowOnMapIntent.putExtra(Constants.LATITUDE, lat);
                mShowOnMapIntent.putExtra(Constants.LONGITUDE, lang);
                mShowOnMapIntent.putExtra(Constants.PLACE_NAME, placeResult.getName());
                getPhotos(placeResult.getPlaceId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView placeName, placeAddress;
        public SearchViewHolder(final View itemView) {
            super(itemView);

            placeName = (TextView) itemView.findViewById(R.id.place_title);
            placeAddress  = (TextView) itemView.findViewById(R.id.place_address);
        }
    }

    private void getPhotos(String placeID) {
        new LoadPhotosTask(100, 100, mGoogleAPIClient, this, mContext).execute(placeID);
    }

    @Override
    public void getBitmaps(List<Bitmap> bitmaps) {
        mPlacePhotos = bitmaps;
        mShowOnMapIntent.putParcelableArrayListExtra(Constants.PHOTOS, (ArrayList<? extends Parcelable>) mPlacePhotos);
        mContext.startActivity(mShowOnMapIntent);
    }
}
