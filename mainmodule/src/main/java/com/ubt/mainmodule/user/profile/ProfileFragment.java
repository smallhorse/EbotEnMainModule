package com.ubt.mainmodule.user.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.ubt.mainmodule.user.profile.edit.UserInfoEditActivity;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 15:21
 * @描述:
 */

public class ProfileFragment extends SupportFragment {

    @BindView(R2.id.iv_profile_icon)
    ImageView ivProfileIcon;
    @BindView(R2.id.tv_profile_name)
    TextView tvProfileName;
    @BindView(R2.id.tv_profile_id)
    TextView tvProfileId;
    @BindView(R2.id.tv_profile_age_content)
    TextView tvProfileAgeContent;
    @BindView(R2.id.tv_profile_gender_content)
    TextView tvProfileGenderContent;
    @BindView(R2.id.tv_profile_country_content)
    TextView tvProfileCountryContent;

    Unbinder unbinder;
    private UserModel userModel = null;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_profile, container, false);
        initData();
        initView();
        view.findViewById(R.id.iv_user_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoEditActivity.startActivity(ProfileFragment.this, userModel);
            }
        });

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initData() {

    }

    private void initView() {
        if(userModel != null){
            if(userModel.getIcon() != null) {
                Glide.with(this).load(userModel.getIcon()).centerCrop().into(ivProfileIcon);
            }
            tvProfileName.setText(userModel.getName());
            tvProfileId.setText(userModel.getId());
            tvProfileAgeContent.setText(userModel.getCountry());
            tvProfileGenderContent.setText(userModel.getGender());
            tvProfileCountryContent.setText(userModel.getCountry());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UserInfoEditActivity.USERINFO_EDIT  &&resultCode == RESULT_OK){
            ViseLog.i("用户信息保存成功!!!");
        }else{
            ViseLog.i("取消保存!!!");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
