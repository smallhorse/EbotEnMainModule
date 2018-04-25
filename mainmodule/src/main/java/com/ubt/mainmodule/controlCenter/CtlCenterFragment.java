package com.ubt.mainmodule.controlCenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import com.ubt.baselib.mvp.MVPBaseFragment;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
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
public class CtlCenterFragment extends MVPBaseFragment<CtlContract.View, CtlPresenter> implements CtlContract.View {


    @BindView(R2.id.seekbar_volume)
    SeekBar seekbarVolume;
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

        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(System.currentTimeMillis() - volTime > 500){
                    ViseLog.i("progress vol="+progress);
                    volTime = System.currentTimeMillis();
                    mPresenter.setVol(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ViseLog.i("progress vol="+seekBar.getProgress());
                mPresenter.setVol(seekBar.getProgress());
            }
        });

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
        if(!mPresenter.isBTConnected()){
            ViseLog.e("蓝牙未连接!!!");
            return;
        }
        if(view.getId() == R.id.switch_fall){
            mPresenter.setFallDown(switchFall.isChecked());
        }else if(view.getId() == R.id.switch_ir){
            mPresenter.setIRStatus(switchIr.isChecked());
        }
    }
}
