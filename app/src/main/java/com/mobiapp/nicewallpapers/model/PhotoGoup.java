package com.mobiapp.nicewallpapers.model;

import java.io.File;
import java.io.Serializable;

import lombok.Data;

@Data
public class PhotoGoup implements Serializable {
    private String id;
    private String owner;
    private String secret;
    private String server;
    private int farm;
    private int title;
    private int ispublic;
    private int isfriend;
    private int isfamily;
    private File file;
    private String dateCreated;
    private String bitmap;
}
