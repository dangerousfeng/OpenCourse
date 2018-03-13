package com.feng.opencourse.adapter;

import android.os.Bundle;
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
    private String[] mTitles = new String[]{"Desc", "Section", "Comment"};
    private String courseDescJsonStr;
    private String sectionsJsonStr;
    private String commentsJsonStr;

    public MyFragmentPagerAdapter(FragmentManager fm, String courseDescJsonStr,
                                  String sectionsJsonStr, String commentsJsonStr) {
        super(fm);
        this.courseDescJsonStr = courseDescJsonStr;
        this.sectionsJsonStr = sectionsJsonStr;
        this.commentsJsonStr = commentsJsonStr;
    }

    @Override
    public Fragment getItem(int position) {
        //此处根据不同的position返回不同的Fragment
        if (position == 1) {
            Fragment sectionFragment = new CourseSectionFragment();
            Bundle sectionBundle = new Bundle();
            sectionBundle.putString("sectionsJsonStr",sectionsJsonStr);
            sectionFragment.setArguments(sectionBundle);
            return sectionFragment;
        }
        if (position == 2) {
            Fragment commentFragment = new CourseCommentFragment();
            Bundle commentBundle = new Bundle();
            commentBundle.putString("commentsJsonStr",commentsJsonStr);
            commentFragment.setArguments(commentBundle);
            return commentFragment;
        }
        Fragment courseDescFragment = new CourseDescFragment(); // 0
        Bundle courseDescBundle = new Bundle();
        courseDescBundle.putString("courseDescJsonStr",courseDescJsonStr);
        courseDescFragment.setArguments(courseDescBundle);
        return courseDescFragment;
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

