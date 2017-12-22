package com.ad.weather;

import android.app.Application;

/**
 * Created by anastasiia on 22.12.17.
 */

public class Weather extends Application {
    private static Weather instance;

    public static Weather getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
