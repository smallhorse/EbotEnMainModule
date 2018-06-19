package com.ubt.mainmodule.user.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.SPUtils;
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

    @BindView(R2.id.iv_profile_icon)    ImageView ivProfileIcon;
    @BindView(R2.id.tv_profile_name)    TextView tvProfileName;
    @BindView(R2.id.tv_profile_id)    TextView tvProfileId;
    @BindView(R2.id.tv_profile_age_content)    TextView tvProfileAgeContent;
    @BindView(R2.id.tv_profile_gender_content)    TextView tvProfileGenderContent;
    @BindView(R2.id.tv_profile_country_content)    TextView tvProfileCountryContent;

    Unbinder unbinder;
    private UserModel userModel = new UserModel(); //本地显示使用的数据结构 因为本地显示需要重新格式化，所以重新定义了类
    private UserInfoModel userInfo; //后台保存的数据结构

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
        unbinder = ButterKnife.bind(this, view);

        view.findViewById(R.id.iv_user_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoEditActivity.startActivity(ProfileFragment.this);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
        initView();

    }

    private void initData() {
        userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);
        if(userInfo != null) {
            if(userInfo.getSex() != null) {
                userModel.setGenderId(Integer.valueOf(userInfo.getSex()) - 1);
            }else{
                userModel.setGenderId(-1);
            }
            if(!TextUtils.isEmpty(userInfo.getBirthDate())) {
                userModel.setBirthday(userInfo.getBirthDate());
            }else{
                userModel.setBirthday(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
            }
            if(TextUtils.isEmpty(userInfo.getCountry())) {
                userModel.setCountry(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
            }else{
                try {
                    userModel.setCountry(SkinManager.getInstance()
                            .getSkinArrayResource(R.array.main_country)[Integer.valueOf(userInfo.getCountry())]);
                }catch (Exception e){

                }
            }
            userModel.setId(userInfo.getEmail());
            if(!TextUtils.isEmpty(userInfo.getNickName())) {
                userModel.setName(userInfo.getNickName());
            }else{
                userModel.setName("");
            }
            userModel.setIcon(userInfo.getHeadPic());
        }else{
            ViseLog.e("userInfo is null!!!!");
            userModel.setName(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
            userModel.setId(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
            userModel.setBirthday(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
            userModel.setGenderId(-1);
            userModel.setCountry(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
        }
    }

    private void initView() {
        if(userModel != null){
            if(userModel.getIcon() != null) {
                ViseLog.i("userModel.getIcon()="+userModel.getIcon());
                Glide.with(this).load(userModel.getIcon()).centerCrop().into(ivProfileIcon);
            }
            tvProfileName.setText(userModel.getName());
            tvProfileId.setText(userModel.getId());
            tvProfileAgeContent.setText(userModel.getBirthday());
            tvProfileGenderContent.setText(userModel.getGender());
            tvProfileCountryContent.setText(userModel.getCountry());
            switch (userModel.getGenderId()){
                case UserModel.MALE:
                    tvProfileGenderContent.setText(SkinManager.getInstance().getTextById(R.string.main_profile_gender_male));
                    break;
                case UserModel.FEMALE:
                    tvProfileGenderContent.setText(SkinManager.getInstance().getTextById(R.string.main_profile_gender_female));
                    break;
                case UserModel.OTHER:
                    tvProfileGenderContent.setText(SkinManager.getInstance().getTextById(R.string.main_profile_gender_robot));
                    break;
                default:
                    tvProfileGenderContent.setText(""/*SkinManager.getInstance().getTextById(R.string.main_profile_unfilled)*/);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UserInfoEditActivity.USERINFO_EDIT  &&resultCode == RESULT_OK){
            ViseLog.i("用户信息保存成功!!!");
            //兼容语言切换，在onResume()里刷新布局
            /*initData();
            initView();*/
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
