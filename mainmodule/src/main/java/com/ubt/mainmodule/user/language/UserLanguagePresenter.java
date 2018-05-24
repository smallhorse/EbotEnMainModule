package com.ubt.mainmodule.user.language;

import com.ubt.baselib.model1E.LanguageModel;
import com.ubt.baselib.mvp.BasePresenterImpl;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.mainmodule.R;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.List;

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
        initLanguageData();
    }

    private void initLanguageData() {
        mLanguageModels.clear();
        String[] languagesTitle = SkinManager.getInstance().getSkinArrayResource(R.array.main_ui_lanuages_title);
        String[] languagesContent = SkinManager.getInstance().getSkinArrayResource(R.array.main_ui_lanuages);
        String[] languagesUp = SkinManager.getInstance().getSkinArrayResource(R.array.main_ui_lanuages_up);
        for (int i = 0; i < languagesTitle.length; i++) {
            LanguageModel model = new LanguageModel(languagesTitle[i], languagesContent[i], languagesUp[i]);
            mLanguageModels.add(model);
            ViseLog.d(model.toString());
        }
    }


    @Override
    public void release() {

    }
}
