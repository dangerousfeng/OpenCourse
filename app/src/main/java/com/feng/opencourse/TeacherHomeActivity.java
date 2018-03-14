package com.feng.opencourse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.feng.opencourse.adapter.CoursesListViewAdapter;
import com.feng.opencourse.entity.Course;
import com.feng.opencourse.entity.Section;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class TeacherHomeActivity extends AppCompatActivity {

    // // TODO: 2018/3/7 0007 服务端下发teacher info 并绑定数据;item中绑定face image; 
    private ImageView ivTeacherAvatar;
    private TextView tvTeacherName;
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
        ivTeacherAvatar = (ImageView) findViewById(R.id.iv_teacher_avatar);
        tvTeacherName = (TextView) findViewById(R.id.tv_teacher_name);
        tvTeacherInfo = (TextView) findViewById(R.id.tv_teacher_info);
        lvCourses = (ListView) findViewById(R.id.lv_teacher_courses);
        FloatingActionButton fabCreateCourse = (FloatingActionButton) findViewById(R.id.fab_create_course);

        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");

        initData();


        lvCourses.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clickCourseId = courseList.get(position).getCourseId();
                Intent toCourseDetail = new Intent(myapp.getApplicationContext(), CourseDetailActivity.class);
                toCourseDetail.putExtra("courseId",clickCourseId);
                startActivity(toCourseDetail);
            }
        } );
        fabCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCreateCourse = new Intent();
                toCreateCourse.setClass(TeacherHomeActivity.this,CreateCourseActivity.class);
                startActivity(toCreateCourse);
            }
        });
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
                        JSONObject teacherInfoJson = respDataJson.optJSONObject("teacherInfo");
                        
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
                                lvCourses.setAdapter(new CoursesListViewAdapter(myapp.getApplicationContext(),courseList,myapp));
                                tvTeacherName.setText(teacherInfoJson.optString("userName"));
                                String tvShow = "Email: " + teacherInfoJson.optString("email") + "\n";
                                tvTeacherInfo.setText(tvShow);
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


        Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
        String endpoint = proper.getProperty("OSS_ENDPOINT");
        OSS oss = new OSSClient(
                myapp.getApplicationContext(),
                endpoint,
                myapp.getReadOnlyOSSCredentialProvider());

        GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"), proper.getProperty("AVATAR_KEY") + teacherId );

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                ivTeacherAvatar.setImageBitmap(bitmap);
            }
            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

}
