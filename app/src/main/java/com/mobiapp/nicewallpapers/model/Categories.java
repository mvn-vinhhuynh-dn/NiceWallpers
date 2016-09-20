package com.mobiapp.nicewallpapers.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class Categories {

    private String groupId;
    private String title;
    private PhotoGoup photo;
}
