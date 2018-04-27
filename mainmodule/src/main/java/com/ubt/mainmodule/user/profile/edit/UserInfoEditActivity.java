package com.ubt.mainmodule.user.profile.edit;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.baselib.mvp.MVPBaseActivity;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.FileUtils;
import com.ubt.baselib.utils.ToastUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.ubt.mainmodule.user.profile.UserModel;
import com.vise.log.ViseLog;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/20 10:48
 * @描述:
 */

public class UserInfoEditActivity extends MVPBaseActivity<UserInfoEditContract.View, UserInfoEditPresenter> implements UserInfoEditContract.View {
    public static final int PHOTO_BY_SHOOT = 1001; //拍照获取照片
    public static final int PHOTO_BY_FILE = 1002;  //相册获取
    public static final int USERINFO_EDIT = 1003;  //用户信息编辑

    @BindView(R2.id.iv_topbar_back)    ImageView ivTopbarBack;
    @BindView(R2.id.tv_topbar_save)    TextView tvTopbarSave;
    @BindView(R2.id.iv_edit_icon)    ImageView ivEditIcon;
    @BindView(R2.id.iv_edit_icon_edit)    ImageView ivEditIconEdit;
    @BindView(R2.id.rg_edit_gender_select)    RadioGroup rgEditGenderSelect;
    @BindView(R2.id.iv_edit_age_more)    ImageView ivEditAgeMore;
    @BindView(R2.id.iv_edit_country_more)    ImageView ivEditCountryMore;
    @BindView(R2.id.tv_edit_name)    TextView tvEditName;
    @BindView(R2.id.tv_edit_id)    TextView tvEditId;
    @BindView(R2.id.rb_edit_male)    RadioButton rbEditMale;
    @BindView(R2.id.rb_edit_female)    RadioButton rbEditFemale;
    @BindView(R2.id.rb_edit_robot)    RadioButton rbEditRobot;
    @BindView(R2.id.tv_edit_birthday_content)    TextView tvEditBirthdayContent;
    @BindView(R2.id.tv_edit_country_content)    TextView tvEditCountryContent;


    private FullSheetDialogFragment photoDialog;
    private File cameraFile;
    private String headPath;
    private Uri mImageUri;

    private UserModel userModel; //传过来的用户参数
    private Handler mHandler;

