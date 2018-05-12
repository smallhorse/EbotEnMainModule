package com.ubt.mainmodule;

import com.ubt.baselib.globalConst.BaseHttpEntity;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/18 13:17
 * @描述:
 */

public class MainHttpEntity extends BaseHttpEntity{
    //http
    public static final String UPDATE_USERINFO = "overseas/user/update";//更新用户信息
    public static final String GET_UPLOAD_TOKEN = "files/up-token";  //获取七牛TOKEN
    public static final String APP_UPGRADE = "sys/appUpgrade";  //查询后台升级

    //webView
    public static final String HELP_FEEDBACK = BASIC_UBX_SYS+"overseas/setHelp.html";  //帮助反馈页面
}

