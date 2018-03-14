package com.feng.opencourse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.feng.opencourse.adapter.CoursesListViewAdapter;
import com.feng.opencourse.adapter.PlayRecordListViewAdapter;
import com.feng.opencourse.entity.Course;
import com.feng.opencourse.entity.PlayRecord;
import com.feng.opencourse.util.MyApplication;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class PlayRecordActivity extends AppCompatActivity {

    private ListView lvPlayRecord;
    private ArrayList<PlayRecord> playRecordList;
    private MyApplication myapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record);

        myapp = (MyApplication) getApplication();
        lvPlayRecord = (ListView) findViewById(R.id.lv_play_record);
        initData();
        lvPlayRecord.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clickCourseId = playRecordList.get(position).getCourseId();
                Intent toCourseDetail = new Intent(myapp.getApplicationContext(), CourseDetailActivity.class);
                toCourseDetail.putExtra("courseId",clickCourseId);
                startActivity(toCourseDetail);
            }
        } );
    }

    private void initData() {

        JSONObject json = new JSONObject();
        try {
            json.put("ActionId", 999999999);
            json.put("JWT", myapp.getJWT());
            json.put("userId", myapp.getUserId());
            String req = json.toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
            com.feng.opencourse.util.HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(PlayRecordActivity.this, "获取播放记录失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String respDataStr = getRespData(response);
                        JSONObject respDataJson = new JSONObject(respDataStr);
                        String playRecordJsonStr = respDataJson.optString("playRecords");


                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(playRecordJsonStr).getAsJsonArray();

                        Gson gson = new Gson();
                        playRecordList = new ArrayList<>();

                        //加强for循环遍历JsonArray
                        for (JsonElement record : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            PlayRecord playRecord = gson.fromJson(record, PlayRecord.class);
                            playRecordList.add(playRecord);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lvPlayRecord.setAdapter(new PlayRecordListViewAdapter(myapp.getApplicationContext(),playRecordList,myapp));

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
