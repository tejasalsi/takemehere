package com.weboniselab.takemehere.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jarvis on 05/09/16.
 */

public interface ApiInterface {

    @GET("textsearch/json")
    Call<SearchResultResponse> getSearchList(@Query("query") String searchText, @Query("key") String apiKey);
}
