package com.mobiapp.nicewallpapers.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class CategoriesRequest {

    private String title;
    private String groupId;
}
