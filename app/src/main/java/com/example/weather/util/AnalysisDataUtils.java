package com.example.weather.util;

import android.text.TextUtils;

import com.example.weather.db.City;
import com.example.weather.db.County;
import com.example.weather.db.Province;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zxb.
 * 解析数据类
 */
public class AnalysisDataUtils {
    /**
     * 解析省数据
     * @param response
     * @return
     */
    public static boolean analysisiProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.provinceCode = jsonObject.getInt("id");
                    province.provinceName = jsonObject.getString("name");
                    province.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 解析市数据
     * @param response
     * @return
     */
    public static boolean analysisiCityResponse(String response,int provoinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.cityCode = jsonObject.getInt("id");
                    city.cityName = jsonObject.getString("name");
                    city.provinceId = provoinceId;
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 解析县数据
     * @param response
     * @return
     */
    public static boolean analysisiCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.countyCode = jsonObject.getInt("id");
                    county.countyName = jsonObject.getString("name");
                    county.cityId = cityId;
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
