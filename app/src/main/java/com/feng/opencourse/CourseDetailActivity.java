package com.feng.opencourse;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feng.opencourse.adapter.MyFragmentPagerAdapter;

import cn.jzvd.JZVideoPlayerStandard;

public class CourseDetailActivity extends AppCompatActivity {

    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private TabLayout mTabLayout;
    private ViewPager attrViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;

    private TabLayout.Tab mTab1;
    private TabLayout.Tab mTab2;
    private TabLayout.Tab mTab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.vp_face);
        attrViewPager = (ViewPager) findViewById(R.id.vp_attr);
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        //给ViewPager设置适配器
        attrViewPager.setAdapter(mFragmentPagerAdapter);
        //tablayout与Viewpager绑定
        mTabLayout = (TabLayout) findViewById(R.id.tl_attr);
        mTabLayout.setupWithViewPager(attrViewPager);

        mTab1 = mTabLayout.getTabAt(0);
        mTab2 = mTabLayout.getTabAt(1);
        mTab2 = mTabLayout.getTabAt(2);
    }
}
