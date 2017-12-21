package com.example.weather.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    //状态
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    //三天天气预报集合
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
