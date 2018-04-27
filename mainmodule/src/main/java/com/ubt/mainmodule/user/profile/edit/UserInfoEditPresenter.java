package com.ubt.mainmodule.user.profile.edit;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.mvp.BasePresenterImpl;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.GsonImpl;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.mainmodule.MainHttpEntity;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.user.profile.UserModel;
import com.vise.log.ViseLog;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.request.PostRequest;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/20 10:49
 * @描述:
 */

public class UserInfoEditPresenter extends BasePresenterImpl<UserInfoEditContract.View> implements UserInfoEditContract.Presenter{

    private UserModel userModel = new UserModel(); //传过来的用户参数
    private UserInfoModel userInfo;
    private Handler viewHandler;

    @Override
    public void init() {
        initData();
        viewHandler = mView.getViewHandler();
    }

    @Override
    public void release() {

    }

    @Override
    public void saveUserInfo() {
        if(userInfo != null) {
            userInfo.setBirthDate(userModel.getBirthday());
            userInfo.setHeadPic(userModel.getIcon());
            userInfo.setSex(String.valueOf(userModel.getGenderId() + 1));
            SPUtils.getInstance().saveObject(Constant1E.SP_USER_INFO, userInfo);
            updateUserInfo();
        }else{
            ViseLog.e("userInfo is null !!!");
            Message msg = viewHandler.obtainMessage(UserInfoEditContract.HCMD_UPDATE_COMPLET);
            msg.arg1 = 0;
            viewHandler.sendMessage(msg);
        }
    }

    @Override
    public UserModel getUserModel() {
        return userModel;
    }

    private void initData() {
        userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        if(userInfo != null) {
            userModel.setGenderId(Integer.valueOf(userInfo.getSex()) - 1);
            if(!TextUtils.isEmpty(userInfo.getBirthDate())) {
                userModel.setBirthday(userInfo.getBirthDate());
            }else{
                userModel.setBirthday(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
            }
            userModel.setCountry(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
            userModel.setId(userInfo.getEmail());
            userModel.setName(userInfo.getNickName());
            userModel.setIcon(userInfo.getHeadPic());
        }else{
            ViseLog.e("userInfo is null!!!!");
            userModel.setName(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
            userModel.setId(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
            userModel.setBirthday(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
            userModel.setGenderId(-1);
            userModel.setCountry(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
        }
    }

    public void updateUserInfo() {
        ViseLog.d("url:" + MainHttpEntity.UPDATE_USERINFO + "params:" + userInfo.toString());
        ViseHttp.BASE(new PostRequest(MainHttpEntity.UPDATE_USERINFO)
                .setJson(GsonImpl.get().toJson(userInfo)))
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String o) {
                        ViseLog.d("USER_UPDATE onSuccess:" + o.toString());
                        Message msg = viewHandler.obtainMessage(UserInfoEditContract.HCMD_UPDATE_COMPLET);
                        msg.arg1 = 1;
                        viewHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFail(int i, String s) {
                        ViseLog.d("USER_UPDATE onFail:" + s);
                        Message msg = viewHandler.obtainMessage(UserInfoEditContract.HCMD_UPDATE_COMPLET);
                        msg.arg1 = 0;
                        viewHandler.sendMessage(msg);
                    }
                });


    }

}
