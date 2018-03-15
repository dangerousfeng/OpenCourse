package com.feng.opencourse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.feng.opencourse.adapter.CoursesListViewAdapter;
import com.feng.opencourse.entity.Course;
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

public class ClassifyActivity extends AppCompatActivity {
    private ListView lvClassify;
    private ArrayList<Course> courseList;
    private MyApplication myapp;
    private int clickType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        myapp = (MyApplication) getApplication();
        lvClassify = (ListView) findViewById(R.id.lv_classify);
        Intent intent = getIntent();
        clickType = intent.getIntExtra("clickType",1);

        initData();

        lvClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickCourseId = courseList.get(position).getCourseId();
                Intent toCourseDetail = new Intent(myapp.getApplicationContext(), CourseDetailActivity.class);
                toCourseDetail.putExtra("courseId",clickCourseId);
                startActivity(toCourseDetail);
            }
        });
    }

    private void initData() {
        JSONObject json = new JSONObject();
        try {
            json.put("ActionId", 208);
            json.put("JWT", myapp.getJWT());
            json.put("userId", myapp.getUserId());
            json.put("type",clickType);
            String req = json.toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
            com.feng.opencourse.util.HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(ClassifyActivity.this, "获取分类列表失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String respDataStr = getRespData(response);
                        JSONObject respDataJson = new JSONObject(respDataStr);
                        String typeCourseJsonStr = respDataJson.optString("typeCourses");
                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(typeCourseJsonStr).getAsJsonArray();

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
                                lvClassify.setAdapter(new CoursesListViewAdapter(myapp.getApplicationContext(),courseList,myapp));
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
