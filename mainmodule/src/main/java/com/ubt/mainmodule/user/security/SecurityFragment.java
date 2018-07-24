package com.ubt.mainmodule.user.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.customView.BaseDialog;
import com.ubt.baselib.globalConst.BaseHttpEntity;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.BaseResponseModel;
import com.ubt.baselib.model1E.ManualEvent;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.utils.AppStatusUtils;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.GsonImpl;
import com.ubt.baselib.utils.MD5Util;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.baselib.utils.ToastUtils;
import com.ubt.bluetoothlib.blueClient.BlueClientUtil;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.ubt.mainmodule.user.privacy.DeleteUserInfoRequest;
import com.vise.log.ViseLog;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SecurityFragment extends SupportFragment {

    @BindView(R2.id.iv_logout_icon)    ImageView ivLogoutIcon;
    @BindView(R2.id.tv_logout_name)    TextView tvLogoutName;
    @BindView(R2.id.tv_logout_id)    TextView tvLogoutId;
    @BindView(R2.id.tv_request_cancel)    TextView tvRequestCancel;
    @BindView(R2.id.wv_request_content)
    WebView mWebView;

    String URL = "http://10.10.1.14:8080/alpha1e/gdpr/warning.html?systemLanguage=CN ";
    Unbinder unbinder;
    private UserInfoModel userInfo;

    public static SecurityFragment newInstance() {

        Bundle args = new Bundle();

        SecurityFragment fragment = new SecurityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_security, container, false);
        unbinder = ButterKnife.bind(this, view);
//        initData();
        initWebView();
        return view;
    }

    private void initWebView(){
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        //开发稳定后需去掉该行代码
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setUseWideViewPort(true);  //将图片调整到适合webview的大小
        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

        };

        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(URL);
    }

    private void initData() {
        userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        if(userInfo != null){
            ViseLog.i("userInfo="+userInfo.toString());
            tvLogoutName.setText(userInfo.getNickName());
            if(!userInfo.getEmail().equals("null") && !TextUtils.isEmpty(userInfo.getEmail())){
                tvLogoutId.setText(userInfo.getEmail());
            }else{
                tvLogoutId.setText("");
            }

            if(!TextUtils.isEmpty(userInfo.getHeadPic())){
                Glide.with(this).load(userInfo.getHeadPic()).centerCrop().into(ivLogoutIcon);
            }
        }else{
            ViseLog.e("userInfo is null");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            initData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private int ck = 0;
    @OnClick(R2.id.tv_request_cancel)
    public void onViewClicked() {
        new BaseDialog.Builder(getActivity())
                .setMessage(R.string.security_center_dialogue)
                .setConfirmButtonId(R.string.main_common_cancel)
                .setConfirmButtonColor(R.color.base_blue)
                .setCancleButtonID(R.string.common_btn_submit)
                .setCancleButtonColor(R.color.main_red)
                .setOnDissmissListener(new BaseDialog.OnDissmissListener() {
                    @Override
                    public void onDissmiss() {
                        if(ck == 1){
//                            showConfirmDialog();
                            showDilogCheck(getActivity());
                            ck = 0;
                        }
                    }
                })
                .setButtonOnClickListener(new BaseDialog.ButtonOnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == com.ubt.baselib.R.id.button_confirm) {
                            ck = 0;
                            dialog.dismiss();
                        } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                            ck = 1;
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }


    DialogPlus mDialogPlus = null;
    EditText edtPsw;
    TextView tvTip;
    TextView tvUserTip;
    private void showDilogCheck(Context context) {
        if (mDialogPlus != null) {
            mDialogPlus.dismiss();
            mDialogPlus = null;
        }

        View contentView = LayoutInflater.from(context).inflate(R.layout.base_dialog_gdpr_tip_layout, null);

        edtPsw = contentView.findViewById(R.id.edt_password);
        tvTip = contentView.findViewById(R.id.tv_err_psw);
        ViseLog.d("tvTip:" + tvTip);
        tvUserTip = contentView.findViewById(R.id.tv_user_tip);
        String email = SPUtils.getInstance().getString(Constant1E.SP_USER_EMAIL);
        int msgId = R.string.security_center_password_2;
        int tipId = R.string.policy_disagree_password_error;
        if(!email.equals("null") && !TextUtils.isEmpty(email)){
            msgId  = R.string.security_center_password_2;
            tipId = R.string.policy_disagree_password_error;
        }else{
            msgId = R.string.policy_disagree_123456;
            tipId = R.string.policy_disagree_123456_error;
        }
        tvTip.setText(tipId);
        tvUserTip.setText(msgId);
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.8); //设置宽度
        int height = (int)((display.getHeight()) * 0.9);
        mDialogPlus = DialogPlus.newDialog(context)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setContentHeight(height)
                .setContentBackgroundResource(android.R.color.transparent)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        int id = view.getId();
                        if(id == R.id.button_confirm){
                            dialog.dismiss();
                        }else if(id == R.id.button_cancle){
                            if(TextUtils.isEmpty(edtPsw.getText().toString())){
                                ToastUtils.showShort("please input password");
                                return;
                            }

                            String email = SPUtils.getInstance().getString(Constant1E.SP_USER_EMAIL);
                            if(email.equals("null") && TextUtils.isEmpty(email)){
                                if(!edtPsw.getText().equals("123456")){
                                    tvTip.setVisibility(View.VISIBLE);
                                }

                                return;
                            }

                            doCheck(edtPsw.getText().toString(), dialog);
                        }

                    }
                })
                .setCancelable(false)
                .create();
        mDialogPlus.show();


    }


    private void doCheck(String password, final DialogPlus dialogPlus) {

        String token = /*"7bd35c006d994aba95e7c1e5b2965dcc806742";*/SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN);
        String email = SPUtils.getInstance().getString(Constant1E.SP_USER_EMAIL);
        int isThrid = 0;
        String psw = "";
        if (TextUtils.isEmpty(email) || email.equals("null")) {
            isThrid = 1;
            psw = password;
        } else {
            isThrid = 0;
            psw = MD5Util.encodeByMD5(password);

        }

        ViseLog.d("token:" + token + "psw:" + psw);
        String url = "user-service-rest/v2/user?isThird=" + isThrid + "&pwd=" + psw;


        ViseHttp.DELETE_EXTR(url).baseUrl(BaseHttpEntity.BASE_UBX_COMMON)
                .addHeader("authorization", token)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        ViseLog.d("onSuccess :" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.optInt("code");
                            String message = jsonObject.optString("message");
                            if (code == 0) {
                                doDeleteUserInfo();
                            } else {
                                ToastUtils.showShort("Account cancellation failed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int i, String s) {
                        ViseLog.d("onFail :" + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.optInt("code");
                            String message = jsonObject.optString("message");
                            tvTip.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void doDeleteUserInfo() {
        DeleteUserInfoRequest deleteUserInfoRequest = new DeleteUserInfoRequest();
        deleteUserInfoRequest.setToken(SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN));
        deleteUserInfoRequest.setUserId(SPUtils.getInstance().getInt(Constant1E.SP_USER_ID));

        ViseLog.d("doDeleteUserInfo");

        ViseHttp.POST("alpha1e/overseas/user/delete").baseUrl(BaseHttpEntity.BASIC_UBX_SYS)
                .setJson(GsonImpl.get().toJson(deleteUserInfoRequest))
                .request(new ACallback<String>() {

                    @Override
                    public void onSuccess(String data) {
                        ViseLog.d("USER_DELETE_INFO onSuccess:" + data);

                        BaseResponseModel<UserInfoModel> baseResponseModel = GsonImpl.get().toObject(data,
                                new TypeToken<BaseResponseModel<UserInfoModel>>() {
                                }.getType());
                        if(baseResponseModel.status){
                            ToastUtils.showShort("Account cancellation success");
                            clearUser();

                        }else{
                            ToastUtils.showShort("Account cancellation failed");
                        }

                    }

                    @Override
                    public void onFail(int i, String s) {
                        ViseLog.d("USER_DELETE_INFO onFail:"+ i + "s:" +  s);
                        ToastUtils.showShort("Account cancellation failed");

                    }
                });
    }


    private void clearUser() {
        //清除用户参数
        ARouter.getInstance().build(ModuleUtils.Login_Module).navigation();
        SPUtils.getInstance().remove(Constant1E.SP_USER_INFO);
        //断开自动连接蓝牙服务
        AppStatusUtils.setIsForceDisBT(true);
        BlueClientUtil.getInstance().disconnect();
        ManualEvent manualEvent = new ManualEvent(ContextUtils.getContext(),
                ManualEvent.Event.START_AUTOSERVICE);
        manualEvent.setManual(false);
        EventBus.getDefault().post(manualEvent);

        if(getActivity() != null) {
            ViseLog.d("MainActivity退出!!");
            getActivity().finish();
        }
    }



}
