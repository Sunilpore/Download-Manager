package com.example.sunil.downloadmanager10.api;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceClient {

    public static final String Base_URL="https://upload.wikimedia.org/";

    private static Retrofit retrofit=null;

    public static Retrofit getClient(){
        if(retrofit==null)
            retrofit=new Retrofit.Builder().baseUrl(Base_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofit;
    }

}
