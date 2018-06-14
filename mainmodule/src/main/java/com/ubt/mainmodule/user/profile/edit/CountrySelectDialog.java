package com.ubt.mainmodule.user.profile.edit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.weigan.loopview.LoopView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CountrySelectDialog extends Dialog {

    @BindView(R2.id.loopView_country)    LoopView loopViewCountry;


    private List<String> mListCountry; //国家列表形式
    private String[] mCountryArrays;   //所有国家的数组形式
    private ICountryDialogListener listener;

    public CountrySelectDialog(@NonNull Context context) {
        this(context, 0);
    }

    public CountrySelectDialog setListener(ICountryDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public CountrySelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dialog_user_country);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        UserInfoModel userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        mCountryArrays = SkinManager.getInstance().getSkinArrayResource(R.array.main_country);
        mListCountry = Arrays.asList(mCountryArrays);
        loopViewCountry.setItems(mListCountry);
        loopViewCountry.setItemsVisibleCount(9);
        loopViewCountry.setTextSize(16);


        //初始化当前位置
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getCountry())) {
            int countryPos = Integer.valueOf(userInfo.getCountry());
            loopViewCountry.setCurrentPosition(countryPos);
        }


    }

    @OnClick({R2.id.btn_country_cancel, R2.id.btn_country_confirm})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.btn_country_cancel) {
            CountrySelectDialog.this.dismiss();
        } else if (i == R.id.btn_country_confirm) {
            if (listener != null) {
                listener.onConfirm(loopViewCountry.getSelectedItem(),
                        mListCountry.get(loopViewCountry.getSelectedItem()));
            }
            CountrySelectDialog.this.dismiss();
        }
    }


    public interface ICountryDialogListener {
        void onConfirm(int pos, String country);
    }
}
