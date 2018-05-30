package com.ubt.mainmodule.controlCenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.baselib.customView.BaseBTDisconnectDialog;
import com.ubt.baselib.mvp.MVPBaseFragment;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.ubt.mainmodule.signSeekBar.SignSeekBar;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:30
 * @描述:
 */
public class CtlCenterFragment extends MVPBaseFragment<CtlContract.View, CtlPresenter> implements CtlContract.View,
        View.OnTouchListener{


    @BindView(R2.id.seekbar_volume)
    SignSeekBar seekbarVolume;
    @BindView(R2.id.switch_fall)
    Switch switchFall;
    @BindView(R2.id.switch_ir)
    Switch switchIr;
    Unbinder unbinder;

    private Handler mHandler;
    private long volTime = System.currentTimeMillis();

    public static CtlCenterFragment newInstance() {

        Bundle args = new Bundle();

        CtlCenterFragment fragment = new CtlCenterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_ctrl_center, container, false);
        unbinder = ButterKnife.bind(this, view);
        seekbarVolume.setOnTouchListener(this);
        seekbarVolume.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                ViseLog.i("progress vol="+progress);
                if(System.currentTimeMillis() - volTime > 500){
                    if(!mPresenter.isBTConnected()){
                        ViseLog.e("蓝牙未连接!!!");
                        showBTDisDialog();
                        return;
                    }
                    ViseLog.i("progress vol="+progress);
                    volTime = System.currentTimeMillis();
                    mPresenter.setVol(progress);
                }
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                ViseLog.i("progress vol="+signSeekBar.getProgress());
                if(!mPresenter.isBTConnected()){
                    ViseLog.e("蓝牙未连接!!!");
                    return;
                }
                mPresenter.setVol(signSeekBar.getProgress());
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                ViseLog.i("progress vol="+signSeekBar.getProgress());
            }
        });
        switchFall.setOnTouchListener(this);
        switchIr.setOnTouchListener(this);
        initHandler();
        mPresenter.init();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case CtlContract.HCMD_REFRESH_VOL:
                        refreshVolStatus(msg.arg1);
                        break;
                    case CtlContract.HCMD_REFRESH_FALL:
                        refreshFallStatus(msg.arg1 == 1);
                        break;
                    case CtlContract.HCMD_REFRESH_IR:
                        refreshIRStatus(msg.arg1 == 1);
                        break;
                    default:
                        break;
                }

            }
        };
    }

    @Override
    public Handler getViewHandler() {
        return mHandler;
    }

    public void refreshVolStatus(int vol) {
        seekbarVolume.setProgress(vol);
    }

    public void refreshFallStatus(boolean status) {
        if(isAdded() && getActivity() != null){
            switchFall.setChecked(status);
        }
    }

    public void refreshIRStatus(boolean status) {
        if(isAdded() && getActivity() != null){
            switchIr.setChecked(status);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R2.id.switch_fall, R2.id.switch_ir})
    public void onViewClicked(View view) {
        if(view.getId() == R.id.switch_fall){
            mPresenter.setFallDown(switchFall.isChecked());
        }else if(view.getId() == R.id.switch_ir){
            mPresenter.setIRStatus(switchIr.isChecked());
        }
    }


    private void showBTDisDialog(){
        if(BaseBTDisconnectDialog.getInstance().isShowing()){
            return;
        }
        BaseBTDisconnectDialog.getInstance().show(new BaseBTDisconnectDialog.IDialogClick() {
            @Override
            public void onConnect() {
                ARouter.getInstance().build(ModuleUtils.Bluetooh_BleStatuActivity).navigation();
                BaseBTDisconnectDialog.getInstance().dismiss();
            }

            @Override
            public void onCancel() {
                BaseBTDisconnectDialog.getInstance().dismiss();
            }
        });
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mPresenter.isBTConnected()){
            return false;
        }

        int viewId = v.getId();
        if (((viewId == R.id.seekbar_volume) ||
            (viewId == R.id.switch_ir) ||
            (viewId == R.id.switch_fall))){
            if((event.getAction() == MotionEvent.ACTION_UP)) { //抬起时才弹框,其它时候只拦截
                ViseLog.e("蓝牙未连接!!!viewId=" + viewId);
                showBTDisDialog();
            }
            return true;
        }

        return false;
    }
}
