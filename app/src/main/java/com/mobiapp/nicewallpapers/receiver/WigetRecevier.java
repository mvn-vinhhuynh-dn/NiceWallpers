package com.mobiapp.nicewallpapers.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobiapp.nicewallpapers.util.Utils;

public class WigetRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.setWallpaperRandom(context, true);
    }
}
