package com.mobiapp.nicewallpapers.api;

import com.mobiapp.nicewallpapers.api.core.ApiCallback;
import com.mobiapp.nicewallpapers.api.core.ApiClient;
import com.mobiapp.nicewallpapers.model.PhotoSize;
import com.mobiapp.nicewallpapers.model.ResponePhoto;

public final class ApiRequest {

    private static final String METHOD = "flickr.groups.pools.getPhotos";
    private static final String METHOD_PHOTO_SIZE = "flickr.photos.getSizes";
    private static final String API_KEY = "bd4c8a9d94a9617ca85ee7dbfd89f49c";
    private static final String FORMAT = "json";
    private static final int PER_PAGE = 20;
    private static final int NOJSONCALLBACK = 1;

    public static void getPhotos(int page, String groupId, ApiCallback<ResponePhoto> callback) {
        ApiClient.getService().getPhotos(METHOD, API_KEY, groupId, page, PER_PAGE,
                FORMAT, NOJSONCALLBACK, callback);
    }

    public static void getPhotosRandom(String groupId, ApiCallback<ResponePhoto> callback) {
        ApiClient.getService().getPhotos(METHOD, API_KEY, groupId, 1, 100,
                FORMAT, NOJSONCALLBACK, callback);
    }

    public static void getPhotosCategories(String groupId, ApiCallback<ResponePhoto> callback) {
        ApiClient.getService().getPhotos(METHOD, API_KEY, groupId, 1, 1,
                FORMAT, NOJSONCALLBACK, callback);
    }

    public static void getPhotoSize(String photoId, ApiCallback<PhotoSize> callback) {
        ApiClient.getService().getPhotoSize(METHOD_PHOTO_SIZE, API_KEY, photoId,
                FORMAT, NOJSONCALLBACK, callback);
    }
}
