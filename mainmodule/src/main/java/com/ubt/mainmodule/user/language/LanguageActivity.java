package com.ubt.mainmodule.user.language;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.baselib.customView.BaseLoadingDialog;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.LanguageModel;
import com.ubt.baselib.mvp.MVPBaseActivity;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LanguageActivity extends MVPBaseActivity<UserLanguageContract.View, UserLanguagePresenter> implements UserLanguageContract.View, BaseQuickAdapter.OnItemClickListener {


    Unbinder mUnbinder;
    @BindView(R2.id.main_iv_back)
    ImageView mMainIvBack;
    @BindView(R2.id.main_tv_title)
    TextView mMainTvTitle;
    @BindView(R2.id.main_tv_done)
    TextView mMainTvDone;
    @BindView(R2.id.main_language_list)
    RecyclerView mMainLanguageList;

    LanguageAdapter mAdapter;

    private LanguageModel mLanguageModel;

    @Override
    public int getContentViewId() {
        return R.layout.main_activity_language;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnbinder = ButterKnife.bind(this);
        initData();
        mPresenter.init();
    }


    private void initData() {
         mAdapter = new LanguageAdapter(R.layout.main_language_item, mPresenter.getLanguageModels());
        mMainLanguageList.setLayoutManager(new LinearLayoutManager(this));
        mMainLanguageList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setSelectedLanguageType(SPUtils.getInstance().getString(Constant1E.CURRENT_APP_LANGUAGE));
    }


    @OnClick({R2.id.main_iv_back, R2.id.main_tv_done})
    public void ClockView(View view) {
        if (view.getId() == R.id.main_iv_back) {
            finish();
        } else if (view.getId() == R.id.main_tv_done) {
            if (mLanguageModel != null) {
                if (mLanguageModel.getLanguageType().equals("en")) {
                    SkinManager.getInstance().restoreDefaultTheme();
                    if (mLanguageModel != null) {
                        SPUtils.getInstance().put(Constant1E.CURRENT_APP_LANGUAGE, mLanguageModel.getLanguageType());
                    }
                    finish();
                } else {
                    SkinManager.getInstance().loadSkin(mLanguageModel.getLanguageType(), new SkinManager.SkinListener() {

                        @Override
                        public void onStart() {
                            ViseLog.d("开始加载多语言");
                        }

                        @Override
                        public void onSuccess() {
                            ViseLog.d("成功加载多语言");
                            if (mLanguageModel != null) {
                                SPUtils.getInstance().put(Constant1E.CURRENT_APP_LANGUAGE, mLanguageModel.getLanguageType());
                            }
                            finish();
                        }

                        @Override
                        public void onFailed(String errMsg) {
                            ViseLog.d("加载多语言失败" + errMsg);
                            finish();
                        }
                    });
                }
            }else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        LanguageModel model = mPresenter.getLanguageModels().get(position);
        if (mLanguageModel != null) {
            if (!mLanguageModel.getLanguageTitle().equals(model.getLanguageTitle())) {
                mLanguageModel = model;
                mMainTvDone.setEnabled(true);
                mMainTvDone.setTextColor(getResources().getColorStateList(R.color.base_blue));
                mAdapter.setSelectedLanguageType(mLanguageModel.getLanguageType());
            }
        } else {
            mMainTvDone.setEnabled(true);
            mMainTvDone.setTextColor(getResources().getColorStateList(R.color.base_blue));
            mLanguageModel = model;
            mAdapter.setSelectedLanguageType(mLanguageModel.getLanguageType());
        }


    }

    @Override
    public void showLoadingDialog() {
        BaseLoadingDialog.show(this);
    }

    @Override
    public void dimissDialog() {
        BaseLoadingDialog.dismiss(this);
        mPresenter.initLanguageData();

    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}
