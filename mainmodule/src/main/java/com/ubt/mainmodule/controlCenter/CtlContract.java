package com.ubt.mainmodule.controlCenter;

import android.os.Handler;

import com.ubt.baselib.mvp.BasePresenter;
import com.ubt.baselib.mvp.BaseView;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:31
 * @描述:
 */

public class CtlContract {

    public static final int HCMD_REFRESH_VOL = 1;   //更新音量状态
    public static final int HCMD_REFRESH_FALL = 2;  //更新跌倒保护状态
    public static final int HCMD_REFRESH_IR = 3;    //更新红外控制状态

    public interface View extends BaseView {
        Handler getViewHandler();
    }

    interface Presenter extends BasePresenter<CtlContract.View> {
        void init();
        void release();
        boolean isBTConnected();
        void setVol(int vol);
        void setFallDown(boolean status);
        void setIRStatus(boolean status);
    }
}
