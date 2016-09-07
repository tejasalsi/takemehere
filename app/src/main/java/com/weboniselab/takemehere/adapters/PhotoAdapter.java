package com.weboniselab.takemehere.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.weboniselab.takemehere.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tejas.alsi on 9/7/2016.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private static PhotoAdapter mPhotoAdapter= new PhotoAdapter();
    private ArrayList<Bitmap> mPhotoBitmaps;
    private static Context mContext;
    private PhotoAdapter() {
        //do nothing
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new PhotoViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {
        Bitmap photoBitmap = mPhotoBitmaps.get(position);
        holder.placeImage.setImageBitmap(photoBitmap);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap imageBitmap = mPhotoBitmaps.get(position);
                savePhotoToSDcard(imageBitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotoBitmaps.size();
    }

    public static PhotoAdapter getInstance(Context context) {
        mContext = context;
        return mPhotoAdapter;
    }

    public void setPhotosList(ArrayList bitmaps) {
        mPhotoBitmaps = bitmaps;
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView placeImage;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            placeImage = (ImageView) itemView.findViewById(R.id.place_photo);
        }
    }

    private void savePhotoToSDcard(Bitmap bitmapImage) {
        try
        {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "MeeguImages");
            sdCardDirectory.mkdirs();

            String imageNameForSDCard = "image_" + String.valueOf(new Random().nextInt(1000)) + System.currentTimeMillis() + ".jpg";

            File image = new File(sdCardDirectory, imageNameForSDCard);
            FileOutputStream outStream;

            outStream = new FileOutputStream(image);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
    /* 100 to keep full quality of the image */
            outStream.flush();
            outStream.close();



            //Refreshing SD card
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.sdcard_error), Toast.LENGTH_LONG).show();
        }
    }

}
