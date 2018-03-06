package com.feng.opencourse.entity;

/**
 * Created by Windows 7 on 2018/3/7 0007.
 */

public class Course {
    private String courseId;
    private String courseName;

    public Course() {
    }

    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
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
}
