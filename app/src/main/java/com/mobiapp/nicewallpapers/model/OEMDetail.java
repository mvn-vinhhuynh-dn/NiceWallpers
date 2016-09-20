package com.mobiapp.nicewallpapers.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class OEMDetail implements Serializable {
    private int number;
    private int size;
    private String thumb;
    private String name;
    private String link;
    private String id;
    private String lastupdate;
    private String linkCompressed;
    private String category;
    private String wall;
}
