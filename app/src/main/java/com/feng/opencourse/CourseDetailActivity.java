package com.feng.opencourse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.feng.opencourse.adapter.MyFragmentPagerAdapter;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class CourseDetailActivity extends AppCompatActivity {

    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private TabLayout mTabLayout;
    private ViewPager attrViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;

    private TabLayout.Tab mTab1;
    private TabLayout.Tab mTab2;
    private TabLayout.Tab mTab3;

    private String courseId;
    private String courseFacePath;
    private String courseDescJsonStr;
    private String sectionsJsonStr;
    private String commentJsonStr;
    private MyApplication myapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        courseFacePath = intent.getStringExtra("courseFacePath");

        myapp = (MyApplication) getApplication();

        initData();

        attrViewPager = (ViewPager) findViewById(R.id.vp_attr);
        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.vp_face);

        //// TODO: 2018/3/7 0007 courseFacePath == null,fragment中填充image
//        jzVideoPlayerStandard.setUp(sectionOnePath
//                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, sectionOneName);

        if (courseFacePath == null){
            Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
            String endpoint = proper.getProperty("OSS_ENDPOINT");
            OSS oss = new OSSClient(
                    myapp.getApplicationContext(),
                    endpoint,
                    myapp.getReadOnlyOSSCredentialProvider());

            GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"), courseId + proper.getProperty("FACE_KEY"));

            OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
                @Override
                public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                    // 请求成功
                    InputStream inputStream = result.getObjectContent();
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    jzVideoPlayerStandard.thumbImageView.setImageBitmap(bitmap);
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

        }else {
            Picasso.with(CourseDetailActivity.this).load(new File(courseFacePath)).into(jzVideoPlayerStandard.thumbImageView);
        }

    }

    private void initData() {
        //// TODO: 2018/3/5 0005 根据courseId获取课程小节,评论;
        JSONObject json = new JSONObject();
        try {
            json.put("ActionId", 204);
            json.put("JWT", myapp.getJWT());
            json.put("userId", myapp.getUserId());
            json.put("courseId", courseId);

            String req = json.toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
            com.feng.opencourse.util.HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(CourseDetailActivity.this, "获取课程详情失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String respDataStr = getRespData(response);
                        JSONObject respDataJson = new JSONObject(respDataStr);
                        courseDescJsonStr = respDataJson.optString("course");
                        sectionsJsonStr = respDataJson.optString("sections");
                        commentJsonStr = respDataJson.optString("comments");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                                        courseDescJsonStr, sectionsJsonStr, commentJsonStr);
                                //给ViewPager设置适配器
                                attrViewPager.setAdapter(mFragmentPagerAdapter);
                                //tablayout与Viewpager绑定
                                mTabLayout = (TabLayout) findViewById(R.id.tl_attr);
                                mTabLayout.setupWithViewPager(attrViewPager);

                                mTab1 = mTabLayout.getTabAt(0);
                                mTab2 = mTabLayout.getTabAt(1);
                                mTab2 = mTabLayout.getTabAt(2);
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
    @Override
    protected void onDestroy() {
        jzVideoPlayerStandard.releaseAllVideos();
        super.onDestroy();
    }
}
