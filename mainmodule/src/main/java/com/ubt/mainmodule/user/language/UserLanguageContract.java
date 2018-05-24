package com.ubt.mainmodule.user.language;

import com.ubt.baselib.mvp.BasePresenter;
import com.ubt.baselib.mvp.BaseView;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:31
 * @描述:
 */

public class UserLanguageContract {

    public interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<UserLanguageContract.View> {
        void init();

        void release();
    }
}
