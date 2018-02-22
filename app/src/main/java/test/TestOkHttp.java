package test;

import com.feng.opencourse.util.HttpUtil;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Call;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Windows 7 on 2018/2/7 0007.
 */

public class TestOkHttp {
    public static void main(String args[]) {
        RequestBody requestBody = new FormBody.Builder()
                .add("key","value")
                .build();
        HttpUtil.sendHttpRequest( requestBody,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //  失败 处理异常
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
            }
        });
    }
}