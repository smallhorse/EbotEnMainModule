package com.ubt.mainmodule;

import com.ubt.baselib.globalConst.BaseHttpEntity;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/18 13:17
 * @描述:
 */

public class MainHttpEntity extends BaseHttpEntity{
    public static final String THRID_LOGIN_URL = BASIC_THIRD_LOGIN_URL + "user/login/third";
    public static final String GET_USER_INFO = BASIC_UBX_SYS + "user/get";
    public static final String REQUEST_SMS_CODE = BASIC_UBX_SYS + "user/register";
    public static final String BIND_ACCOUNT = BASIC_UBX_SYS + "user/bind";
    public static final String UPDATE_USERINFO = BASIC_UBX_SYS + "user/update";
}

