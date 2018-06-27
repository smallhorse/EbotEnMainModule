package com.ubt.mainmodule.user.contact;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.ubt.baselib.customView.BaseDialog;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.vise.log.ViseLog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 15:21
 * @描述:
 */

public class ContactFragment extends SupportFragment {

    @BindView(R2.id.tv_contact_phone)    TextView tvContactPhone;
    @BindView(R2.id.tv_contact_email)    TextView tvContactEmail;
    @BindView(R2.id.tv_contact_website)    TextView tvContactWebsite;

    Unbinder unbinder;

    public static ContactFragment newInstance() {

        Bundle args = new Bundle();

        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R2.id.tv_contact_phone, R2.id.tv_contact_email, R2.id.tv_contact_website})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_contact_phone) {
            showDialog(R.string.main_contact_us_telephone_call, new BaseDialog.ButtonOnClickListener() {
                @Override
                public void onClick(final DialogPlus dialog, View view) {
                    if (view.getId() == com.ubt.baselib.R.id.button_confirm) {
                        dialog.dismiss();
                    } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                        AndPermission.with(ContextUtils.getContext())
                                .requestCode(10000)
                                .permission(Manifest.permission.CALL_PHONE)
                                .callback(new PermissionListener() {
                                    @Override
                                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+getString(R.string.main_contact_us_telephone)));
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                        dialog.dismiss();
                                    }
                                })
                                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                                // 你也可以不设置。
                                .rationale(new RationaleListener() {
                                    @Override
                                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                        rationale.resume();
                                    }
                                })
                                .start();


                    }
                }
            });
        } else if (i == R.id.tv_contact_email) {
            showDialog(R.string.main_contact_us_email_send, new BaseDialog.ButtonOnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    if (view.getId() == com.ubt.baselib.R.id.button_confirm) {

                        dialog.dismiss();
                    } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                        dialog.dismiss();
                        sendEmail();
                        /*Uri uri = Uri.parse("mailto:"+getString(R.string.main_contact_us_email_address));
                        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                        startActivity(it);*/

                    }
                }
            });
        } else if (i == R.id.tv_contact_website) {
            showDialog(R.string.main_contact_us_website_go, new BaseDialog.ButtonOnClickListener() {
                @Override
                public void onClick(DialogPlus dialog, View view) {
                    if (view.getId() == com.ubt.baselib.R.id.button_confirm) {
                        dialog.dismiss();
                    } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                        Uri uri = Uri.parse("http://"+getString(R.string.main_contact_us_website_address));
                        Intent it   = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(it);
                        dialog.dismiss();
                    }
                }
            });
        }
    }


    private void showDialog(int title, BaseDialog.ButtonOnClickListener buttonOnClickListener){
        new BaseDialog.Builder(getActivity())
                .setMessage(title)
                .setConfirmButtonId(R.string.main_common_cancel)
                .setCancleButtonID(R.string.main_common_sure)
                .setButtonOnClickListener(buttonOnClickListener)
                .create()
                .show();
    }

    /**
     * 发邮件
     */
    private void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/plain"); //模拟器请使用这行
        i.setType("message/rfc822"); // 真机上使用这行
        i.putExtra(Intent.EXTRA_EMAIL,new String[] { getString(R.string.main_contact_us_email_address) });
        //i.putExtra(Intent.EXTRA_SUBJECT, "您的建议");
        //i.putExtra(Intent.EXTRA_TEXT, "我们很希望能得到您的建议!");
        startActivity(Intent.createChooser(i,getString(R.string.main_contact_us_email_select)));
    }
}
