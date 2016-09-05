package com.weboniselab.takemehere.network;

import com.weboniselab.takemehere.util.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jarvis on 05/09/16.
 */

public class RequestBuilder {

    private static Retrofit mRetrofit = null;

    public static Retrofit apiBuilder() {
        if(mRetrofit == null) {
            mRetrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL).build();
        }
        return mRetrofit;
    }
}
