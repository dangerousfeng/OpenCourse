package com.feng.opencourse.entity;

/**
 * Created by Administrator on 2018/3/6.
 */

public class Section {
    private String courseId;
    private int sectionId;
    private String secName;
    private String sectionDesc;

    public Section() {
    }

    public Section(String courseId, int sectionId, String secName, String sectionDesc) {
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.secName = secName;
        this.sectionDesc = sectionDesc;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getSectionDesc() {
        return sectionDesc;
    }

    public void setSectionDesc(String sectionDesc) {
        this.sectionDesc = sectionDesc;
    }
}
