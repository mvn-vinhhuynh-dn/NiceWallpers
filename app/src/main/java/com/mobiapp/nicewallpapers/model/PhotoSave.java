package com.mobiapp.nicewallpapers.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "photo")
@AllArgsConstructor
public class PhotoSave extends SugarRecord {
    public PhotoSave() {
    }

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
}
