package com.mobiapp.nicewallpapers.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "chooseSources")
public class ChooseSources extends SugarRecord {

    public ChooseSources() {
    }

    private boolean checked;
    private String title;
}
