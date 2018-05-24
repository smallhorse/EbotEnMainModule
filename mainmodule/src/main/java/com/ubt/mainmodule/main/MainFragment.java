package com.ubt.mainmodule.main;

import android.graphics.RectF;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.mvp.MVPBaseFragment;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.AppStatusUtils;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.mainmodule.FloatView;
import com.ubt.mainmodule.MainHttpEntity;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.xsnow.cache.SpCache;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.shape.CircleLightShape;
import zhy.com.highlight.shape.RectLightShape;
import zhy.com.highlight.view.HightLightView;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 17:31
 * @描述:
 */

public class MainFragment extends MVPBaseFragment<MainContract.View, MainPresenter> implements MainContract.View,
        View.OnClickListener{

    private static final String IS_FIRST_RUN = "isFirst";

    @BindView(R2.id.iv_robot_status)    ImageView ivRobotStatus;
    @BindView(R2.id.iv_robot_status_error)    ImageView ivRobotStatusError;
    @BindView(R2.id.iv_robot_status_ok)    ImageView ivRobotStatusOk;
    @BindView(R2.id.tv_charging_backgroup)    TextView tvChargingBattary;
    @BindView(R2.id.iv_play_center)    ImageView ivPlayCenter;
    @BindView(R2.id.iv_voice_cmd)    ImageView ivVoiceCmd;
    @BindView(R2.id.iv_actions)    RelativeLayout ivActions;
    @BindView(R2.id.iv_blockly)    RelativeLayout ivBlockly;
    @BindView(R2.id.iv_community)    RelativeLayout ivCommunity;
    @BindView(R2.id.iv_joystick)    RelativeLayout ivJoystick;

    Unbinder unbinder;
    private Handler mHandler;
    private int prePower = -1;
    private HighLight mHightLight;
    private View mainView;
    private FloatView mFloatView;
    private SpCache spCache;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppStatusUtils.setBussiness(false);
        AppStatusUtils.setBtBussiness(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.main_fragment_main, container, false);
        unbinder = ButterKnife.bind(this, mainView);
        mFloatView = new FloatView(getContext());
        initHandler();
        spCache = new SpCache(ContextUtils.getContext());
        if(spCache.get(IS_FIRST_RUN, true)) {
            spCache.put(IS_FIRST_RUN, false);
            showUserGide();
        }
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.init();
        mPresenter.queryBattery(true);
        setRobotStatus(mPresenter.isRobotConnected());
        setRobotChargeStatus(mPresenter.isRobotConnected());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    @OnClick({R2.id.iv_robot_status, R2.id.iv_play_center, R2.id.iv_voice_cmd, R2.id.iv_actions,
            R2.id.iv_blockly, R2.id.iv_community, R2.id.iv_joystick})
    public void onViewClicked(View view) {
        if(view.getId() == R.id.iv_robot_status){
//            BaseLowBattaryDialog.getInstance().showLow5Dialog();
            ARouter.getInstance().build(ModuleUtils.Bluetooh_BleStatuActivity).navigation();
        }else if(view.getId() == R.id.iv_play_center){
            ARouter.getInstance().build(ModuleUtils.Playcenter_module).navigation();
        }else if(view.getId() == R.id.iv_voice_cmd){
            ARouter.getInstance().build(ModuleUtils.BaseWebview_module)
                    .withString(ModuleUtils.BaseWebview_KEY_URL, MainHttpEntity.VOICE_CMD)
                    .navigation();
        }else if(view.getId() == R.id.iv_actions){
//            BlueClientUtil.getInstance().connect("A0:2C:36:89:F3:7D");
//            BlueClientUtil.getInstance().connect("88:83:5D:B8:39:0C");
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
                    case MainContract.HCMD_LOST_BT: //蓝牙断开充电和机器人状态都断开
                        setRobotStatus(false);
                        setRobotChargeStatus(false);
                        break;
                    case MainContract.HCMD_BT_CONNETED: //蓝牙连接成功，充电状态
                        setRobotChargeStatus(true);
                        break;
                    case MainContract.HCMD_ROBOT_WIFI_CONNETED: //wifi连接成功，机器人状态TURE
                        setRobotStatus(true);
                        break;
                    case MainContract.HCMD_ROBOT_WIFI_DISCONNETED://wifi断开，机器人状态FALSE
                        setRobotStatus(false);
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
     * @param isWIFIConnected WIFI是否连接成功
     */
    private void setRobotStatus(boolean isWIFIConnected){
        if(isWIFIConnected){ //蓝牙连接成功，隐藏错误状态
            ivRobotStatusError.setVisibility(View.INVISIBLE);
            ivRobotStatusOk.setVisibility(View.VISIBLE);
        }else{
            ivRobotStatusOk.setVisibility(View.INVISIBLE);
            ivRobotStatusError.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 充电状态显示
     * @param isBTConnected 蓝牙是否连接
     */
    private void setRobotChargeStatus(boolean isBTConnected){
        if(isBTConnected){ //蓝牙连接成功，隐藏错误状态
            tvChargingBattary.setVisibility(View.VISIBLE);
        }else{
            tvChargingBattary.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 新手指引
     */
    public  void showUserGide(){
        mHightLight = new HighLight(this.getActivity())//
                .autoRemove(false)
                .intercept(true)
                .enableNext()
                .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
                    @Override
                    public void onLayouted() {
                        //界面布局完成添加tipview
                        mHightLight.addHighLight(R.id.iv_robot_status,R.layout.main_guide_robot_status, new HighLight.OnPosCallback() {
                            @Override
                            public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                marginInfo.leftMargin = rectF.right + 45;
                                marginInfo.topMargin = rectF.top + 100;
                            }} ,new CircleLightShape(-18,-18))
                                .addHighLight(R.id.iv_voice_cmd,R.layout.main_guide_voice_cmd,new HighLight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                        marginInfo.rightMargin = rightMargin + rectF.width() + 45;
                                        marginInfo.topMargin = rectF.top + 100;
                                    }},new CircleLightShape(-18,-18))
                                .addHighLight(R.id.iv_play_center,R.layout.main_guide_play_center,new HighLight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                        marginInfo.rightMargin = rightMargin + rectF.width() + 45;
                                        marginInfo.topMargin = rectF.top + 100;
                                    }},new CircleLightShape(-18,-18))
                                .addHighLight(R.id.iv_actions,R.layout.main_guide_actions,new HighLight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                        marginInfo.leftMargin = rectF.right + 45;
                                        marginInfo.topMargin = rectF.top + 45;;
                                    }},new RectLightShape(-5,-5,5,15,15))
                                .addHighLight(R.id.iv_blockly,R.layout.main_guide_blockly,new HighLight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                        marginInfo.leftMargin = rightMargin ;
                                        marginInfo.topMargin = rectF.top + rectF.height() -120;
                                    }},new RectLightShape(-5,-5,5,15,15))
                                .addHighLight(R.id.iv_community,R.layout.main_guide_community,new HighLight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                        marginInfo.leftMargin = rectF.right - rectF.width() - rightMargin;
                                        marginInfo.topMargin = bottomMargin +80;
                                    }},new RectLightShape(-5,-5,5,15,15))
                                .addHighLight(R.id.iv_joystick,R.layout.main_guide_joystick,new HighLight.OnPosCallback() {
                                    @Override
                                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                        marginInfo.leftMargin = rectF.right - rectF.width()*2.0f - 45;
                                        marginInfo.topMargin = bottomMargin +140;
                                    }},new RectLightShape(-5,-5,5,15,15))
                          .setOnNextCallback(new HighLightInterface.OnNextCallback() {
                            @Override
                            public void onNext(HightLightView hightLightView, View targetView, View tipView) {
                                // targetView 目标按钮 tipView添加的提示布局 可以直接找到'我知道了'按钮添加监听事件等处理
                                TextView textView = tipView.findViewById(R.id.tv_got_it);
                                textView.setOnClickListener(MainFragment.this);

                                if(targetView.getId() == R.id.iv_joystick){
                                    mFloatView.dismissFloatButton();
                                }

                            }
                        });
                        //然后显示高亮布局
                        mHightLight.show();

                        mFloatView.addButton(SkinManager.getInstance().getTextById(R.string.main_guide_skip),
                                40, 120, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHightLight.remove();
                                mFloatView.dismissFloatButton();
                            }
                        });
                    }
                });


    }



    @Override
    public void onClick(View v) {
        if(mHightLight.isShowing() && mHightLight.isNext())//如果开启next模式
        {
            mHightLight.next();
        }else
        {
            mHightLight.remove();
        }
    }

}
