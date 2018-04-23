package com.ubt.en.alpha1e;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.utils.ToastUtils;
import com.vise.log.ViseLog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/15 15:14
 * @描述:
 */

public class WelcomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION_MULTI = 1111;

    @BindView(R.id.gif_start_welcome)
    ImageView gifWelcome;
    GifDrawable gifDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initView();
        gifDrawable.start();
    }

    private void initView() {
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.gif_welcome);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted() {
                    applyPermission();
                }
            });
            gifWelcome.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void applyPermission(){
        ViseLog.d("申请权限");
        // 申请多个权限。
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_MULTI)
                .permission(Permission.CAMERA, Permission.LOCATION, Permission.STORAGE,Permission.MICROPHONE)
                .callback(permissionListener)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        rationale.resume();
                    }
                })
                .start();
    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {

                case REQUEST_CODE_PERMISSION_MULTI:
                    ViseLog.d("申请权限 onSucceed requestCode="+requestCode+
                                                    "grantPermissions:"+grantPermissions);
                    startMainActivity();
                    break;
                default:
                    break;

            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {

                case REQUEST_CODE_PERMISSION_MULTI:
                    ViseLog.d("申请权限 onFailed requestCode="+requestCode+
                            "deniedPermissions="+deniedPermissions.toString());
                    ToastUtils.showShort("申请权限失败");
                    WelcomActivity.this.finish();
                    break;
                default:
                    break;

            }

        }

    };

    /**
     * 跳转到其它模块
     */
    private void startMainActivity(){
        String startModule = ModuleUtils.Main_MainActivity;
        /*UserModel userModel = (UserModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        if (null != userModel) {
            if (!TextUtils.isEmpty(userModel.getPhone())){
                if (TextUtils.isEmpty(userModel.getAge())) {
                   // startModule = ModuleUtils.Login_UserEdit;
                    startModule = ModuleUtils.Main_MainActivity;

                } else {
                    startModule = ModuleUtils.Main_MainActivity;
                }
            }
        }*/
        ARouter.getInstance().build(startModule).navigation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WelcomActivity.this.finish();
            }
        },1000);

    }
}
