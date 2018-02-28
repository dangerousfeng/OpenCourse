package test;

import com.feng.opencourse.util.HttpUtil;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Call;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Windows 7 on 2018/2/7 0007.
 */

public class TestOkHttp {
    public static void main(String args[]) {
        //String req = "{'ActionId':'101','JWT':'sdsa','email':'mEmail','password':'mPassword'}";
        JSONObject json = new JSONObject();
        try {
            json.put("ActionId", 101);
            json.put("JWT", "this is jwt");
            json.put("email", "email");
            json.put("password", "password");
            String req = json.toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
            HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //  失败 处理异常
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}