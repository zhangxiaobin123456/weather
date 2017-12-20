package com.example.weather.db;

import org.litepal.crud.DataSupport;


public class City extends DataSupport {
    //本地数据库id  自增长
    public int id;
    //城市的名字
    public String cityName;
    //访问api使用到的
    public int cityCode;
    //存储本地数据库的Province 表中的 id字段对应
    public int provinceId;
}
