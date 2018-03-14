package com.feng.opencourse;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jzvd.JZVideoPlayerStandard;

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
        }
    };
}
