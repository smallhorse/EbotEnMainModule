package com.ubt.mainmodule.user.language;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.ubt.baselib.globalConst.BaseHttpEntity;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.BaseResponseModel;
import com.ubt.baselib.model1E.LanguageModel;
import com.ubt.baselib.mvp.BasePresenterImpl;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.FileUtils;
import com.ubt.baselib.utils.GsonImpl;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.baselib.utils.ZipUtils;
import com.ubt.baselib.utils.http1E.OkHttpClientUtils;
import com.ubt.mainmodule.MainHttpEntity;
import com.ubt.mainmodule.R;
import com.vise.log.ViseLog;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:31
 * @描述:
 */

public class UserLanguagePresenter extends BasePresenterImpl<UserLanguageContract.View> implements UserLanguageContract.Presenter {

    private List<LanguageModel> mLanguageModels = new ArrayList<>();

    public List<LanguageModel> getLanguageModels() {
        return mLanguageModels;
    }

    @Override
    public void init() {
        String appLanguageVersion = SPUtils.getInstance().getString(Constant1E.CURRENT_APP_LANGUAGE_VERSION);
        if (TextUtils.isEmpty(appLanguageVersion)) {
            if (mView != null) {
                mView.showLoadingDialog();
            }
            getLanguageType(ContextUtils.getContext());
        } else {
            initLanguageData();
        }
    }

    /**
     * 初始化本地语言包版本号
     */
    public void initLanguageData() {
        mLanguageModels.clear();

        String[] languagesTitle = SkinManager.getInstance().getSkinArrayResource(R.array.main_ui_lanuages_title);
        String[] languagesContent = SkinManager.getInstance().getSkinArrayResource(R.array.main_ui_lanuages);
        String[] languagesUp = SkinManager.getInstance().getSkinArrayResource(R.array.main_ui_lanuages_up);
        for (int i = 0; i < languagesTitle.length; i++) {
            LanguageModel model = new LanguageModel(languagesTitle[i], languagesContent[i], languagesUp[i]);
            mLanguageModels.add(model);
            ViseLog.d(model.toString());
        }
        if (mView != null) {
            mView.notifyDataSetChanged();
        }
    }


    /**
     * 获取语言包最新版本
     *
     * @param context
     */

    private void getLanguageType(final Context context) {
        GetLanguageRequest request = new GetLanguageRequest();
        request.setUserId(String.valueOf(SPUtils.getInstance().getInt(Constant1E.SP_USER_ID)));
        request.setToken(SPUtils.getInstance().getString(Constant1E.SP_USER_TOKEN));
        request.setType("1");
        request.setVersion(SPUtils.getInstance().getString(Constant1E.CURRENT_APP_LANGUAGE_VERSION));
        ViseLog.d(request.toString());
        ViseLog.d(MainHttpEntity.GET_LANGUAGE_TYPE);
        ViseLog.d(BaseHttpEntity.BASIC_UBX_SYS);

        ViseHttp.POST(MainHttpEntity.GET_LANGUAGE_TYPE)
                .baseUrl("http://10.10.1.14:8080/")
                .setJson(GsonImpl.get().toJson(request))
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        ViseLog.d("getLanguageType==" + response);

                        BaseResponseModel<LanguageDownResponse> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<LanguageDownResponse>>() {
                                }.getType());
                        if (baseResponseModel.status) {
                            LanguageDownResponse languageDownResponse = baseResponseModel.models;
                            if (languageDownResponse != null) {
                                downLoadLanguage(languageDownResponse, context);
                            } else {
                                updateLanguageComplete();
                            }
                        } else {
                            updateLanguageComplete();
                        }
                    }

                    @Override
                    public void onFail(int i, String s) {
                        ViseLog.d(s);
                        updateLanguageComplete();
                    }
                });

    }


    /**
     * 下载压缩包及解压
     *
     * @param response
     * @param context
     */
    private void downLoadLanguage(final LanguageDownResponse response, final Context context) {
        final String path = FileUtils.getCacheDirectory(context, "") + Constant1E.LANUGAGE_DOWN_PATh + File.separator;//下载路径
        final String destPath = FileUtils.getCacheDirectory(context, "") + Constant1E.LANUGAGE_PATH;//解压文件路径

        OkHttpClientUtils.getDownloadFile(response.getUrl()).execute(new FileCallBack(path, Constant1E.LANGUAGE_ZIP_NAME) {
            @Override
            public void onError(Call call, Exception e, int id) {
                ViseLog.d("downLoadLanguage onError:" + e.getMessage());
                updateLanguageComplete();
            }

            @Override
            public void onResponse(final File dowloadFile, int id) {
                ViseLog.d("downLoadLanguage onResponse:" + dowloadFile.getAbsolutePath());

                ViseLog.d("zippath==" + dowloadFile.getAbsolutePath() + "    destPath = " + destPath);

                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        //判断本地是否有语言包
                        String skinPath = FileUtils.getCacheDirectory(context, "") + Constant1E.LANUGAGE_PATH +
                                File.separator + Constant1E.LANGUAGE_NAME;
                        File file = new File(skinPath);
                        ViseLog.d("assets===" + skinPath);
                        if (file.exists()) {
                            file.delete();
                        }
                        ZipUtils.unzipFile(dowloadFile.getAbsolutePath(), destPath);
                        e.onNext(1);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        ViseLog.d("integer==" + integer + " thread===" + Thread.currentThread().getName());
                        SPUtils.getInstance().put(Constant1E.CURRENT_APP_LANGUAGE_VERSION, response.getVersion());
                        updateLanguageComplete();
                    }
                });


            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                //  ViseLog.d("downloadVideo inProgress:" + progress);
            }
        });

    }

    private void updateLanguageComplete() {
        ViseLog.d("updateLanguageComplete");
        if (mView != null) {
            mView.dimissDialog();
        }
    }


    @Override
    public void release() {

    }
}
