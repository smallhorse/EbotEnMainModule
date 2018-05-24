package com.ubt.mainmodule.user.language;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.mainmodule.R;

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
    }
}
