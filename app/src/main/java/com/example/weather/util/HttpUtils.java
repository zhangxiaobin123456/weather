package com.example.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zxb.
 */
public class HttpUtils {

    private OkHttpClient mOkHttpClient;
    private Request mRequest;

    private  void _sendOkHttpRequest(String url, okhttp3.Callback callback){
        mRequest = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(mRequest).enqueue(callback);
    }
    private HttpUtils(){
        mOkHttpClient = new OkHttpClient();
    }
    public static HttpUtils mHttpUtils = null;
    public static HttpUtils getInstance() {
        synchronized (HttpUtils.class) {
            if (mHttpUtils == null) {
                mHttpUtils = new HttpUtils();
            }
        }
        return mHttpUtils;
    }

    public void sendOkHttpRequest(String url, okhttp3.Callback callback){
        _sendOkHttpRequest(url,callback);
    }

}
