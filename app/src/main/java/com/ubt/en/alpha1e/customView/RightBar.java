package com.ubt.en.alpha1e.customView;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ubt.en.alpha1e.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/17 16:57
 * @描述:
 */

public class RightBar extends LinearLayout{
    public static final int TOP_ON = 1;
    public static final int CENTER_ON = 2;
    public static final int BOTTOM_ON = 3;
    @IntDef({TOP_ON, CENTER_ON, BOTTOM_ON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RightBarStatus {}

    private ImageView rightBarTopOn;
    private ImageView rightBarTopOff;
    private RelativeLayout rightBarTop;
    private ImageView rightBarCenterOn;
    private ImageView rightBarCenterOff;
    private RelativeLayout rightBarCenter;
    private ImageView rightBarBottomOn;
    private ImageView rightBarBottomOff;
    private RelativeLayout rightBarBottom;


    public RightBar(Context context) {
        this(context, null);
    }

    public RightBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.right_bar, this);
        rightBarTopOn = findViewById(R.id.right_bar_top_on);
        rightBarTopOff = findViewById(R.id.right_bar_top_off);
        rightBarTop = findViewById(R.id.right_bar_top);
        rightBarCenterOn = findViewById(R.id.right_bar_center_on);
        rightBarCenterOff = findViewById(R.id.right_bar_center_off);
        rightBarCenter = findViewById(R.id.right_bar_center);
        rightBarBottomOn = findViewById(R.id.right_bar_bottom_on);
        rightBarBottomOff = findViewById(R.id.right_bar_bottom_off);
        rightBarBottom = findViewById(R.id.right_bar_bottom);

    }

    /**
     *  上面控件点击事件注册监听
     * @param listener
     */
    public void setTopClickListener(@Nullable OnClickListener listener){
        rightBarTop.setOnClickListener(listener);
    }

    public void setCenterClickListener(@Nullable OnClickListener listener){
        rightBarCenter.setOnClickListener(listener);
    }

    public void setBottomClickListener(@Nullable OnClickListener listener){
        rightBarBottom.setOnClickListener(listener);
    }

    /**
     * 设置3个按钮状态
     * @param status
     */
    public void setRightBarStatus(@RightBarStatus int status){
        switch(status){
            case TOP_ON:
                //上面按钮选中效果
                rightBarTopOn.setVisibility(VISIBLE);
                rightBarTopOff.setVisibility(GONE);
                //中间按钮未选中
                rightBarCenterOn.setVisibility(GONE);
                rightBarCenterOff.setVisibility(VISIBLE);
                //下面按钮未选中
                rightBarBottomOn.setVisibility(GONE);
                rightBarBottomOff.setVisibility(VISIBLE);
                break;
            case CENTER_ON:
                //上面按钮未选中
                rightBarTopOn.setVisibility(GONE);
                rightBarTopOff.setVisibility(VISIBLE);
                //中间按钮选中效果
                rightBarCenterOn.setVisibility(VISIBLE);
                rightBarCenterOff.setVisibility(GONE);
                //下面按钮未选中
                rightBarBottomOn.setVisibility(GONE);
                rightBarBottomOff.setVisibility(VISIBLE);
                break;
            case BOTTOM_ON:
                //上面按钮选中效果
                rightBarTopOn.setVisibility(GONE);
                rightBarTopOff.setVisibility(VISIBLE);
                //中间按钮未选中
                rightBarCenterOn.setVisibility(GONE);
                rightBarCenterOff.setVisibility(VISIBLE);
                //下面按钮选中效果
                rightBarBottomOn.setVisibility(VISIBLE);
                rightBarBottomOff.setVisibility(GONE);
                break;
            default:
                break;
        }
    }
}
