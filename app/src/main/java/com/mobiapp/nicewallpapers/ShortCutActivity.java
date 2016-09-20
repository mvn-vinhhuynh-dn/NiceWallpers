package com.mobiapp.nicewallpapers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobiapp.nicewallpapers.receiver.WigetRecevier;

public class ShortCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, WigetRecevier.class);
        intent.setAction("action_next_wallpaper");
        sendBroadcast(intent);
        finish();
    }
}
