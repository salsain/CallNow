package com.resiligence.callnow;

import android.support.multidex.MultiDexApplication;

import com.splunk.mint.Mint;

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
        Mint.initAndStartSession(getApplicationContext(), "6ca4b144");
    }
}
