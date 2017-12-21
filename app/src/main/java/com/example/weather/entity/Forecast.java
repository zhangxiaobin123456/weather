package com.example.weather.entity;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        //最高温度
        public String max;
        //最低温度
        public String min;

    }

    public class More {
        //天气描述
        @SerializedName("txt_d")
        public String info;

    }

}
