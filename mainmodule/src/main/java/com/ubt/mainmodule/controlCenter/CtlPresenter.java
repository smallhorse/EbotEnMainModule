package com.ubt.mainmodule.controlCenter;

import android.os.Handler;
import android.os.Message;

import com.ubt.baselib.BlueTooth.BTReadData;
import com.ubt.baselib.BlueTooth.BTServiceStateChanged;
import com.ubt.baselib.btCmd1E.BTCmd;
import com.ubt.baselib.btCmd1E.ProtocolPacket;
import com.ubt.baselib.btCmd1E.cmd.BTCmdIRSensor;
import com.ubt.baselib.btCmd1E.cmd.BTCmdReadDevStatus;
import com.ubt.baselib.btCmd1E.cmd.BTCmdSensorControl;
import com.ubt.baselib.btCmd1E.cmd.BTCmdVolumeAdjust;
import com.ubt.baselib.mvp.BasePresenterImpl;
import com.ubt.bluetoothlib.blueClient.BlueClientUtil;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:31
 * @描述:
 */

public class CtlPresenter extends BasePresenterImpl<CtlContract.View> implements CtlContract.Presenter{
    private BlueClientUtil mBTUtil = null;
    private Handler mHandler;

    @Override
    public void init() {
        mBTUtil = BlueClientUtil.getInstance();
        mHandler = mView.getViewHandler();
        EventBus.getDefault().register(this);
        if(isBTConnected()){
            readRobotStatus();
        }else{
            ViseLog.e("蓝牙未连接!!!");
        }
    }

    private void readRobotStatus() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBTUtil.sendData(new BTCmdReadDevStatus().toByteArray());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBTUtil.sendData(new BTCmdSensorControl().toByteArray());
            }
        },1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBTUtil.sendData(new BTCmdIRSensor().toByteArray());
            }
        },2000);
    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setVol(int vol) {
        mBTUtil.sendData(new BTCmdVolumeAdjust((byte) (vol&0xff)).toByteArray());
    }

    @Override
    public void setFallDown(boolean status) {
        mBTUtil.sendData(new BTCmdSensorControl(status?BTCmdSensorControl.START:BTCmdSensorControl.STOP).toByteArray());
    }

    @Override
    public void setIRStatus(boolean status) {
        mBTUtil.sendData(new BTCmdIRSensor(status?BTCmdIRSensor.START:BTCmdIRSensor.STOP).toByteArray());
    }

    @Override
    public boolean isBTConnected(){
        return mBTUtil.getConnectionState() == BTServiceStateChanged.STATE_CONNECTED;
    }


    @Subscribe
    public void onBTRead(BTReadData data){
//        BTCmdHelper.parseBTCmd(data.getDatas(), mBTCmdListener);
        parseBTCmd(data);
    }

    @Subscribe
    public void onBTServiceStateChanged(BTServiceStateChanged stateChanged){
        if(stateChanged.getState() == BTServiceStateChanged.STATE_CONNECTED){
            readRobotStatus();
        }
    }

    private void parseBTCmd(BTReadData readData){
        ProtocolPacket packet = readData.getPack();
        Message msg;
        switch (packet.getmCmd()){
            case BTCmd.DV_SENSOR_CONTROL: //更新跌倒保护状态
                msg = mHandler.obtainMessage(CtlContract.HCMD_REFRESH_FALL);
                msg.arg1 = (int)packet.getmParam()[0];
                mHandler.sendMessage(msg);
                break;
            case BTCmd.DV_CONTROL_IR_SENSOR: //更新红外状态
                msg = mHandler.obtainMessage(CtlContract.HCMD_REFRESH_IR);
                msg.arg1 = (int)packet.getmParam()[0];
                mHandler.sendMessage(msg);
                break;
            case BTCmd.DV_READSTATUS: //更新音量状态
                msg = mHandler.obtainMessage(CtlContract.HCMD_REFRESH_VOL);
                byte data[] = packet.getmParam();
                if(data[0] == 0x02) {
                    msg.arg1 = packet.getmParam()[1]&0xff;
                    mHandler.sendMessage(msg);
                }
                break;
        }
    }
}