    public static void startActivity(Fragment context) {
        Intent intent = new Intent(context.getActivity(), UserInfoEditActivity.class);
        context.startActivityForResult (intent, USERINFO_EDIT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_userinfoedit);
        ButterKnife.bind(this);
        initHandler();
        mPresenter.init();
        userModel = mPresenter.getUserModel();
        initView();
        initDialog();

    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case UserInfoEditContract.HCMD_UPDATE_COMPLET:
                        if(msg.arg1 == 1){
                            Intent intent = new Intent();
                            UserInfoEditActivity.this.setResult(RESULT_OK, intent);
                            UserInfoEditActivity.this.finish();
                        }else{
                            ToastUtils.showShort(SkinManager.getInstance().getTextById(R.string.main_profile_saveFail));
                        }
                        break;
                }
            }
        };
    }


    private void initView() {
        if(userModel.getIcon() != null) {
            Glide.with(this).load(userModel.getIcon()).centerCrop().into(ivEditIcon);
        }
        tvEditBirthdayContent.setText(userModel.getBirthday());
        tvEditCountryContent.setText(userModel.getCountry());
        tvEditName.setText(userModel.getName());
        tvEditId.setText(userModel.getId());
        switch (userModel.getGenderId()){
            case UserModel.MALE:
                rbEditMale.setChecked(true);
                break;
            case UserModel.FEMALE:
                rbEditFemale.setChecked(true);
                break;
            case UserModel.OTHER:
                rbEditRobot.setChecked(true);
                break;
            default:
                break;
        }
        rgEditGenderSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_edit_male) {
                    userModel.setGenderId(UserModel.MALE);
                    userModel.setGender(""+rbEditMale.getText());
                } else if (checkedId == R.id.rb_edit_female) {
                    userModel.setGenderId(UserModel.FEMALE);
                    userModel.setGender(""+rbEditFemale.getText());
                } else if (checkedId == R.id.rb_edit_robot) {
                    userModel.setGenderId(UserModel.OTHER);
                    userModel.setGender(""+rbEditRobot.getText());
                }
                ViseLog.i(userModel.getGender());
            }
        });
    }

    /**
     * 选择头像对话框
     */
    private void initDialog() {
        photoDialog = new FullSheetDialogFragment();
        photoDialog.setOnClickListener(new FullSheetDialogFragment.OnItemClickListener() {
            @Override
            public void OnCamera() {
                getShootCamera();
                photoDialog.dismiss();
            }

            @Override
            public void OnAlbums() {
                choosePic();
                photoDialog.dismiss();
            }

            @Override
            public void OnCancel() {
                photoDialog.dismiss();
            }
        });
    }

    @Override
    public Handler getViewHandler() {
        return mHandler;
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_BY_SHOOT) {
                //用相机返回的照片去调用剪裁也需要对Uri进行处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mImageUri = FileProvider.getUriForFile(ContextUtils.getContext(), "com.ubt.setting", cameraFile);
                } else {
                    mImageUri = Uri.fromFile(cameraFile);
                }
                if (mImageUri == null) {
                    return;
                }
            } else if (requestCode == PHOTO_BY_FILE) {
                if (data == null) {
                    return;
                }
                ContentResolver cr = ContextUtils.getContext().getContentResolver();
                String type = cr.getType(data.getData());
                if (type == null) {
                    return;
                }
                mImageUri = data.getData();
            }

            try {
                Bitmap bitmap = FileUtils.getBitmapFormUri(ContextUtils.getContext(), mImageUri);
                ivEditIcon.setImageBitmap(bitmap);
                headPath = FileUtils.SaveImage(ContextUtils.getContext(), "head", bitmap);
//                    mPresenter.updateHead(headPath);
//                    LoadingDialog.show(getActivity());
                userModel.setIcon(headPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R2.id.iv_topbar_back, R2.id.tv_topbar_save, R2.id.iv_edit_icon, R2.id.iv_edit_icon_edit,
            R2.id.rg_edit_gender_select, R2.id.iv_edit_age_more, R2.id.iv_edit_country_more})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_topbar_back) {
            Intent intent = new Intent();
            UserInfoEditActivity.this.setResult(RESULT_CANCELED, intent);
            finish();
        } else if ((view.getId() == R.id.iv_edit_icon) || (view.getId() == R.id.iv_edit_icon_edit)) {
            showPhotoDialog();
        } else if (view.getId() == R.id.tv_topbar_save) {
            mPresenter.saveUserInfo();
        } else if (view.getId() == R.id.iv_edit_age_more) {
            new BirthdaySelectDialog(UserInfoEditActivity.this, R.style.mainBirthDialogStyle)
                    .setListener(new BirthdaySelectDialog.IBirthDialogListener() {
                        @Override
                        public void onConfirm(String birth) {
                            ViseLog.i("birth="+birth);
                            userModel.setBirthday(birth);
                            tvEditBirthdayContent.setText(userModel.getBirthday());
                        }
                    })
                    .show();
        } else if (view.getId() == R.id.iv_edit_country_more) {

        }
    }

    /**
     * 显示底部对话框
     */
    private void showPhotoDialog() {
        photoDialog.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * 通过相册获取头像
     */
    private void choosePic() {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, PHOTO_BY_FILE);

    }

    /**
     * 通过摄像头拍摄头像
     */
    public void getShootCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String catchPath = FileUtils.getCacheDirectory(ContextUtils.getContext(), "");
        File path = new File(catchPath + "/images");
        if (!path.exists()) {
            path.mkdirs();
        }
        cameraFile = new File(path, System.currentTimeMillis() + "");
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            cameraIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(ContextUtils.getContext(), "com.ubt.setting", cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            mImageUri = Uri.fromFile(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            cameraIntent.putExtra("return-data", true);
        }

        startActivityForResult(cameraIntent, PHOTO_BY_SHOOT);
    }

}
