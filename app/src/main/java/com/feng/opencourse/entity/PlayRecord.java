package com.feng.opencourse.entity;

/**
 * Created by Administrator on 2018/3/14.
 */

public class PlayRecord {
    private String courseId;
    private String courseName;
    private String playTime;

    public PlayRecord() {
    }

    public PlayRecord(String courseId, String courseName, String playTime) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.playTime = playTime;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }
}
