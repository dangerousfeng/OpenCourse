package com.feng.opencourse.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by Windows 7 on 2018/2/25 0025.
 */

public class UserBase implements Serializable {
    private String userId="";
    private String userName="";
    private int phone = 0;
    private String email="";
    private String password="";
    private String itime="";
    private Boolean superRole;

    public UserBase() {
    }

    public UserBase(String userId, String userName, int phone, String email, String password, String itime, Boolean superRole) {
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.itime = itime;
        this.superRole = superRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getItime() {
        return itime;
    }

    public void setItime(String itime) {
        this.itime = itime;
    }

    public Boolean getSuperRole() {
        return superRole;
    }

    public void setSuperRole(Boolean superRole) {
        this.superRole = superRole;
    }
}

