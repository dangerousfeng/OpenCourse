package com.feng.opencourse.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.feng.opencourse.CourseCommentFragment;
import com.feng.opencourse.CourseDescFragment;
import com.feng.opencourse.CourseSectionFragment;

/**
 * Created by Windows 7 on 2018/3/4 0004.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"Desc", "Section","Comment"};

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //此处根据不同的position返回不同的Fragment
        if (position == 1){
            return new CourseSectionFragment();
        }
        if (position == 2){
            return new CourseCommentFragment();
        }
        return new CourseDescFragment(); // 0
    }

    @Override
    public int getCount() {
        //此处返回Tab的数目
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //此处返回每个Tab的title
        return mTitles[position];
    }
}

