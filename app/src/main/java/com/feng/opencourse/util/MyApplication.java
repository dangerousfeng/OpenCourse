package com.feng.opencourse.util;

/**
 * Created by Windows 7 on 2018/2/23 0023.
 */

import android.app.Application;
import android.content.Context;

/**
 * 编写自己的Application，管理全局状态信息，比如Context
 * @author yy
 *
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        //获取Context
        context = getApplicationContext();
    }

    //返回
    public static Context getContextObject(){
        return context;
    }
}

//MyApplication.getContextObject();