package com.example.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.weather.util.PrefUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //不为空的情况下直接跳转到天气详情
        String weather = PrefUtils.getString(getApplicationContext(), "weather", "");
        if (!TextUtils.isEmpty(weather)){
            Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
