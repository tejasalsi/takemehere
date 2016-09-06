package com.weboniselab.takemehere.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.weboniselab.takemehere.R;
import com.weboniselab.takemehere.network.Result;
import com.weboniselab.takemehere.ui.ShowOnMapActivity;
import com.weboniselab.takemehere.util.Constants;

import java.util.List;

/**
 * Created by tejas.alsi on 9/6/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Result> searchList;
    private static SearchAdapter searchAdapter = new SearchAdapter();
    private static Context mContext;

    private SearchAdapter() {
        //nothing to do here.
    }

    public static SearchAdapter getInstance(Context context) {
        mContext = context;
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

                Intent showOnMapIntent = new Intent(mContext, ShowOnMapActivity.class);
                showOnMapIntent.putExtra(Constants.LATITUDE, lat);
                showOnMapIntent.putExtra(Constants.LONGITUDE, lang);
                showOnMapIntent.putExtra(Constants.PLACE_NAME, placeResult.getName());
                showOnMapIntent.putExtra(Constants.PHOTOS_URL, placeResult.getPhotos().get(position));
                mContext.startActivity(showOnMapIntent);

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
}
