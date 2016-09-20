package com.mobiapp.nicewallpapers.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    private static final String MyPREFERENCES = "MyPreferences";

    public static final String APP_FIRST_INSTALL = "app_first_run";
    public static final String CHOOSE_SOURCE_FIRST_SHOW = "choose_source_first_show";
    public static final String AUTO_CHANGE = "ato_change";
    public static final String DURATION = "duration";
    public static final String AUTO_CHANGE_WITH_WIFI = "wifi";
    public static final String AUTO_CHANGE_WALLPAPERS = "auto_change_wallpapers";

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return pref.getBoolean(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return pref.getInt(key, defaultValue);
    }

}
