package com.feng.opencourse;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.opencourse.adapter.PlayRecordListViewAdapter;
import com.feng.opencourse.entity.PlayRecord;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class CourseDescFragment extends Fragment {

    private View view;
    private TextView tvName;
    private TextView tvTeacher;
    private TextView tvHot;
    private TextView tvTime;
    private TextView tvDesc;
    private FloatingActionButton fabCollectionCourse;

    private String courseId;
    private Activity myactivity;
    private MyApplication myapp;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myactivity = (CourseDetailActivity) activity;
        courseId = ((CourseDetailActivity) activity).getCourseId();//通过强转成宿主activity，就可以获取到传递过来的数据
        myapp = ((CourseDetailActivity) activity).getMyapp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_course_desc,container, false);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvTeacher = (TextView) view.findViewById(R.id.tv_teacher);
        tvHot = (TextView) view.findViewById(R.id.tv_hot);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvDesc = (TextView) view.findViewById(R.id.tv_desc);
        fabCollectionCourse = (FloatingActionButton) view.findViewById(R.id.fab_collection_course);
        fabCollectionCourse.setOnClickListener(collectionListener);

        if (isAdded()) {//判断Fragment已经依附Activity
            String courseDescJsonStr = getArguments().getString("courseDescJsonStr");
            try {
                JSONObject json = new JSONObject(courseDescJsonStr);
                tvName.setText(json.optString("courseName"));
                tvTeacher.setText(json.optString("teacherName"));
                tvHot.setText(String.valueOf(json.optInt("hot")));
                tvTime.setText(json.optString("createTime"));
                tvDesc.setText(json.optString("courseDesc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }
    View.OnClickListener collectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 请求服务 添加收藏记录
            fabCollectionCourse.setImageResource(R.drawable.heart_red);
            JSONObject json = new JSONObject();
            try {
                json.put("ActionId", 206);
                json.put("JWT", myapp.getJWT());
                json.put("userId", myapp.getUserId());
                json.put("courseId",courseId);
                String req = json.toString();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
                com.feng.opencourse.util.HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(myapp.getApplicationContext(), "获取播放记录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
