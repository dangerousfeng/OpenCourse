package com.feng.opencourse.entity;

import java.io.Serializable;

/**
 * Created by Windows 7 on 2018/2/25 0025.
 */

public class UserData implements Serializable {
    private String userId ="";
    private String nickname ="";
    private int money ;

    public UserData() {
    }

    public UserData(String userId, String nickname, int money) {
        this.userId = userId;
        this.nickname = nickname;
        this.money = money;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
