package com.example.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.entity.Forecast;
import com.example.weather.entity.Weather;
import com.example.weather.service.AutoUpdateService;
import com.example.weather.util.AnalysisDataUtils;
import com.example.weather.util.HttpUtils;
import com.example.weather.util.PrefUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private ImageView bingPicImg;
    private Button navButton;
    public DrawerLayout drawerLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView airText;
    private TextView drsgText;
    private TextView fluText;
    private TextView travText;
    private TextView uvText;
    private ImageView imageViewWeather;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        imageViewWeather =  (ImageView) findViewById(R.id.image_view_weather);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        airText = (TextView) findViewById(R.id.air_text);
        drsgText = (TextView) findViewById(R.id.drsg_text);
        fluText = (TextView) findViewById(R.id.flu_text);
        travText = (TextView) findViewById(R.id.trav_text);
        uvText = (TextView) findViewById(R.id.uv_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        String weatherString = PrefUtils.getString(getApplicationContext(),"weather","");
        final String weatherId;
        if (!TextUtils.isEmpty(weatherString)) {
            // 有缓存时直接解析天气数据
            Weather weather = AnalysisDataUtils.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            //隐藏界面
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //手动请求
                requestWeather(weatherId);
            }
        });

        String bingPic = PrefUtils.getString(getApplicationContext(), "bing_pic", "");
        if (!TextUtils.isEmpty(bingPic)) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=b94f022367a74c4288dbc11c130b18ff";
        HttpUtils.getInstance().sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //解析天气数据
                final Weather weather = AnalysisDataUtils.handleWeatherResponse(responseText);
                Log.e("zz", "onResponse: "+weather.toString());
                //刷新数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            PrefUtils.putString(getApplicationContext(),"weather",responseText);
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);

                        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                        startService(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.getInstance().sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                //存储
                PrefUtils.putString(getApplicationContext(),"bing_pic",bingPic);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        //城市名
        String cityName = weather.basic.cityName;
        //更新时间
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        //温度
        String degree = weather.now.temperature + "℃";
        //天气情况
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText("更新时间:"+updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        String url = "https://cdn.heweather.com/cond_icon/"+ weather.now.more.code+".png";
        Glide.with(getApplicationContext()).load(url).into(imageViewWeather);
        //几天天气预报
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            //循环添加view到 forecastLayout中
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText("日期:"+forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText("高温:"+forecast.temperature.max);
            minText.setText("低温:"+forecast.temperature.min);
            forecastLayout.addView(view);
        }
        //空气质量
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        //生活建议
        String comfort = "舒适度指数：" +weather.suggestion.comfort.alert+" : "+ weather.suggestion.comfort.info;
        String carWash = "洗车指数：" +weather.suggestion.carWash.alert+" : "+ weather.suggestion.carWash.info;
        String sport = "运动指数："  +weather.suggestion.sport.alert+" : "+ weather.suggestion.sport.info;
        String air = "空气污染扩散条件指数："  +weather.suggestion.air.alert+" : "+ weather.suggestion.air.info;
        String flu = "感冒指数："  +weather.suggestion.flu.alert+" : "+ weather.suggestion.flu.info;
        String trav = "旅游指数："  +weather.suggestion.trav.alert+" : "+ weather.suggestion.trav.info;
        String uv = "紫外线指数："  +weather.suggestion.uv.alert+" : "+ weather.suggestion.uv.info;
        String drsg = "穿衣指数："  +weather.suggestion.drsg.alert+" : "+ weather.suggestion.drsg.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        airText.setText(air);
        fluText.setText(flu);
        travText.setText(trav);
        uvText.setText(uv);
        drsgText.setText(drsg);


        //显示界面
        weatherLayout.setVisibility(View.VISIBLE);
//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);
    }
}
