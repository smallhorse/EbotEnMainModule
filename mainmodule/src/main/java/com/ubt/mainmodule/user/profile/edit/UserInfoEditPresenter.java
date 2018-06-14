package com.ubt.mainmodule.user.profile.edit;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ubt.baselib.globalConst.BaseHttpEntity;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/20 10:49
 * @描述:
 */

public class UserInfoEditPresenter extends BasePresenterImpl<UserInfoEditContract.View> implements UserInfoEditContract.Presenter{

    private UserModel userModel = new UserModel(); //传过来的用户参数
    private UserInfoModel userInfo;
    private Handler viewHandler;
    private String qiniuToken;
    private String qiniuFileUrl;
    private String qiniuPath;
    private UploadManager uploadManager;
    private String token = SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN);

    @Override
    public void init() {
        initData();
        viewHandler = mView.getViewHandler();
        Configuration config = new Configuration.Builder()
                .zone(AutoZone.autoZone)
                .build();
        uploadManager = new UploadManager(config);
    }

    @Override
    public void release() {

    }

    /**
     * 获取上传图片所需要的七牛 TOKEN
     * @param iconPath
     */
    private void getQiniuToken(final String iconPath){
        ViseHttp.GET(MainHttpEntity.GET_UPLOAD_TOKEN)
                .addHeader("authorization",token)
                .baseUrl(BaseHttpEntity.BASE_UBX_COMMON)
                .addParam("pName", "alphaebot")
                .request(new ACallback<String>() {

                    @Override
                    public void onSuccess(String msg) {
                        ViseLog.i("msg="+msg);
                        if(saveQiniuInfo(msg)){
                            uploadIconToQiniu(iconPath);
                        }else{
                            sendCompletMsg(false);
                        }
                    }

                    @Override
                    public void onFail(int code, String errmsg) {
                        ViseLog.e("code="+code+"   errmsg="+errmsg);
                        sendCompletMsg(false);
                    }
                });
    }

    private boolean saveQiniuInfo(String info){
        try {
            JSONObject jInfo = new JSONObject(info);
            if(jInfo.has("uri")){
                qiniuFileUrl = jInfo.getString("uri");
            }
            if(jInfo.has("token")){
                qiniuToken = jInfo.getString("token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (qiniuFileUrl != null  && qiniuToken != null);
    }

    /**
     * 上传头像到七牛
     * @param iconPath 图片完整路径
     */
    private void uploadIconToQiniu(String iconPath){

        uploadManager.put(iconPath, null, qiniuToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        ViseLog.i("res:"+res.toString());
                        ViseLog.i("info:"+info.toString());
                        if(info.isOK()){
                            try {
                                if(res.has("key")) {
                                    qiniuPath = qiniuFileUrl+"/"+res.getString("key");
                                    userInfo.setHeadPic(qiniuPath);
                                    SPUtils.getInstance().saveObject(Constant1E.SP_USER_INFO, userInfo);
                                    updateUserInfo();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            sendCompletMsg(false);
                        }

                    }
                }, null);
    }

    @Override
    public void saveUserInfo(final String iconPath) {
        if(userInfo != null) {
            if(iconPath != null){
                getQiniuToken(iconPath);
            }else {
                updateUserInfo();
            }
        }else{
            ViseLog.e("userInfo is null !!!");
            sendCompletMsg(false);
        }
    }

    /**
     *  发送保存完成命令到VIEW
     * @param isOk
     */
    private void sendCompletMsg(boolean isOk){
        Message msg = viewHandler.obtainMessage(UserInfoEditContract.HCMD_UPDATE_COMPLET);
        msg.arg1 = isOk?1:0;
        viewHandler.sendMessage(msg);
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
            if(TextUtils.isEmpty(userInfo.getCountry())) {
                userModel.setCountry(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled));
            }else{
                try {
                    userModel.setCountry(userInfo.getCountry());
                }catch (Exception e){

                }
            }
            if(!TextUtils.isEmpty(userInfo.getEmail())) {
                userModel.setId(userInfo.getEmail());
            }else{
                userModel.setId("");
            }
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

    /**
     *  上传用户信息到后台
     */
    private void updateUserInfo() {
        userInfo.setBirthDate(userModel.getBirthday());
//        userInfo.setHeadPic(userModel.getIcon());
        userInfo.setSex(String.valueOf(userModel.getGenderId() + 1));
        userInfo.setCountry(userModel.getCountry());
        ViseLog.d("url:" + MainHttpEntity.UPDATE_USERINFO + "  params:" + userInfo.toString());
        JSONObject juser = null;
        try {
            juser = new JSONObject(GsonImpl.get().toJson(userInfo));
            juser.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(juser == null){
            ViseLog.e(("juser is null!!"));
            return;
        }
        ViseLog.i("juser = "+juser.toString());
        ViseHttp.POST(MainHttpEntity.UPDATE_USERINFO)
                .setJson(juser.toString())
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String msg) {
                        ViseLog.d("USER_UPDATE onSuccess:" + msg.toString());
                        try {
                            JSONObject jMsg = new JSONObject(msg);
                            if(jMsg.has("status")){
                                if(jMsg.getBoolean("status")){
                                    sendCompletMsg(true);
                                    SPUtils.getInstance().saveObject(Constant1E.SP_USER_INFO, userInfo);
                                }else{
                                    sendCompletMsg(false);
                                    userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO); //上传失败重新初始化用户信息
                                }
                            }
                        } catch (JSONException e) {
                            userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
                            sendCompletMsg(false);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFail(int code, String s) {
                        userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
                        ViseLog.d("USER_UPDATE onFail:" + s+"  code="+code);
                        sendCompletMsg(false);
                    }
                });


    }

    @Override
    public boolean isUserInfoModified(){
        if(userInfo == null){
            return false;
        }
        if(userModel.getGenderId() != (Integer.valueOf(userInfo.getSex()) - 1)){
            return true;
        }else if(!userModel.getBirthday().equals(userInfo.getBirthDate())){
            return true;
        }else if(userModel.getIcon()!= null  &&!userModel.getIcon().equals(userInfo.getHeadPic())){
            return true;
        }
        return false;
    }
}
