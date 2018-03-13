package com.feng.opencourse.entity;

/**
 * Created by Administrator on 2018/3/13.
 */

public class Comment {
    private int commentId;
    private String courseId;
    private String userId;
    private String content;
    private String commentTime;
    private int zanNum;
    private String uName;

    public Comment() {
    }

    public Comment(int commentId, String courseId, String userId, String content, String commentTime, int zanNum, String uName) {
        this.commentId = commentId;
        this.courseId = courseId;
        this.userId = userId;
        this.content = content;
        this.commentTime = commentTime;
        this.zanNum = zanNum;
        this.uName = uName;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public int getZanNum() {
        return zanNum;
    }

    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
