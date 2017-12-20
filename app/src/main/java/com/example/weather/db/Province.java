package com.example.weather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    //省id
    public int id;
    //省的名字
    public String provinceName;
    //省的代号
    public int provinceCode;
}
