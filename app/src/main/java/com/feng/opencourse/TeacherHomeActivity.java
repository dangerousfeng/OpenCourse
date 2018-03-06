package com.feng.opencourse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.opencourse.adapter.CoursesListViewAdapter;
import com.feng.opencourse.entity.Course;
import com.feng.opencourse.entity.Section;
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

public class TeacherHomeActivity extends AppCompatActivity {

    // // TODO: 2018/3/7 0007 服务端下发teacher info 并绑定数据;item中绑定face image; 
    private TextView tvTeacherInfo;
    private ListView lvCourses;
    private String teacherId;
    
    private ArrayList<Course> courseList;

    private MyApplication myapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        myapp = (MyApplication) getApplication();
        tvTeacherInfo = (TextView) findViewById(R.id.tv_teacher_info);
        lvCourses = (ListView) findViewById(R.id.lv_teacher_courses);
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");

        initData();
    }

    private void initData() {
        JSONObject json = new JSONObject();
        try {
            json.put("ActionId", 202);
            json.put("JWT", myapp.getJWT());
            json.put("userId", teacherId);
            String req = json.toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
            com.feng.opencourse.util.HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(TeacherHomeActivity.this, "获取老师主页失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String respDataStr = getRespData(response);
                        JSONObject respDataJson = new JSONObject(respDataStr);
                        String coursesJsonStr = respDataJson.optString("createdCourses");
                        
                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(coursesJsonStr).getAsJsonArray();

                        Gson gson = new Gson();
                        courseList = new ArrayList<>();

                        //加强for循环遍历JsonArray
                        for (JsonElement cour : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            Course course = gson.fromJson(cour, Course.class);
                            courseList.add(course);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lvCourses.setAdapter(new CoursesListViewAdapter(myapp.getApplicationContext(),courseList));
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
