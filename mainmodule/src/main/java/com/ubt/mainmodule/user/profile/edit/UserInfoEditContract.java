package com.ubt.mainmodule.user.profile.edit;

import android.os.Handler;

import com.ubt.baselib.mvp.BasePresenter;
import com.ubt.baselib.mvp.BaseView;
import com.ubt.mainmodule.user.profile.UserModel;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/20 10:49
 * @描述:
 */

public class UserInfoEditContract {
    public static final int HCMD_UPDATE_COMPLET = 1;

    public interface View extends BaseView {
        Handler getViewHandler();
    }

    interface Presenter extends BasePresenter<View> {
        void init();
        void release();
        void saveUserInfo(final String iconPath);
        UserModel getUserModel();
        boolean isUserInfoModified();
    }
}
