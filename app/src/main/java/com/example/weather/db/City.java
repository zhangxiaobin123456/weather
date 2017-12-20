package com.example.weather.db;

import org.litepal.crud.DataSupport;


public class City extends DataSupport {
    //城市id
    public int id;
    //城市的名字
    public String cityName;
    //城市的代号
    public int cityCode;
    //所属省份的id
    public int procinceId;
}
