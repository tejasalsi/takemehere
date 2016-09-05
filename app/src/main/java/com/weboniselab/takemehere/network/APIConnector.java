package com.weboniselab.takemehere.network;

/**
 * Created by tejas.alsi on 9/5/2016.
 */
public class APIConnector {

    public static ApiInterface getConnector() {
        ApiInterface apiInterface = RequestBuilder.apiBuilder().create(ApiInterface.class);
        return  apiInterface;
    }
}
