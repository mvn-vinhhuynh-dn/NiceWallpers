package com.mobiapp.nicewallpapers;

import android.app.Application;

import com.mobiapp.nicewallpapers.api.core.ApiClient;
import com.mobiapp.nicewallpapers.api.core.ApiConfig;
import com.orm.SugarContext;

public class BaseApp extends Application {

    private static BaseApp sInstance;

    public static synchronized BaseApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        // setup service
        createService();

        // setup database
        SugarContext.init(getApplicationContext());
    }

    private void createService() {
        ApiConfig apiConfig = ApiConfig.builder()
                .context(getApplicationContext())
                .baseUrl(getApplicationContext().getString(R.string.base_url))
                .build();
        ApiClient.getInstance().init(apiConfig);
    }
}
