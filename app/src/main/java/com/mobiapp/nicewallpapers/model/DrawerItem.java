package com.mobiapp.nicewallpapers.model;

import android.support.v4.app.Fragment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class DrawerItem {
    private String title;
    private int icon;
    private Fragment fragment;
}
