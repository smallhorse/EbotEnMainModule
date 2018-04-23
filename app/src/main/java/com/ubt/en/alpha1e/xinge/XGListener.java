package com.ubt.en.alpha1e.xinge;

import android.content.Context;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.ubt.xingemodule.IXGListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 09:43
 * @描述:
 */

public class XGListener implements IXGListener {

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        ViseLog.d("onRegisterResult");
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        ViseLog.d("onUnregisterResult");
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        ViseLog.d("onSetTagResult");
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        ViseLog.d("onDeleteTagResult");
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        ViseLog.d("onTextMessage");
        EventBus.getDefault().post(xgPushTextMessage);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        ViseLog.d("onNotifactionClickedResult");
        EventBus.getDefault().post(xgPushClickedResult);
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        ViseLog.d("onNotifactionShowedResult");
        EventBus.getDefault().post(xgPushShowedResult);
    }
}
