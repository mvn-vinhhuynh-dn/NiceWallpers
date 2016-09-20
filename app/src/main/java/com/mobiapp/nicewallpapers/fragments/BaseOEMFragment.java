package com.mobiapp.nicewallpapers.fragments;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.api.ApiService;

import org.androidannotations.annotations.EFragment;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@EFragment
public abstract class BaseOEMFragment extends BaseFragment {
    protected ApiService mApiService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getString(R.string.base_url_oem))
                .setConverter(new GsonConverter(gson))
                .build();
        mApiService = restAdapter.create(ApiService.class);
    }
}
