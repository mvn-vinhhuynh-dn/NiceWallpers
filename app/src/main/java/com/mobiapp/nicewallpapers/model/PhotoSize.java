package com.mobiapp.nicewallpapers.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PhotoSize {
    private Sizes sizes;
    private String stat;

    @Data
    public class Size {
        private String label;
        private String width;
        private String height;
        private String source;
        private String url;
        private String media;
    }

    @Data
    public class Sizes {
        private Integer canblog;
        private Integer canprint;
        private Integer candownload;
        private List<Size> size = new ArrayList<>();
    }
}
