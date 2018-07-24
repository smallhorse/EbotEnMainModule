package com.ubt.en.alpha1e;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.globalConst.BaseHttpEntity;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.utils.GsonImpl;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.baselib.utils.ToastUtils;
import com.ubt.en.alpha1e.customView.RightBar;
import com.ubt.en.alpha1e.module.GdprRequestListModule;
import com.ubt.en.alpha1e.module.GdprRequestModule;
import com.ubt.en.alpha1e.webview.UserNoticeActivity;
import com.ubt.en.alpha1e.webview.UserServiceActivity;
import com.ubt.mainmodule.controlCenter.CtlCenterFragment;
import com.ubt.mainmodule.main.MainFragment;
import com.ubt.mainmodule.user.UserMainFragment;
import com.vise.log.ViseLog;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

@Route(path = ModuleUtils.Main_MainActivity)
public class MainActivity extends SupportActivity {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    @BindView(R.id.right_bar)
    RightBar rightBar;

    private SupportFragment[] mFragments = new SupportFragment[3];
    private int fragmentCur = FIRST; //标识当前fragment标号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            fragmentCur = savedInstanceState.getInt("fragmentCur", FIRST);
        }
        initFragment();
        ButterKnife.bind(this);
        initRightBarClick(); //初始化右边栏点击事件
        doCheckUserService();
//        showDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void doCheckUserService() {
//        "https://test79.ubtrobot.com/user-service-rest/v2/gdpr/userPactInfo"
        String userId = SPUtils.getInstance().getString(Constant1E.SP_USER_ID);
        ViseLog.d("userId:" + userId);
        String url = "user-service-rest/v2/gdpr/userPactInfo?productId=" + Constant1E.PRODUCT_ID + "&userId=" + userId;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-UBT-AppId", Constant1E.APPID);
        headers.put("X-UBT-Sign", Constant1E.APPKEY);

        ViseHttp.GET(url).baseUrl(BaseHttpEntity.BASE_UBX_COMMON)
                .addHeaders(headers).request(new ACallback<String>() {
            @Override
            public void onSuccess(String s) {
                ViseLog.d("doCheckUserService onSuccess:" + s);
            }

            @Override
            public void onFail(int i, String s) {
                ViseLog.e("doCheckUserService onFail:" + i + s);

            }
        });

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
                        if(mDialogPlus != null) {
                            mDialogPlus.dismiss();
                        }
                    }

                    @Override
                    public void onFail(int i, String s) {
                        ViseLog.e("saveUserPact onFail:" + i  +s);
                        ToastUtils.showShort(s);
                        if(mDialogPlus != null) {
                            mDialogPlus.dismiss();
                        }
                    }
                });

    }

    DialogPlus mDialogPlus = null;

    private void showDialog(Context context){
        if (mDialogPlus != null) {
            mDialogPlus.dismiss();
            mDialogPlus = null;
        }

        View contentView = LayoutInflater.from(context).inflate(R.layout.base_dialog_user_privacy, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.55); //设置宽度
        int height = (int)((display.getHeight()) * 0.7);
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
                        if(id == R.id.tv_user_privacy){
                            Intent intent = new Intent();
                            intent.putExtra(UserServiceActivity.USER_CODE, UserServiceActivity.USER_PRIVACY_CODE);
                            intent.setClass(MainActivity.this, UserServiceActivity.class);
                            startActivity(intent);
                        }else if(id == R.id.tv_user_service){
                            Intent intent = new Intent();
                            intent.putExtra(UserServiceActivity.USER_CODE, UserServiceActivity.USER_SERVICE_CODE);
                            intent.setClass(MainActivity.this, UserServiceActivity.class);
                            startActivity(intent);
                        }else if(id == R.id.tv_disagree){
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, UserNoticeActivity.class);
                            startActivity(intent);
                        }else if(id == R.id.tv_agree){
                            doSaveUserService();
                        }

                    }
                })
                .setCancelable(false)
                .create();
        mDialogPlus.show();
    }



    private void initFragment(){
        SupportFragment firstFragment = findFragment(MainFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = MainFragment.newInstance();
            mFragments[SECOND] =CtlCenterFragment.newInstance();
            mFragments[THIRD] = UserMainFragment.newInstance();

            loadMultipleRootFragment(R.id.frame_content, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
        } else {
            ViseLog.i("fragmentCur == "+ fragmentCur);
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findFragment(CtlCenterFragment.class);
            mFragments[THIRD] = findFragment(UserMainFragment.class);
        }
    }

    /**
     * 右边栏为自定义VIEW点击事件使用传统的方式实现
     */
    private void initRightBarClick() {
        switch (fragmentCur){
            case FIRST:
                rightBar.setRightBarStatus(RightBar.TOP_ON);
                break;
            case SECOND:
                rightBar.setRightBarStatus(RightBar.CENTER_ON);
                break;
            case THIRD:
                rightBar.setRightBarStatus(RightBar.BOTTOM_ON);
                break;
            default:
                rightBar.setRightBarStatus(RightBar.TOP_ON);
                break;
        }

        rightBar.setTopClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCur != FIRST) {
                    rightBar.setRightBarStatus(RightBar.TOP_ON);
                    showHideFragment(mFragments[FIRST], mFragments[fragmentCur]);
                    fragmentCur = FIRST;
//                    start(mainFragment);
                }
            }
        });

        rightBar.setCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCur != SECOND) {
                    rightBar.setRightBarStatus(RightBar.CENTER_ON);
                    showHideFragment(mFragments[SECOND], mFragments[fragmentCur]);
                    fragmentCur = SECOND;
                }
            }
        });

        rightBar.setBottomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCur != THIRD) {
                    rightBar.setRightBarStatus(RightBar.BOTTOM_ON);
                    showHideFragment(mFragments[THIRD], mFragments[fragmentCur]);
                    fragmentCur = THIRD;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragmentCur", fragmentCur);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
