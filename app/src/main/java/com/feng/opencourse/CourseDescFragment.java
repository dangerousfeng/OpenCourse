package com.feng.opencourse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        return view;
    }
}
