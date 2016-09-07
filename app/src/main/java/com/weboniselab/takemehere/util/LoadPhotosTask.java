package com.weboniselab.takemehere.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejas.alsi on 9/7/2016.
 */
public class LoadPhotosTask extends AsyncTask<String, Void, List<Bitmap>> {

    private int mHeight;
    private int mWidth;
    private GoogleApiClient mGoogleApiClient;
    private LoadBitmaps mLoadBitmaps;
    private List<Bitmap> bitmaps = new ArrayList();
    private Context mContext;
    private ProgressDialog dialog;

    public LoadPhotosTask(int width, int height, GoogleApiClient googleApiClient, LoadBitmaps loadBitmaps, Context context) {
        mHeight = height;
        mWidth = width;
        mGoogleApiClient = googleApiClient;
        mLoadBitmaps = loadBitmaps;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Fetching your request...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected List<Bitmap> doInBackground(String... params) {

        if (params.length != 1) {
            return null;
        }

        final String placeId = params[0];

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                PlacePhotoMetadata photo;
                for(int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                    photo = photoMetadataBuffer.get(i);
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    bitmaps.add(image);
                }
            }
            photoMetadataBuffer.release();
        }
        return bitmaps;
    }

    @Override
    protected void onPostExecute(List<Bitmap> bitmaps) {
        if (bitmaps != null) {
            mLoadBitmaps.getBitmaps(bitmaps);
        }
        dialog.dismiss();
    }
}
