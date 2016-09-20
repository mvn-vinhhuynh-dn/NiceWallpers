package com.mobiapp.nicewallpapers.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Photos {
    private int page;
    private int pages;
    private int perpage;
    private int total;
    private List<PhotoGoup> photo = new ArrayList<>();



}
