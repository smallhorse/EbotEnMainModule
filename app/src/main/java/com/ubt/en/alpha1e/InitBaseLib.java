package com.ubt.en.alpha1e;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ubt.baselib.ConfigureBaseLib;
import com.vise.utils.handler.CrashHandlerUtil;


/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/4 14:47
 * @描述:
 */

public class InitBaseLib {

    public static void init(Application appContext) {
        boolean isIssue = false;

        CrashHandlerUtil.getInstance().init(appContext, null, "alphaEn_crash/");
        //初始化HttpEntity 必须在initNet()之前
        if (!TextUtils.isEmpty(BuildConfig.FLAVOR) && BuildConfig.FLAVOR.equals("ubt_issue_env")) {
            isIssue = true;
        }

        ConfigureBaseLib.getInstance().init(appContext,  isIssue); //默认为测试环境

        /**ARouter必须放到APP层初始，这是由于库的注解解释器所决定的*/
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(appContext); // 尽可能早，推荐在Application中初始化
    }

}
