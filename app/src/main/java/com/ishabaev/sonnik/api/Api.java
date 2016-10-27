package com.ishabaev.sonnik.api;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {

    String COF = "FORID:9";
    String IE = "UTF-8";
    String CX = "partner-pub-2659822908602005:5071584033";

    @GET("search.php")
    Observable<Response<ResponseBody>> search(@Query("q") String q,
                                              @Query("ie") String ie,
                                              @Query("cof") String cof,
                                              @Query("key") String key,
                                              @Query("cx") String cx);

    @GET("articles/{id}.html")
    Observable<Response<ResponseBody>> article(@Path("id") String id);

}
