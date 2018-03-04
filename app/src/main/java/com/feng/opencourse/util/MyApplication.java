package com.feng.opencourse.util;

/**
 * Created by Windows 7 on 2018/2/23 0023.
 */

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;

/**
 * 编写自己的Application，管理全局状态信息，比如Context
 * @author yy
 *
 */
public class MyApplication extends Application {
    private static Context context;
    private String JWT;
    private String userId;
    private OSSFederationToken readOnlyOSSFederationToken;
    private OSSFederationToken writeOnlyOSSFederationToken;

    @Override
    public void onCreate() {
        //获取Context
        context = getApplicationContext();
    }

    public OSSFederationToken getReadOnlyOSSFederationToken() {
        return readOnlyOSSFederationToken;
    }
    public OSSCredentialProvider getReadOnlyOSSCredentialProvider() {

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                readOnlyOSSFederationToken.getTempAK(),
                readOnlyOSSFederationToken.getTempSK(),
                readOnlyOSSFederationToken.getSecurityToken());
        return credentialProvider;
    }

    public void setReadOnlyOSSFederationToken(OSSFederationToken readOnlyOSSFederationToken) {
        this.readOnlyOSSFederationToken = readOnlyOSSFederationToken;
    }

    public OSSFederationToken getWriteOnlyOSSFederationToken() {
        return writeOnlyOSSFederationToken;
    }
    public OSSCredentialProvider getWriteOnlyOSSCredentialProvider() {

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                writeOnlyOSSFederationToken.getTempAK(),
                writeOnlyOSSFederationToken.getTempSK(),
                writeOnlyOSSFederationToken.getSecurityToken());
        return credentialProvider;
    }

    public void setWriteOnlyOSSFederationToken(OSSFederationToken writeOnlyOSSFederationToken) {
        this.writeOnlyOSSFederationToken = writeOnlyOSSFederationToken;
    }

    //返回
    public static Context getContextObject(){

        return context;

    }
    public String getJWT() {
        return JWT;
    }

    public void setJWT(String JWT) {
        this.JWT = JWT;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}

//MyApplication.getContextObject();