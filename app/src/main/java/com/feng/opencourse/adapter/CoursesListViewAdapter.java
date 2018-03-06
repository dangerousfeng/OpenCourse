package com.feng.opencourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.opencourse.R;
import com.feng.opencourse.entity.Course;

import java.util.List;

/**
 * Created by Windows 7 on 2018/3/7 0007.
 */

public class CoursesListViewAdapter extends BaseAdapter {

    private List<Course> courseList;
    private LayoutInflater layoutInflater;
    private Context context;
    public CoursesListViewAdapter(Context context,List<Course> courseList){
        this.context=context;
        this.courseList = courseList;
        this.layoutInflater=LayoutInflater.from(context);
    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class CourseItem {
        public TextView title;
        public ImageView face;
    }
    @Override
    public int getCount() {
        return courseList.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CourseItem courseItem =null;
        if(convertView==null){
            courseItem =new CourseItem();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.listview_item_course,null);

            courseItem.title=(TextView)convertView.findViewById(R.id.tv_item_course_name);
            courseItem.face = (ImageView) convertView.findViewById(R.id.iv_item_course_face);
            convertView.setTag(courseItem);
        }else{
            courseItem =(CourseItem)convertView.getTag();
        }
        //绑定数据
        courseItem.title.setText(courseList.get(position).getCourseName());
        String courseId = courseList.get(position).getCourseId();
        //courseItem.face.

        return convertView;
    }
}
