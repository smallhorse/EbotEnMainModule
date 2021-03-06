package com.ubt.mainmodule.main;

import android.os.Handler;
import android.os.Message;

import com.ubt.baselib.BlueTooth.BTReadData;
import com.ubt.baselib.BlueTooth.BTServiceStateChanged;
import com.ubt.baselib.btCmd1E.BTCmd;
import com.ubt.baselib.btCmd1E.ProtocolPacket;
import com.ubt.baselib.btCmd1E.cmd.BTCmdGetWifiStatus;
import com.ubt.baselib.btCmd1E.cmd.BTCmdReadBattery;
import com.ubt.baselib.mvp.BasePresenterImpl;
import com.ubt.bluetoothlib.blueClient.BlueClientUtil;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/17 14:45
 * @描述:
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter{

    private Handler mViewHandler;
    private BlueClientUtil mBTUtil;

    @Override
    public void init() {
        mViewHandler = mView.getViewHandler();
        EventBus.getDefault().register(MainPresenter.this);
        mBTUtil = BlueClientUtil.getInstance();
    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(MainPresenter.this);
    }

    @Override
    public void queryBattery(boolean isStart) {
        if(!isRobotConnected()){
            ViseLog.e("robot not connected");
            return;
        }
        mViewHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isRobotConnected() ) {
                    mBTUtil.sendData(new BTCmdReadBattery().toByteArray());
                }
            }
        },200);
    }

    @Override
    public boolean isRobotConnected() {
        return mBTUtil.getConnectionState() == BTServiceStateChanged.STATE_CONNECTED;
    }


    @Subscribe
    public void onBTRead(BTReadData data){
//        BTCmdHelper.parseBTCmd(data.getDatas(), mBTCmdListener);
        parseBTCmd(data);
    }

    @Subscribe
    public void onBTServiceStateChanged(BTServiceStateChanged stateChanged){
        if(stateChanged.getState() == BTServiceStateChanged.STATE_DISCONNECTED){//蓝牙掉线
            mViewHandler.sendEmptyMessage(MainContract.HCMD_LOST_BT);
            queryBattery(false);
        }else if(stateChanged.getState() == BTServiceStateChanged.STATE_CONNECTED){//蓝牙连接成功
            mViewHandler.sendEmptyMessage(MainContract.HCMD_BT_CONNETED);
            queryBattery(true);
            mBTUtil.sendData(new BTCmdGetWifiStatus().toByteArray()); //查询WIFI联网状态
        }
    }


    private void parseBTCmd(BTReadData readData){
        ProtocolPacket packet = readData.getPack();
        switch (packet.getmCmd()){
            case BTCmd.DV_READ_BATTERY: //更新电量
                if(packet.getmParamLen() < 4){
                    ViseLog.e("电量参数错误，丢弃!!!");
                    return;
                }
                Message msg = mViewHandler.obtainMessage(MainContract.HCMD_REFRESH_BATTERY);
                msg.arg1 = (int)packet.getmParam()[3];
                mViewHandler.sendMessage(msg);
                break;
            case BTCmd.DV_READ_NETWORK_STATUS: //wifi联网状态
                String status = new String(packet.getmParam());
                try {
                    JSONObject jStatus = new JSONObject(status);
                    if(jStatus.has("status")){
                        ViseLog.i("status="+status);
                        if(jStatus.getBoolean("status")){
                            mViewHandler.sendEmptyMessage(MainContract.HCMD_ROBOT_WIFI_CONNETED);
                        }else{
                            mViewHandler.sendEmptyMessage(MainContract.HCMD_ROBOT_WIFI_DISCONNETED);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
