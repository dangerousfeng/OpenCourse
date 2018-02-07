package com.feng.opencourse.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Windows 7 on 2018/2/7 0007.
 *  http请求
 */


public class HttpUtil {

    public static void sendHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}



