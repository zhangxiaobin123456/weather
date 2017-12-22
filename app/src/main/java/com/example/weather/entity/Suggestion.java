package com.example.weather.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxb.
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;
    //舒适度
    public class Comfort {
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }
    //洗车
    public class CarWash {
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }
    //运动
    public class Sport {
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }
    public AIR air;
    //空气质量
    public class AIR{
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }

    //穿衣指数
    public Drsg drsg;
    public class Drsg{
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }

    //感冒指数
    public Flu flu;
    public class Flu{
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }

    //旅游指数
    public Trav trav;
    public class Trav{
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }

    //紫外线指数
    public Uv uv;
    public class Uv{
        @SerializedName("txt")
        public String info;

        @SerializedName("brf")
        public String alert;
    }
}
