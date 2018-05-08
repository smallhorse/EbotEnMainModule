package com.ubt.mainmodule.user.data;

import android.content.Context;

import com.ubt.baselib.utils.ContextUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.user.recycleview.LeftMenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 13:37
 * @描述:
 */

public class DataServer {
    private static List<LeftMenuModel> datas = new ArrayList<>();

    private DataServer(){

    }

    public static List<LeftMenuModel> getLeftData(){
        if(datas.size() <= 0) {
            Context context = ContextUtils.getContext();
            datas.add(new LeftMenuModel(context.getString(R.string.main_profile_tab),
                    true, R.mipmap.main_user_profile, 0));
            datas.add(new LeftMenuModel(context.getString(R.string.main_notifications_tab),
                    false, R.mipmap.main_user_notification, 0));
            datas.add(new LeftMenuModel(context.getString(R.string.main_about_app_tab),
                    false, R.mipmap.main_user_about, 0));
            datas.add(new LeftMenuModel(context.getString(R.string.main_contact_us_tab),
                    false, R.mipmap.main_user_contact, 0));
            datas.add(new LeftMenuModel(context.getString(R.string.main_help_feedback_tab),
                    false, R.mipmap.main_user_help, 0));
            datas.add(new LeftMenuModel(context.getString(R.string.main_language_tab),
                    false, R.mipmap.main_user_lanuague, 0));
            datas.add(new LeftMenuModel(context.getString(R.string.main_log_out_tab),
                    false, R.mipmap.main_user_logout, 0));
        }
        return datas;
    }

    public static void clearChick(){
        if(datas.size() > 0){
            for (LeftMenuModel model: datas) {
                model.setChick(false);
            }
        }
    }
}
