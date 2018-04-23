package com.ubt.mainmodule.controlCenter;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import com.ubt.baselib.mvp.MVPBaseFragment;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:30
 * @描述:
 */
public class CtlCenterFragment extends MVPBaseFragment<CtlContract.View, CtlPresenter> implements CtlContract.View {


    @BindView(R2.id.seekbar_volume)
    SeekBar seekbarVolume;
    @BindView(R2.id.switch_fall)
    Switch switchFall;
    @BindView(R2.id.switch_ir)
    Switch switchIr;
    Unbinder unbinder;

    public static CtlCenterFragment newInstance() {

        Bundle args = new Bundle();

        CtlCenterFragment fragment = new CtlCenterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_ctrl_center, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public Handler getViewHandler() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R2.id.seekbar_volume, R2.id.switch_fall, R2.id.switch_ir})
    public void onViewClicked(View view) {
        if(view.getId() == R.id.seekbar_volume){

        }else if(view.getId() == R.id.switch_fall){

        }else if(view.getId() == R.id.switch_ir){

        }
    }
}
