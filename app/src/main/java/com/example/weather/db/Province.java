package com.example.weather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    //本地数据库id  自增长
    public int id;
    //省的名字
    public String provinceName;
    //访问api使用到的
    public int provinceCode;
}
