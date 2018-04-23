package com.ubt.en.alpha1e.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vise.log.ViseLog;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/1/8 15:16
 * @描述: 处理全局消息的服务
 */

public class GlobalMsgService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
      //  EventBus.getDefault().register(this);
        ViseLog.i("onCreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViseLog.i("onDestroy");
       // EventBus.getDefault().unregister(this);
    }

//    @Subscribe
//    public void onDataSynEvent(XGPushShowedResult xgPushShowedResult) {
//        ViseLog.i("onDataSynEvent event---->" + xgPushShowedResult.getContent());
//        try {
//            JSONObject mJson = new JSONObject(xgPushShowedResult.getCustomContent());
//            if( mJson.getString("category").equals(XGConstact.BEHAVIOUR_HABIT)) {
//                if (mJson.get("eventId") != null) {
//                    Log.d("TPush"," contents"+xgPushShowedResult.getContent());
//                   /* new HibitsAlertDialog(AppManager.getInstance().currentActivity()).builder()
//                            .setCancelable(true)
//                            .setEventId(mJson.get("eventId").toString())
//                            .setMsg(xgPushShowedResult.getContent())
//                            .show();*/
//                    //  new LowBatteryDialog(AppManager.getInstance().currentActivity()).setBatteryThresHold(1000000).builder().show();
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
