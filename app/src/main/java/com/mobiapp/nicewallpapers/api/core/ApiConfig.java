package com.mobiapp.nicewallpapers.api.core;

import android.content.Context;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiConfig {
    private Context context;
    private String baseUrl;
}
