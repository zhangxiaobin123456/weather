package com.example.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.entity.Weather;
import com.example.weather.util.AnalysisDataUtils;
import com.example.weather.util.HttpUtils;
import com.example.weather.util.PrefUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自动更新天气服务
 */
public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //刷新数据，刷新每日一图
        updateWeather();
        updateBingPic();

        //使用AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long elapsedRealtime = SystemClock.elapsedRealtime() + 8 * 60 * 60 * 1000;
        Intent alarmIntent = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,alarmIntent,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,elapsedRealtime,pi);
        }else{
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,elapsedRealtime,pi);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 保存 天气数据到sharedPreferences中
     */
    private void updateWeather() {
        String weatherString = PrefUtils.getString(getApplicationContext(),"weather","");
        final String weatherId;
        if (!TextUtils.isEmpty(weatherString)) {
            // 有缓存时直接解析天气数据
            Weather weatherCache = AnalysisDataUtils.handleWeatherResponse(weatherString);
            weatherId = weatherCache.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=b94f022367a74c4288dbc11c130b18ff";
            HttpUtils.getInstance().sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    //解析天气数据
                    final Weather weather = AnalysisDataUtils.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        PrefUtils.putString(AutoUpdateService.this,"weather",responseText);
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }



    }

    /**
     * 保存bing每日一图到 sharedPreferences中
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.getInstance().sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                //存储
                PrefUtils.putString(AutoUpdateService.this,"bing_pic",bingPic);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
