package com.ubt.mainmodule.user.profile.edit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.ubt.baselib.globalConst.Constant1E;
import com.ubt.baselib.model1E.UserInfoModel;
import com.ubt.baselib.utils.SPUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BirthdaySelectDialog extends Dialog {

    private static final int START_YEAR = 1900;
    private static final int END_YEAR = 2014;
    @BindView(R2.id.loopView_year)    LoopView loopViewYear;
    @BindView(R2.id.loopView_month)    LoopView loopViewMonth;
    @BindView(R2.id.loopView_day)    LoopView loopViewDay;
    @BindView(R2.id.btn_birth_cancel)    Button btnBirthCancel;
    @BindView(R2.id.btn_birth_confirm)    Button btnBirthConfirm;


    private List<String> listYear;
    private List<String> listMonth;
    private List<String> listday;
    private IBirthDialogListener listener;

    public BirthdaySelectDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BirthdaySelectDialog setListener(IBirthDialogListener listener){
        this.listener = listener;
        return this;
    }

    public BirthdaySelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dialog_user_age);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        UserInfoModel userInfo = (UserInfoModel) SPUtils.getInstance().readObject(Constant1E.SP_USER_INFO);

        getYearData();
        loopViewYear.setItemsVisibleCount(5);
        loopViewYear.setTextSize(18);


        getMonthData();
        loopViewMonth.setItemsVisibleCount(5);
        loopViewMonth.setTextSize(18);

        getDayData(31); //默认设置每月有31天，然后根据选择的月份再计算出相应月份的天数
        loopViewDay.setItemsVisibleCount(5);
        loopViewDay.setTextSize(18);

        //初始化当前位置
        if(userInfo != null && !TextUtils.isEmpty(userInfo.getBirthDate())){
            String[] birth = userInfo.getBirthDate().split("-");
            int yearPos = Integer.valueOf(birth[0]) - 1900;
            loopViewYear.setCurrentPosition(yearPos);

            int monthPos = Integer.valueOf(birth[1]) - 1;
            loopViewMonth.setCurrentPosition(monthPos);

            int dayPos = Integer.valueOf(birth[1]) - 1;
            loopViewDay.setCurrentPosition(dayPos);
        }

        loopViewYear.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {

            }
        });

        loopViewMonth.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int month = Integer.valueOf(listMonth.get(index));
                switch (month){
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        getDayData(31);
                        break;
                    case 2:
                        getDayData(28);
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        getDayData(30);
                        break;
                        default:break;
                }

            }
        });

    }


    private void setYearData(List<String> listYear) {
        this.listYear = listYear;
        loopViewYear.setItems(listYear);
    }

    private void setMonthData(List<String> listMonth) {
        this.listMonth = listMonth;
        loopViewMonth.setItems(listMonth);
    }

    private void setDayData(List<String> listDay) {
        this.listday = listDay;
        loopViewDay.setItems(listDay);
    }


    private void getYearData() {
        List<String> listYear = new ArrayList<String>();
        for (int i = START_YEAR; i <= END_YEAR; i++) {
            listYear.add(String.valueOf(i));
        }
        setYearData(listYear);
    }

    private void getMonthData() {
        List<String> listMonth = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            listMonth.add(String.valueOf(i));
        }

        setMonthData(listMonth);
    }

    private void getDayData(int dayCount) {
        List<String> listDay = new ArrayList<String>();
        for (int i = 1; i <= dayCount; i++) {
            listDay.add(String.valueOf(i));
        }

            setDayData(listDay);
    }


    @OnClick({R2.id.btn_birth_cancel, R2.id.btn_birth_confirm})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.btn_birth_cancel) {
            BirthdaySelectDialog.this.dismiss();
        } else if (i == R.id.btn_birth_confirm) {
            if(listener != null){
                String str=String.format("%s-%s-%s", listYear.get(loopViewYear.getSelectedItem()),
                                                    listMonth.get(loopViewMonth.getSelectedItem()),
                                                    listday.get(loopViewDay.getSelectedItem()));
                listener.onConfirm(str);
            }
            BirthdaySelectDialog.this.dismiss();
        }
    }


    public interface  IBirthDialogListener{
        void onConfirm(String birth);
    }
}
