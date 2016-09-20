package com.mobiapp.nicewallpapers.api.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobiapp.nicewallpapers.BuildConfig;
import com.mobiapp.nicewallpapers.api.ApiService;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Use to create RestAdapter with options in order to request API.
 * <p/>
 * Needs to call "init" in Application before using it.
 *
 */
public final class ApiClient {
    private static final String TAG = ApiClient.class.getSimpleName();
    private static final String HEADER_UA = "User-Agent";
    private static final String HEADER_AUTH = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";
    private static final int TIMEOUT_CONNECTION = 10000;

    private static ApiClient sInstance;
    private Context context;
    private ApiService service;

    /**
     * Custom header request.
     */
    private final RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader(HEADER_UA, createUserAgent());
            // TODO add token
            String accessToken = null;
            Log.i(TAG, "access_token:" + accessToken);
            if (!TextUtils.isEmpty(accessToken)) {
                request.addHeader(HEADER_AUTH, AUTH_PREFIX + accessToken);
            }
        }
    };

    public static synchronized ApiClient getInstance() {
        if (sInstance == null) {
            sInstance = new ApiClient();
        }
        return sInstance;
    }

    public static ApiService getService() {
        return getInstance().service;
    }

    private ApiClient() {
        // no-op
    }

    public void init(ApiConfig apiConfig) {
        context = apiConfig.getContext();

        BooleanAdapter booleanAdapter = new BooleanAdapter();
        IntegerAdapter integerAdapter = new IntegerAdapter();
        // init Gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, booleanAdapter)
                .registerTypeAdapter(boolean.class, booleanAdapter)
                .registerTypeAdapter(Integer.class, integerAdapter)
                .registerTypeAdapter(int.class, integerAdapter)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        // init OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS);

        // RestAdapter
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiConfig.getBaseUrl())
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();

        // init Service
        service = restAdapter.create(ApiService.class);
    }

    private String createUserAgent() {
        PackageManager pm = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "occurs error when creating user agent!!!");
        }
        return System.getProperty("http.agent") + " " + context.getPackageName() + "/" + versionName;
    }
}
