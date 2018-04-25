package com.ubt.mainmodule.user.logout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.ubt.baselib.customView.BaseDialog;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;

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

public class LogoutFragment extends SupportFragment {

    @BindView(R2.id.iv_logout_icon)    ImageView ivLogoutIcon;
    @BindView(R2.id.tv_logout_name)    TextView tvLogoutName;
    @BindView(R2.id.tv_logout_id)    TextView tvLogoutId;
    @BindView(R2.id.btn_logout)    Button btnLogout;

    Unbinder unbinder;

    public static LogoutFragment newInstance() {

        Bundle args = new Bundle();

        LogoutFragment fragment = new LogoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_logout, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.btn_logout)
    public void onViewClicked() {
        new BaseDialog.Builder(getActivity())
                .setMessage(R.string.main_log_out_dialogue)
                .setConfirmButtonId(R.string.main_common_cancel)
                .setConfirmButtonColor(R.color.main_black)
                .setCancleButtonID(R.string.main_log_out_tab)
                .setCancleButtonColor(R.color.main_red)
                .setButtonOnClickListener(new BaseDialog.ButtonOnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == com.ubt.baselib.R.id.button_confirm) {
                            dialog.dismiss();
                        } else if (view.getId() == com.ubt.baselib.R.id.button_cancle) {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }
}
