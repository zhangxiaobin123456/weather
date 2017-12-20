package com.example.weather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    //县id
    public int id;
    //县的名字
    public String countyName;
    //县的代号
    public int countyCode;
    //所属城市的id
    public int cityId;
}
