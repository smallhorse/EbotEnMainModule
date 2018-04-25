package com.ubt.mainmodule.user.about;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.baselib.skin.SkinManager;
import com.ubt.baselib.utils.ContextUtils;
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

public class AboutFragment extends SupportFragment {

    @BindView(R2.id.tv_about_check)    TextView tvAboutCheck;
    @BindView(R2.id.tv_about_version)   TextView tvAboutVersion;

    Unbinder unbinder;

    public static AboutFragment newInstance() {

        Bundle args = new Bundle();

        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        tvAboutCheck.setCompoundDrawables(null, null, null, null);
        String version =SkinManager.getInstance().getTextById(R.string.main_about_app_version) + getVersionName();
        tvAboutVersion.setText(version);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.tv_about_check)
    public void onViewClicked() {
    }


    /**
     * 获取当前本地apk的版本
     *
     * @return
     */
    public static String getVersionName() {
        String versionCode = "1.0.0.1";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = ContextUtils.getContext().getPackageManager().
                    getPackageInfo(ContextUtils.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
