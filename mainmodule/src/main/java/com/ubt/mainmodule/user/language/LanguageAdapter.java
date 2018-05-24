package com.ubt.mainmodule.user.language;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.baselib.model1E.LanguageModel;
import com.ubt.mainmodule.R;

import java.util.List;

/**
 * @author：liuhai
 * @date：2018/5/22 16:38
 * @modifier：ubt
 * @modify_date：2018/5/22 16:38
 * [A brief description]
 * version
 */

public class LanguageAdapter extends BaseQuickAdapter<LanguageModel, BaseViewHolder> {

    private String selectedTitle;

    public void setSelectedTitle(String selectedTitle) {
        this.selectedTitle = selectedTitle;
        notifyDataSetChanged();
    }

    public LanguageAdapter(@LayoutRes int layoutResId, @Nullable List<LanguageModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LanguageModel item) {
        helper.setText(R.id.main_tv_item_title, item.getLanguageTitle());
        helper.setText(R.id.main_tv_item_content, item.getLanguageContent());
        ImageView ivSelected = helper.getView(R.id.main_item_selected);
        if (item.getLanguageTitle().equalsIgnoreCase(selectedTitle) || (TextUtils.isEmpty(selectedTitle) && item.getLanguageType().equals("en"))) {
            ivSelected.setVisibility(View.VISIBLE);
        } else {
            ivSelected.setVisibility(View.GONE);
        }
    }
}
