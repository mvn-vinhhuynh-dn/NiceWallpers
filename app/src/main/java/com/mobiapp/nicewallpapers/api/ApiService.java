package com.mobiapp.nicewallpapers.api;

import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.model.OEM;
import com.mobiapp.nicewallpapers.model.OEMDetail;
import com.mobiapp.nicewallpapers.model.PhotoSize;
import com.mobiapp.nicewallpapers.model.ResponePhoto;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ApiService {

    @GET("/rest/")
    void getPhotos(@Query("method") String method,
                   @Query("api_key") String api_key,
                   @Query("group_id") String group_id,
                   @Query("page") int page,
                   @Query("per_page") int per_page,
                   @Query("format") String json,
                   @Query("nojsoncallback") int nojsoncallback,
                   ApiCallback<ResponePhoto> response);

    @GET("/rest/")
    void getPhotoSize(@Query("method") String method,
                      @Query("api_key") String api_key,
                      @Query("photo_id") String photo_id,
                      @Query("format") String json,
                      @Query("nojsoncallback") int nojsoncallback,
                      ApiCallback<PhotoSize> response);

    @GET("/category")
    void getCategoryOEM(ApiCallback<List<OEM>> response);

    @GET("/{name}/wall")
    void getHomeOEM(@Path("name") String name, ApiCallback<List<OEMDetail>> response);
}
