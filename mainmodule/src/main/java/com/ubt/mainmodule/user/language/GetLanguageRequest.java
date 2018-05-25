package com.ubt.mainmodule.user.language;

/**
 * @author：liuhai
 * @date：2018/5/23 14:55
 * @modifier：ubt
 * @modify_date：2018/5/23 14:55
 * [A brief description]
 * version
 */

public class GetLanguageRequest {
    private String userId;
    private String token;
    private String type;
    private String version;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    @Override
    public String toString() {
        return "GetLanguageRequest{" +
                "userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
