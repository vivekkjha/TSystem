package com.vivek.tsystem.web.retrofit;


import com.vivek.tsystem.framework.datamodel.FlickrObj;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface FlickrApi {

    @GET("services/rest/")
    @Headers("No-Auth: true")
    Single<FlickrObj> getPhotos(@Query("method") String method, @Query("api_key") String apiKey, @Query("text") String text,
                                @Query("format") String format, @Query("nojsoncallback") int noJsonCallBack, @Query("per_page") int per_page, @Query("page") int page);

}
