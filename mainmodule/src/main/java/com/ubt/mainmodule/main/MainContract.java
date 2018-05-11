package com.ubt.mainmodule.main;

import android.os.Handler;

import com.ubt.baselib.mvp.BasePresenter;
import com.ubt.baselib.mvp.BaseView;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/17 14:44
 * @描述:
 */

public class MainContract {
    /**
     * handler交互命令定义
     */
    public static final int HCMD_REFRESH_BATTERY = 1;  //刷新电量显示
    public static final int HCMD_LOST_BT = 2;           //蓝牙断开
    public static final int HCMD_BT_CONNETED = 3;           //蓝牙连接成功
    public static final int HCMD_ROBOT_WIFI_CONNETED = 4;           //机器人WIFI连接成功
    public static final int HCMD_ROBOT_WIFI_DISCONNETED = 5;           //机器人WIFI断开


    public interface View extends BaseView {
        Handler getViewHandler();
    }

    interface Presenter extends BasePresenter<View> {
        void init();
        void release();
        void queryBattery(boolean isStart); //开启或关闭查询电量
        boolean isRobotConnected();
    }

}
