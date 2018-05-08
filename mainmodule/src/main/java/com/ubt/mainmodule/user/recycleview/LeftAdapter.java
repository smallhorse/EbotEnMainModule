package com.ubt.mainmodule.user.recycleview;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.user.data.DataServer;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/19 13:09
 * @描述:
 */

public class LeftAdapter extends BaseQuickAdapter<LeftMenuModel, BaseViewHolder> {

    public LeftAdapter() {
        super(R.layout.main_user_item, DataServer.getLeftData());
    }

    @Override
    protected void convert(BaseViewHolder helper, LeftMenuModel item) {
        helper.setText(R.id.tv_tab, item.getNameString());
        helper.setImageResource(R.id.iv_icon, item.getImageId());
        helper.setVisible(R.id.tv_msgcnt, false);

        TextView barView = helper.getView(R.id.tv_msgcnt);
        if(item.getCountUnRead() > 0){
            helper.setVisible(R.id.tv_msgcnt, true);
            barView.setText(item.getCountUnRead()+"");
        }
        if (item.isChick()) {
              helper.setBackgroundColor(R.id.rl_item, ContextUtils.getContext().getResources().getColor(R.color.main_grey));
        } else {
              helper.setBackgroundColor(R.id.rl_item, ContextUtils.getContext().getResources().getColor(R.color.white));
        }
       /* Drawable drawable = getActivity().getResources().getDrawable(item.getImageId());
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        TextView tv = helper.getView(R.id.tv_item_name);
        tv.setCompoundDrawables(drawable, null, null, null);
        if (item.isChick()) {
            tv.setEnabled(true);
        } else {
            tv.setEnabled(false);
        }
        if (item.getNameString().equals("消息")) {
            if (item.getCountUnRead() > 0) {
                barView.setVisibility(View.VISIBLE);
                if (item.getCountUnRead() < 100) {
                    barView.setText(item.getCountUnRead()+"");
                } else {
                    barView.setText("99+");
                }
            }
        } else {
            barView.setVisibility(View.GONE);
        }*/

    }

    public void clearChick(){
        DataServer.clearChick();
    }
}
