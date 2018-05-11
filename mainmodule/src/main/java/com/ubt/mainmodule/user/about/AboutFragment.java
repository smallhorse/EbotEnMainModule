package com.ubt.mainmodule.user.about;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.ubt.baselib.customView.BaseDialog;
import com.ubt.baselib.customView.BaseLoadingDialog;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.BaseResponseModel;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.GsonImpl;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.mainmodule.MainHttpEntity;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.log.ViseLog;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 15:21
 * @描述:
 */

public class AboutFragment extends SupportFragment {

    @BindView(R2.id.tv_about_check)    TextView tvAboutCheck;
    @BindView(R2.id.tv_about_version)   TextView tvAboutVersion;

    Unbinder unbinder;

    private UpdateModel updateModel;

    public static AboutFragment newInstance() {

        Bundle args = new Bundle();

        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        showRedDot(false);
        String version =SkinManager.getInstance().getTextById(R.string.main_about_app_version) + getVersionName();
        tvAboutVersion.setText(version);
        initData();
        checkUpdate(false);
        return view;
    }

    private void initData() {
        UserInfoModel  userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        updateModel = new UpdateModel();
        updateModel.setType("2");
        String token = SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN);
        if(!TextUtils.isEmpty(token)) {
            updateModel.setToken(token);
        }
        if(userInfo != null) {
            updateModel.setUserId(userInfo.getUserId());
        }
        updateModel.setVersion("V"+getVersionName());
//        updateModel.setVersion("V1.1.1.9");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.tv_about_check)
    public void onViewClicked() {
        BaseLoadingDialog.show(getActivity());
        checkUpdate(true);
    }


    /**
     * 获取当前本地apk的版本
     *
     * @return
     */
    public static String getVersionName() {
        String versionCode = "1.0.0.1";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = ContextUtils.getContext().getPackageManager().
                    getPackageInfo(ContextUtils.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public  void launchAppDetail(Context context) {	//appPkg 是应用的包名
        final String GOOGLE_PLAY = "com.android.vending";//这里对应的是谷歌商店，跳转别的商店改成对应的即可
        try {
            ViseLog.d("packageName = "+context.getPackageName());
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(GOOGLE_PLAY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            	//跳转失败的处理
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    private void showUpdateToast(String msg){
        Toast toast = Toast.makeText(ContextUtils.getContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 180, 0);
        toast.show();
    }

    private void showQuitDialog(){
        if(getActivity() != null) {
            new BaseDialog.Builder(getActivity())
                    .setMessage(R.string.main_about_app_check_update_dialogue)
                    .setConfirmButtonId(R.string.main_common_cancel)
                    .setCancleButtonID(R.string.main_common_sure)
                    .setButtonOnClickListener(new BaseDialog.ButtonOnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            if (view.getId() == com.ubt.baselib.R.id.button_cancle) {//确定按钮
                                launchAppDetail(ContextUtils.getContext());
                            }
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }else{
            ViseLog.e("activity is null!!!!");
        }
    }


    /**
     *  查询后台是否有升级
     * @param isNeedToast 是否提示消息
     */
    private void checkUpdate(final boolean isNeedToast){
        ViseHttp.POST(MainHttpEntity.APP_UPGRADE)
                .setJson(GsonImpl.get().toJson(updateModel))
                .request(new ACallback<BaseResponseModel>() {
                    @Override
                    public void onSuccess(BaseResponseModel responseModel) {
                        BaseLoadingDialog.dismiss(getActivity());
                        ViseLog.d("USER_UPDATE onSuccess: models=" + responseModel.models);
                        if(responseModel.models != null){
                            showRedDot(true);
                            if(isNeedToast) {
                                showQuitDialog();
                            }
                        }else{
                            if(isNeedToast) {
                                showUpdateToast(ContextUtils.getContext()
                                        .getString(R.string.main_about_app_check_latest_version_toast));
                            }
                        }

                    }

                    @Override
                    public void onFail(int code, String s) {
                        BaseLoadingDialog.dismiss(getActivity());
                        ViseLog.d("USER_UPDATE onFail:" + s+"  code="+code);
                        if(isNeedToast) {
                            showUpdateToast(ContextUtils.getContext()
                                    .getString(R.string.main_about_app_check_fail_toast));
                        }
                    }
                });

    }

    /**
     * 显示升级小红点
     * @param isShow 是否显示
     */
    private void showRedDot(boolean isShow){
        if(isShow){
            Drawable  img = getResources().getDrawable(R.drawable.main_update_red_dot);
            if (img != null) {
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                tvAboutCheck.setCompoundDrawables(img, null, null, null);
            }
        }else{
            tvAboutCheck.setCompoundDrawables(null, null, null, null);
        }
    }

}
