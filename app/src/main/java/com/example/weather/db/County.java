package com.example.weather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    //本地数据库id  自增长
    public int id;
    //县的名字
    public String countyName;
    //访问api使用到的
    public String weatherId;
    //存储本地数据库的City 表中的 id字段对应
    public int cityId;
}
