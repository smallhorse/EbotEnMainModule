package com.ubt.mainmodule.user.logout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.customView.BaseDialog;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.ManualEvent;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.utils.AppStatusUtils;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.bluetoothlib.blueClient.BlueClientUtil;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;

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

public class LogoutFragment extends SupportFragment {

    @BindView(R2.id.iv_logout_icon)    ImageView ivLogoutIcon;
    @BindView(R2.id.tv_logout_name)    TextView tvLogoutName;
    @BindView(R2.id.tv_logout_id)    TextView tvLogoutId;
    @BindView(R2.id.btn_logout)    Button btnLogout;

    Unbinder unbinder;
    private UserInfoModel userInfo;

    public static LogoutFragment newInstance() {

        Bundle args = new Bundle();

        LogoutFragment fragment = new LogoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_logout, container, false);
        unbinder = ButterKnife.bind(this, view);
//        initData();
        return view;
    }

    private void initData() {
        userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        if(userInfo != null){
            ViseLog.i("userInfo="+userInfo.toString());
            tvLogoutName.setText(userInfo.getNickName());
            if(!TextUtils.isEmpty(userInfo.getEmail()) && !userInfo.getEmail().equals("null")){
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

    @OnClick(R2.id.btn_logout)
    public void onViewClicked() {
        new BaseDialog.Builder(getActivity())
                .setMessage(R.string.main_log_out_dialogue)
                .setConfirmButtonId(R.string.main_common_cancel)
                .setConfirmButtonColor(R.color.main_black)
                .setCancleButtonID(R.string.main_log_out_tab)
                .setCancleButtonColor(R.color.main_red)
                .setButtonOnClickListener(new BaseDialog.ButtonOnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == com.ubt.baselib.R.id.button_confirm) {
                            dialog.dismiss();
                        } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                            //清除用户参数
                            ARouter.getInstance().build(ModuleUtils.Login_Module).navigation();
                            SPUtils.getInstance().remove(Constant1E.SP_USER_INFO);
                            dialog.dismiss();
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
                })
                .create()
                .show();
    }
}
