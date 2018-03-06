package com.feng.opencourse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseDescFragment extends Fragment {

    private View view;
    private TextView tvName;
    private TextView tvTeacher;
    private TextView tvHot;
    private TextView tvTime;
    private TextView tvDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_course_desc,container, false);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvTeacher = (TextView) view.findViewById(R.id.tv_teacher);
        tvHot = (TextView) view.findViewById(R.id.tv_hot);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvDesc = (TextView) view.findViewById(R.id.tv_desc);

        if (isAdded()) {//判断Fragment已经依附Activity
            String courseDescJsonStr = getArguments().getString("courseDescJsonStr");
            try {
                JSONObject json = new JSONObject(courseDescJsonStr);
                tvName.setText(json.optString("courseName"));
                tvTeacher.setText(json.optString("teacherName"));
                tvHot.setText(json.optInt("hot"));
                tvTime.setText(json.optString("createTime"));
                tvDesc.setText(json.optString("courseDesc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }
}
