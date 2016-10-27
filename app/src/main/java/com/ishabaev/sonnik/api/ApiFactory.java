package com.ishabaev.sonnik.api;


import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class ApiFactory {

    private static OkHttpClient sClient;

    private static Api sApi;

    @NonNull
    public static Api getApi() {
        Api service = sApi;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sApi;
                if (service == null) {
                    service = sApi = createApi();
                }
            }
        }
        return service;
    }

    @NonNull
    private static Api createApi() {
        return new Retrofit.Builder()
                .baseUrl("http://www.sonnik.ru/")
                .client(getClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(Api.class);
    }

    @NonNull
    private static OkHttpClient getClient() {
        OkHttpClient client = sClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = sClient;
                if (client == null) {
                    client = sClient = buildClient();
                }
            }
        }
        return client;
    }

    @NonNull
    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
    }

}
