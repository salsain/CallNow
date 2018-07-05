package com.cyril_rayan.callnow;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Айрат on 04.06.2017.
 */

public class MainApplication extends MultiDexApplication {
    private static MainApplication instance;
    volatile public static StaticMemory staticMemory;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
