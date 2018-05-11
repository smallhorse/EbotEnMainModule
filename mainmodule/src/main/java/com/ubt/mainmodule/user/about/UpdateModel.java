package com.ubt.mainmodule.user.about;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/5/11 14:44
 * @描述:
 */

public class UpdateModel {
    private String token;
    private String userId;
    private String type;
    private String version;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
