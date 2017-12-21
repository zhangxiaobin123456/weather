package com.example.weather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxb.
 */
public class Basic {

    @SerializedName("city")
    public String cityName;//城市名
    @SerializedName("id")
    public String weatherId;//城市代号
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;  //数据更新时间
    }
}
