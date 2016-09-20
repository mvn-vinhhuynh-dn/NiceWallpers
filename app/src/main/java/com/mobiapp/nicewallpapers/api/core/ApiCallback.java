package com.mobiapp.nicewallpapers.api.core;

import android.util.Log;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * A custom Callback.
 *
 * @param <T> expected response type
 */
public abstract class ApiCallback<T> implements Callback<T> {
    private static final String TAG = ApiCallback.class.getSimpleName();

    public abstract void failure(RetrofitError retrofitError, ApiError apiError);

    @Override
    public void failure(RetrofitError error) {
        Log.e(TAG, "kind:" + error.getKind());
        switch (error.getKind()) {
            case NETWORK:
                failure(error, ApiError.ERROR_NETWORK);
                break;

            default:
                failure(error, ApiError.ERROR_PLEASE_TRY_LATER);
        }
    }
}
