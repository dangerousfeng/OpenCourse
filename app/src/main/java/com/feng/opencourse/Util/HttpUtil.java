package com.feng.opencourse.util;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Properties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * Created by Windows 7 on 2018/2/7 0007.
 *  http请求
 */


public class HttpUtil {

    private static void sendAsyncRequest(String address, RequestBody requestBody,okhttp3.Callback callback){

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendAsyncRequest(RequestBody requestBody,okhttp3.Callback callback){
        Properties proper = ProperTies.getProperties(MyApplication.getContextObject());
        String address = proper.getProperty("serverUrl");
        sendAsyncRequest(address,requestBody,callback);
    }

    private static String sendSyncRequest(String address, RequestBody requestBody) throws IOException, JSONException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        okhttp3.Response resp =  okHttpClient.newCall(request).execute();
        System.out.println("=-=-=-=-=-=-=-=-=-=-=");
        return getRespData(resp);

    }

    public static String sendSyncRequest(RequestBody requestBody) throws IOException, JSONException {

        Properties proper = ProperTies.getProperties(MyApplication.getContextObject());
        String address = proper.getProperty("serverUrl");
        return sendSyncRequest(address, requestBody);
    }

    // 返回响应的body,jsonObject类型
    public static String  getRespData(okhttp3.Response resp) throws IOException, JSONException{

        String responseData =  resp.body().string();
        JSONObject jsonResp = new JSONObject(responseData);

        String responseBody = jsonResp.optString("body");
        JSONObject respBodyJsonObj = new JSONObject(responseBody);

        int stat = respBodyJsonObj.optInt("Stat");
        System.out.println("stat"+stat);
        if(stat != 0){
            return null;
        }
        return respBodyJsonObj.optString("Data");
    }
}



