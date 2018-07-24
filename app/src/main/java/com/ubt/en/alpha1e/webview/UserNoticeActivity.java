package com.ubt.en.alpha1e.webview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.customView.BaseGdprTipDialog;
import com.ubt.baselib.globalConst.BaseHttpEntity;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.BaseResponseModel;
import com.ubt.baselib.model1E.ManualEvent;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.utils.AppStatusUtils;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.GsonImpl;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.baselib.utils.ToastUtils;
import com.ubt.baselib.utils.http1E.OkHttpClientUtils;
import com.ubt.bluetoothlib.blueClient.BlueClientUtil;
import com.ubt.en.alpha1e.R;
import com.ubt.en.alpha1e.module.GdprRequestListModule;
import com.ubt.en.alpha1e.module.GdprRequestModule;
import com.ubt.mainmodule.user.privacy.DeleteUserInfoRequest;
import com.vise.log.ViseLog;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class UserNoticeActivity extends AppCompatActivity {

    private String URL = "http://10.10.1.14:8080/alpha1e/gdpr/cancelCount.html?systemLanguage=CN";
    private WebView mWebView;
    private TextView tvDisagree;
    private Button btnAgree;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notice);
        initView();
        initWebView();
    }

    private void initView(){
        mWebView = (WebView) findViewById(R.id.wb_user_notice);
        tvDisagree = (TextView) findViewById(R.id.tv_disagree);
        btnAgree = (Button) findViewById(R.id.btn_agree);

        tvDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDilogLogOut();
            }
        });

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSaveUserService();
            }
        });
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

    private void showDilogLogOut() {
        ViseLog.e("showConfirmDialog");
        new BaseGdprTipDialog.Builder(UserNoticeActivity.this)
                .setMessage(com.ubt.mainmodule.R.string.main_privacy_msg)
                .setConfirmButtonId(com.ubt.mainmodule.R.string.main_common_cancel)
                .setConfirmButtonColor(com.ubt.mainmodule.R.color.main_black)
                .setCancleButtonID(com.ubt.mainmodule.R.string.main_submit)
                .setCancleButtonColor(com.ubt.mainmodule.R.color.main_red)
                .setTitle(com.ubt.mainmodule.R.string.main_important_notice)
                .setMsgTipId(com.ubt.mainmodule.R.string.main_input_tip)
                .setButtonOnClickListener(new BaseGdprTipDialog.ButtonOnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view, String s) {
                        if (view.getId() == com.ubt.baselib.R.id.button_confirm) {
                            dialog.dismiss();
                        } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                            if(TextUtils.isEmpty(s)){
                                ToastUtils.showShort("please input password");
                                return;
                            }

                            dialog.dismiss();
                            doCheck(s);

                        }
                    }
                })
                .create()
                .show();
    }


    private void doCheck(String password) {

        String token = /*"7bd35c006d994aba95e7c1e5b2965dcc806742";*/SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN);
        String email = SPUtils.getInstance().getString(Constant1E.SP_USER_EMAIL);
        int isThrid = 0;
        if(TextUtils.isEmpty(email) || email.equals("null")) {
            isThrid = 0;
        }else{
            isThrid = 1;
        }

        String url = BaseHttpEntity.BASE_UBX_COMMON + "user-service-rest/v2/user?isThird=" + isThrid + "&pwd=" + password;
        OkHttpClientUtils.getJsonByDeleteRequest(url, token, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ViseLog.e("onError:" + e.getMessage());
                ToastUtils.showShort("Account cancellation failed");
            }

            @Override
            public void onResponse(String response, int id) {
                ViseLog.e("onResponse:" +response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("message");
                    if(code == 0) {
                        doDeleteUserInfo();
                    }else{
                        ToastUtils.showShort("Account cancellation failed");
                    }
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
                            ToastUtils.showShort("delete success");
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

        if(UserNoticeActivity.this != null) {
            UserNoticeActivity.this.finish();
        }
    }


    private void doSaveUserService() {

        GdprRequestModule gdprRequestModule = new GdprRequestModule();
        gdprRequestModule.setProductId(Constant1E.PRODUCT_ID);
        gdprRequestModule.setType(0);
        gdprRequestModule.setVersion("v1.0.1");

        GdprRequestModule gdprRequestModule1 = new GdprRequestModule();
        gdprRequestModule1.setProductId(Constant1E.PRODUCT_ID);
        gdprRequestModule1.setType(1);
        gdprRequestModule1.setVersion("v1.0.1");

        List<GdprRequestModule> list = new ArrayList<GdprRequestModule>();
        list.add(gdprRequestModule);
        list.add(gdprRequestModule1);

        GdprRequestListModule gdprRequestListModule = new GdprRequestListModule();
        gdprRequestListModule.setGdprRequestModuleList(list);

        ViseLog.d("gdprRequestListModule:" + GsonImpl.get().toJson(gdprRequestListModule));

        ViseHttp.POST("user-service-rest/v2/gdpr/saveUserPact").baseUrl(BaseHttpEntity.BASE_UBX_COMMON)
                .addHeader("authorization", SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN))
                .setJson(GsonImpl.get().toJson(gdprRequestListModule))
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        ViseLog.d("saveUserPact onSuccess :" + s);
                        finish();
                    }

                    @Override
                    public void onFail(int i, String s) {
                        ViseLog.e("saveUserPact onFail:" + i  +s);
                        ToastUtils.showShort(s);
                        finish();

                    }
                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
