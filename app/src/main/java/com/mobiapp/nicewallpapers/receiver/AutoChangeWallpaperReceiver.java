package com.mobiapp.nicewallpapers.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.mobiapp.nicewallpapers.util.PreferenceUtil;
import com.mobiapp.nicewallpapers.util.Utils;

public class AutoChangeWallpaperReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEnbale = PreferenceUtil.getBoolean(context, PreferenceUtil.AUTO_CHANGE, false);
        boolean isAutoChangeWithWifi = PreferenceUtil.getBoolean(context,
                PreferenceUtil.AUTO_CHANGE_WITH_WIFI, false);
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (!isEnbale || (isAutoChangeWithWifi && !isWifi)) {
            return;
        }
        boolean isChange = PreferenceUtil.getBoolean(context, PreferenceUtil.AUTO_CHANGE_WALLPAPERS, false);
        if (!isChange) {
            PreferenceUtil.saveBoolean(context, PreferenceUtil.AUTO_CHANGE_WALLPAPERS, true);
            return;
        }
        Utils.setWallpaperRandom(context, false);
    }
}
