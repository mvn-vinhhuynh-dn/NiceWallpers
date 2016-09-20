package com.mobiapp.nicewallpapers.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class OEMHome implements Serializable {
    private String header;
    private OEMDetail oemDetail;
}
