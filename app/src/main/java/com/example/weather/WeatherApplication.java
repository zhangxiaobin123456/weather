package com.example.weather;

import android.app.Application;

import org.litepal.LitePal;

public class WeatherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
