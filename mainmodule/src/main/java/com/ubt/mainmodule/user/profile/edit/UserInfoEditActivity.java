package com.ubt.mainmodule.user.profile.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ubt.baselib.mvp.MVPBaseActivity;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/20 10:48
 * @描述:
 */

public class UserInfoEditActivity extends MVPBaseActivity<UserInfoEditContract.View, UserInfoEditPresenter> implements UserInfoEditContract.View {


    @BindView(R2.id.iv_topbar_back)
    ImageView ivTopbarBack;
    @BindView(R2.id.tv_topbar_save)
    TextView tvTopbarSave;
    @BindView(R2.id.iv_edit_icon)
    ImageView ivEditIcon;
    @BindView(R2.id.iv_edit_icon_edit)
    ImageView ivEditIconEdit;
    @BindView(R2.id.rg_edit_gender_select)
    RadioGroup rgEditGenderSelect;
    @BindView(R2.id.iv_edit_age_more)
    ImageView ivEditAgeMore;
    @BindView(R2.id.iv_edit_country_more)
    ImageView ivEditCountryMore;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserInfoEditActivity.class);
        /*Bundle args = new Bundle();

        intent.setData(args);*/
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_userinfoedit);
        ButterKnife.bind(this);

    }

    @Override
    public Handler getViewHandler() {
        return null;
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @OnClick({R2.id.iv_topbar_back, R2.id.tv_topbar_save, R2.id.iv_edit_icon, R2.id.iv_edit_icon_edit,
            R2.id.rg_edit_gender_select, R2.id.iv_edit_age_more, R2.id.iv_edit_country_more})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_topbar_back) {
            finish();
        } else if ((view.getId() == R.id.iv_edit_icon) || (view.getId() == R.id.iv_edit_icon_edit)) {
            showPhotoDialog();
        } else if (view.getId() == R.id.tv_topbar_save) {

        } else if (view.getId() == R.id.rg_edit_gender_select) {

        } else if (view.getId() == R.id.iv_edit_age_more) {

        } else if (view.getId() == R.id.iv_edit_country_more) {

        }
    }

    private void showPhotoDialog() {
       /* BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.main_dialog_photo, null);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();*/
        new FullSheetDialogFragment().show(getSupportFragmentManager(), "dialog");
    }

    private void choosePic() {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
//        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);

    }
}
