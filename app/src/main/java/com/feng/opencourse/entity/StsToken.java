package com.feng.opencourse.entity;

/**
 * Created by Windows 7 on 2018/2/28 0028.
 */

public class StsToken {
    private String Expiration;
    private String AccessKeyId;
    private String AccessKeySecret;
    private String SecurityToken;

    public StsToken() {
    }

    public StsToken(String expiration, String accessKeyId, String accessKeySecret, String securityToken) {
        Expiration = expiration;
        AccessKeyId = accessKeyId;
        AccessKeySecret = accessKeySecret;
        SecurityToken = securityToken;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String expiration) {
        Expiration = expiration;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }
}
