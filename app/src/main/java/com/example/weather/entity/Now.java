package com.example.weather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxb.
 */
public class Now {
    //温度
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        //天气描述
        @SerializedName("txt")
        public String info;
        //天气图片代号
        public String code;

    }
}
