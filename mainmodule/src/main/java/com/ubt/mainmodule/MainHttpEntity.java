package com.ubt.mainmodule;

import com.ubt.baselib.globalConst.BaseHttpEntity;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/18 13:17
 * @描述:
 */

public class MainHttpEntity extends BaseHttpEntity{
    //http
    public static final String UPDATE_USERINFO = "alpha1e/overseas/user/update";//更新用户信息
    public static final String GET_UPLOAD_TOKEN = "file-service-rest/v2/files/up-token";  //获取七牛TOKEN
    public static final String APP_UPGRADE = "alpha1e/sys/appUpgrade";  //查询后台升级

    //webView
    public static final String HELP_FEEDBACK = BASIC_UBX_SYS+"alpha1e/overseas/setHelp.html";  //帮助反馈页面
    public static final String VOICE_CMD = BASIC_UBX_SYS+"alpha1e/overseas/voice.html";  //语音指令
}

