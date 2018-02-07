package test;

import com.feng.opencourse.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Windows 7 on 2018/2/7 0007.
 */

public class TestOkHttp {
    public static void main(String args[]) {
        HttpUtil.sendHttpRequest("aaa", new okhttp3.Callback() {
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