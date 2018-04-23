package com.ubt.mainmodule.user;

import android.os.Handler;

import com.ubt.baselib.mvp.BasePresenter;
import com.ubt.baselib.mvp.BaseView;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:31
 * @描述:
 */

public class UserMainContract {

    public interface View extends BaseView {
        Handler getViewHandler();
    }

    interface Presenter extends BasePresenter<UserMainContract.View> {
        void init();
        void release();
    }
}
