package com.feng.opencourse;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feng.opencourse.adapter.MyFragmentPagerAdapter;
import com.feng.opencourse.util.MyApplication;
import com.squareup.picasso.Picasso;

import cn.jzvd.JZVideoPlayerStandard;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Intent intent = new Intent();
        courseId = intent.getStringExtra("courseId");
        courseFacePath = intent.getStringExtra("courseFacePath");
        MyApplication myapp = (MyApplication) getApplication();

        initData();
        
        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.vp_face);
//        jzVideoPlayerStandard.setUp(sectionOnePath
//                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, sectionOneName);
        Picasso.with(CourseDetailActivity.this).load(courseFacePath).into(jzVideoPlayerStandard.thumbImageView);

        attrViewPager = (ViewPager) findViewById(R.id.vp_attr);
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

    private void initData() {
        //// TODO: 2018/3/5 0005 根据courseId获取课程详情所有数据
    }
}
