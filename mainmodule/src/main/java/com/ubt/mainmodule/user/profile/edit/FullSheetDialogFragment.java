package com.ubt.mainmodule.user.profile.edit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import com.ubt.mainmodule.R;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/23 12:15
 * @描述:
 */

public class FullSheetDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private OnItemClickListener listener;

    public FullSheetDialogFragment setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.main_dialog_photo, null);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        TextView photoCamera = view.findViewById(R.id.tv_profile_camera);
        TextView photoAlbums = view.findViewById(R.id.tv_profile_albums);
        TextView photoCancel = view.findViewById(R.id.tv_profile_cancel);

        photoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnCamera();
                }
            }
        });

        photoAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnAlbums();
                }
            }
        });

        photoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnCancel();
                }
            }
        });
        return dialog;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//全屏展开
    }

    public interface OnItemClickListener {
        void OnCamera();
        void OnAlbums();
        void OnCancel();
    }
}
