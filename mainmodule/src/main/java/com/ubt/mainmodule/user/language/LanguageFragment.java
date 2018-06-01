package com.ubt.mainmodule.user.language;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.mainmodule.R;
import com.vise.log.ViseLog;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 15:21
 * @描述:
 */

public class LanguageFragment extends SupportFragment {

    private TextView tvCurrentLanguage;
    private TextView tvChangeLanguage;

    public static LanguageFragment newInstance() {

        Bundle args = new Bundle();

        LanguageFragment fragment = new LanguageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_language, container, false);

        tvCurrentLanguage = view.findViewById(R.id.main_tv_current_language);
        tvChangeLanguage = view.findViewById(R.id.main_tv_change_language);
        initData();
        return view;
    }

    private void initData() {
        tvChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LanguageActivity.class));
            }
        });
        initLanguageData();
    }


    @Override
    public void onResume() {
        super.onResume();
        initLanguageData();
    }

    /**
     * 初始化本地语言包版本号
     */
    public void initLanguageData() {

        String[] languagesTitle = SkinManager.getInstance().getSkinArrayResource(R.array.base_ui_lanuages_title);
        String[] languagesContent = SkinManager.getInstance().getSkinArrayResource(R.array.base_ui_lanuages);
        String[] languagesUp = SkinManager.getInstance().getSkinArrayResource(R.array.base_ui_lanuages_up);
        String spLanguageType = SPUtils.getInstance().getString(Constant1E.CURRENT_APP_LANGUAGE);
        ViseLog.d("initLanguageData===" + spLanguageType);
        if (TextUtils.isEmpty(spLanguageType)) {
            tvCurrentLanguage.setText(String.format(SkinManager.getInstance().getTextById(R.string.main_language_content), languagesTitle[0]));
        } else {
            for (int i = 0; i < languagesUp.length; i++) {

                if (spLanguageType.equals(languagesUp[i])) {
                    ViseLog.d("languagesUp=equals==" + languagesTitle[i]);
                    String tip = SkinManager.getInstance().getTextById(R.string.main_language_content);
                    String lang = String.format(tip, languagesTitle[i]);
                    ViseLog.d("tip=="+tip+"  tit===" + languagesTitle[i]);
                    tvCurrentLanguage.setText(lang);
                    break;
                }
            }
        }
    }
}
