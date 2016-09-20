package com.mobiapp.nicewallpapers.eventbus;

import com.squareup.otto.Bus;

public class BusProvider {
    private static Bus sBus = new Bus();

    public static Bus getInstance() {
        return sBus;
    }
}
