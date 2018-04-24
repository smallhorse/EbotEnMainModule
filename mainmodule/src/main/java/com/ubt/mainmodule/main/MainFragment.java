package com.ubt.mainmodule.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.mvp.MVPBaseFragment;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 17:31
 * @描述:
 */

public class MainFragment extends MVPBaseFragment<MainContract.View, MainPresenter> implements MainContract.View {

    @BindView(R2.id.iv_robot_status)
    ImageView ivRobotStatus;
    @BindView(R2.id.iv_robot_status_error)
    ImageView ivRobotStatusError;
    @BindView(R2.id.iv_robot_status_ok)
    ImageView ivRobotStatusOk;
    @BindView(R2.id.tv_charging_backgroup)
    TextView tvChargingBattary;
    @BindView(R2.id.iv_play_center)
    ImageView ivPlayCenter;
    @BindView(R2.id.iv_voice_cmd)
    ImageView ivVoiceCmd;
    @BindView(R2.id.iv_actions)
    ImageView ivActions;
    @BindView(R2.id.iv_blockly)
    ImageView ivBlockly;
    @BindView(R2.id.iv_community)
    ImageView ivCommunity;
    @BindView(R2.id.iv_joystick)
    ImageView ivJoystick;

    Unbinder unbinder;
    private Handler mHandler;
    private int prePower = -1;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        initHandler();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.init();
        mPresenter.queryBattery(true);
        setRobotStatus(mPresenter.isRobotConnected());
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.release();
        mPresenter.queryBattery(false);
    }

    @Override
    public Handler getViewHandler() {
        return mHandler;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R2.id.iv_robot_status, R2.id.iv_play_center, R2.id.iv_voice_cmd, R2.id.iv_actions,
            R2.id.iv_blockly, R2.id.iv_community, R2.id.iv_joystick})
    public void onViewClicked(View view) {
        if(view.getId() == R.id.iv_robot_status){
            ARouter.getInstance().build(ModuleUtils.Bluetooh_BleStatuActivity).navigation();
        }else if(view.getId() == R.id.iv_play_center){
            ARouter.getInstance().build(ModuleUtils.Playcenter_module).navigation();
        }else if(view.getId() == R.id.iv_voice_cmd){

        }else if(view.getId() == R.id.iv_actions){
            ARouter.getInstance().build(ModuleUtils.Actions_ActionProgram).navigation();
        }else if(view.getId() == R.id.iv_blockly){
            ARouter.getInstance().build(ModuleUtils.Blockly_BlocklyProgram).navigation();
        }else if(view.getId() == R.id.iv_community){
            ARouter.getInstance().build(ModuleUtils.Community_ActionProgram).navigation();
        }else if(view.getId() == R.id.iv_joystick){
            ARouter.getInstance().build(ModuleUtils.Joystick_ActionProgram).navigation();
        }
    }

    /**
     * 初始化handler
     */
    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MainContract.HCMD_REFRESH_BATTERY:
                        refreshBatteryStatus(msg.arg1);
                        break;
                    case MainContract.HCMD_LOST_BT:
                        setRobotStatus(false);
                        break;
                    case MainContract.HCMD_BT_CONNETED:
                        setRobotStatus(true);
                        break;
                    default:
                        break;
                }

            }
        };
    }

    /**
     * 刷新机器人电池状态
     */
    public void refreshBatteryStatus(int power) {
        if (prePower == power && isAdded() && getActivity()!= null) {
            return;
        }
        //纠正参数
        if (power < 0) {
            power = 0;
        } else if (power > 100) {
            power = 100;
        }

        //判断电量选择电量图标
        Drawable img = null;
        if (power <= 20 && (prePower > 20 || prePower < 0)) {
            img = getResources().getDrawable(R.mipmap.main_charging_red);
        } else if (power > 20 && power <= 40 && (prePower <= 20 || prePower > 40)) {
            img = getResources().getDrawable(R.mipmap.main_charging_yellow);
        } else if (power > 40 && power <= 90 && (prePower <= 40 || prePower > 90)) {
            img = getResources().getDrawable(R.mipmap.main_charging_green);
        } else if (power > 90 && prePower <= 90) {
            img = getResources().getDrawable(R.mipmap.main_charging_full);
        }

        prePower = power;
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        if (img != null) {
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            tvChargingBattary.setCompoundDrawables(img, null, null, null);
        }
        //更新电量显示
        tvChargingBattary.setText(power + "%");
    }

    /**
     * 设置机器人状态UI显示
     * @param isBTConnected 蓝牙是否连接成功
     */
    private void setRobotStatus(boolean isBTConnected){
        if(isBTConnected){ //蓝牙连接成功，隐藏错误状态
            ivRobotStatusError.setVisibility(View.INVISIBLE);
            tvChargingBattary.setVisibility(View.VISIBLE);
            ivRobotStatusOk.setVisibility(View.VISIBLE);
        }else{
            tvChargingBattary.setVisibility(View.INVISIBLE);
            ivRobotStatusOk.setVisibility(View.INVISIBLE);
            ivRobotStatusError.setVisibility(View.VISIBLE);
        }
    }
}
