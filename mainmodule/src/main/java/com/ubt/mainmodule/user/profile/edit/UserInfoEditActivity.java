package com.ubt.mainmodule.user.profile.edit;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.ubt.baselib.customView.BaseDialog;
import com.ubt.baselib.customView.BaseLoadingDialog;
import com.ubt.baselib.mvp.MVPBaseActivity;
import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.baselib.utils.FileUtils;
import com.ubt.baselib.utils.LQRPhotoSelectUtils;
import com.ubt.baselib.utils.ToastUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.ubt.mainmodule.user.profile.UserModel;
import com.vise.log.ViseLog;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.PermissionManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

    private UserModel userModel; //用户参数
    private Handler mHandler;
    LQRPhotoSelectUtils mLqrPhotoSelectUtils;
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
                        BaseLoadingDialog.dismiss(UserInfoEditActivity.this);
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
        if(userModel.getCountry().equals(SkinManager.getInstance().getTextById(R.string.main_profile_unfilled))){
            tvEditCountryContent.setText(userModel.getCountry());
        }else {
            tvEditCountryContent.setText(SkinManager.getInstance()
                    .getSkinArrayResource(R.array.main_country)[Integer.valueOf(userModel.getCountry())]);
        }
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

        // 1、创建LQRPhotoSelectUtils（一个Activity对应一个LQRPhotoSelectUtils）
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
//                // 4、当拍照或从图库选取图片成功后回调

//                Bitmap bitmap = null;
//                try {
//                    bitmap = FileUtils.getBitmapFormUri(UserInfoEditActivity.this, outputUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                Bitmap bitmap = null;
                try {
                    Matrix matrix = getImageMatrix(outputUri);
                    bitmap = FileUtils.getBitmapFormUriWithDegree(ContextUtils.getContext(), mImageUri, matrix);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Bitmap bitmap = FileUtils.getBitmapFormUri(ContextUtils.getContext(), mImageUri);
                ivEditIcon.setImageBitmap(bitmap);
                headPath = FileUtils.SaveImage(ContextUtils.getContext(), "head", bitmap);
                userModel.setIcon(headPath);
            }
        }, false);//true裁剪，false不裁剪
    }

    /**
     * 选择头像对话框
     */
    private void initDialog() {
        photoDialog = new FullSheetDialogFragment();
        photoDialog.setOnClickListener(new FullSheetDialogFragment.OnItemClickListener() {
            @Override
            public void OnCamera() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getCameraPermission();
                }else{
                    getShootCamera();
                }
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
                    mImageUri = FileProvider.getUriForFile(ContextUtils.getContext(), "com.ubt.baselib.fileProvider", cameraFile);
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
                Matrix matrix = getImageMatrix(mImageUri);
                Bitmap bitmap = FileUtils.getBitmapFormUriWithDegree(ContextUtils.getContext(), mImageUri, matrix);
//                Bitmap bitmap = FileUtils.getBitmapFormUri(ContextUtils.getContext(), mImageUri);
                ivEditIcon.setImageBitmap(bitmap);
                headPath = FileUtils.SaveImage(ContextUtils.getContext(), "head", bitmap);
                userModel.setIcon(headPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R2.id.iv_topbar_back, R2.id.tv_topbar_save, R2.id.iv_edit_icon, R2.id.iv_edit_icon_edit,
            R2.id.rg_edit_gender_select, R2.id.iv_edit_age_more, R2.id.iv_edit_country_more,
            R2.id.tv_edit_birthday_content, R2.id.tv_edit_country_content})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_topbar_back) {
            if(mPresenter.isUserInfoModified()){
                showQuitDialog();
            }else {
                Intent intent = new Intent();
                UserInfoEditActivity.this.setResult(RESULT_CANCELED, intent);
                finish();
            }
        } else if ((view.getId() == R.id.iv_edit_icon) || (view.getId() == R.id.iv_edit_icon_edit)) {
            showPhotoDialog();
        } else if (view.getId() == R.id.tv_topbar_save) {
            BaseLoadingDialog.show(UserInfoEditActivity.this);
            mPresenter.saveUserInfo(headPath);
        } else if ((view.getId() == R.id.iv_edit_age_more) || (view.getId() == R.id.tv_edit_birthday_content)) {
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
        } else if ((view.getId() == R.id.iv_edit_country_more)||(view.getId() == R.id.tv_edit_country_content)) {
            new CountrySelectDialog(UserInfoEditActivity.this, R.style.mainBirthDialogStyle)
                    .setListener(new CountrySelectDialog.ICountryDialogListener() {
                        @Override
                        public void onConfirm(int pos, String country) {
                            userModel.setCountry(pos+"");
                            tvEditCountryContent.setText(country);
                        }
                    }).show();
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
            Uri contentUri = FileProvider.getUriForFile(ContextUtils.getContext(), "com.ubt.baselib.fileProvider", cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            mImageUri = Uri.fromFile(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            cameraIntent.putExtra("return-data", true);
        }

        startActivityForResult(cameraIntent, PHOTO_BY_SHOOT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    private void showQuitDialog(){
        new BaseDialog.Builder(this)
                .setMessage(R.string.main_profile_quit_edit)
                .setConfirmButtonId(R.string.main_common_cancel)
                .setCancleButtonID(R.string.main_common_quit)
                .setButtonOnClickListener(new BaseDialog.ButtonOnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == com.ubt.baselib.R.id.button_cancle) {//确定按钮
                            Intent intent = new Intent();
                            UserInfoEditActivity.this.setResult(RESULT_CANCELED, intent);
                            UserInfoEditActivity.this.finish();
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void getCameraPermission(){
        PermissionManager.instance().with(this).request(new OnPermissionCallback() {
            @Override
            public void onRequestAllow(String permissionName) {
                getShootCamera();
            }

            @Override
            public void onRequestRefuse(String permissionName) {

            }

            @Override
            public void onRequestNoAsk(String permissionName) {

            }
        }, Manifest.permission.CAMERA);
    }


    private Matrix getImageMatrix(Uri uri){
        Matrix matrix = new Matrix();

        try {
            InputStream input = UserInfoEditActivity.this.getContentResolver().openInputStream(uri);
            ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exif = new ExifInterface(input);
            }else{
                exif = new ExifInterface(uri.getPath());
            }
            int degree=0;
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        degree = 0;
                        break;
                }
            }
            matrix.postRotate(degree);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }
}
