package com.feng.opencourse.util;

import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Windows 7 on 2018/2/7 0007.
 *  http请求
 */


public class HttpUtil {

    public static void sendHttpRequest(String address, RequestBody requestBody,okhttp3.Callback callback){

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendHttpRequest(RequestBody requestBody,okhttp3.Callback callback){
        Properties proper = ProperTies.getProperties(MyApplication.getContextObject());
        String address = proper.getProperty("serverUrl");
        sendHttpRequest(address,requestBody,callback);
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(address)
//                .build();
//        okHttpClient.newCall(request).enqueue(callback);
    }

}



