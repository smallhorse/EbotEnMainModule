package com.ubt.en.alpha1e;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ubt.baselib.commonModule.ModuleUtils;
import com.ubt.en.alpha1e.customView.RightBar;
import com.ubt.mainmodule.controlCenter.CtlCenterFragment;
import com.ubt.mainmodule.main.MainFragment;
import com.ubt.mainmodule.user.UserMainFragment;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

@Route(path = ModuleUtils.Main_MainActivity)
public class MainActivity extends SupportActivity {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    @BindView(R.id.right_bar)
    RightBar rightBar;

    private SupportFragment[] mFragments = new SupportFragment[3];
    private int fragmentCur = FIRST; //标识当前fragment标号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            fragmentCur = savedInstanceState.getInt("fragmentCur", FIRST);
        }
        initFragment();
        ButterKnife.bind(this);
        initRightBarClick(); //初始化右边栏点击事件
    }

    private void initFragment(){
        SupportFragment firstFragment = findFragment(MainFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = MainFragment.newInstance();
            mFragments[SECOND] =CtlCenterFragment.newInstance();
            mFragments[THIRD] = UserMainFragment.newInstance();

            loadMultipleRootFragment(R.id.frame_content, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
        } else {
            ViseLog.i("fragmentCur == "+ fragmentCur);
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findFragment(CtlCenterFragment.class);
            mFragments[THIRD] = findFragment(UserMainFragment.class);
        }
    }

    /**
     * 右边栏为自定义VIEW点击事件使用传统的方式实现
     */
    private void initRightBarClick() {
        switch (fragmentCur){
            case FIRST:
                rightBar.setRightBarStatus(RightBar.TOP_ON);
                break;
            case SECOND:
                rightBar.setRightBarStatus(RightBar.CENTER_ON);
                break;
            case THIRD:
                rightBar.setRightBarStatus(RightBar.BOTTOM_ON);
                break;
            default:
                rightBar.setRightBarStatus(RightBar.TOP_ON);
                break;
        }

        rightBar.setTopClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCur != FIRST) {
                    rightBar.setRightBarStatus(RightBar.TOP_ON);
                    showHideFragment(mFragments[FIRST], mFragments[fragmentCur]);
                    fragmentCur = FIRST;
//                    start(mainFragment);
                }
            }
        });

        rightBar.setCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCur != SECOND) {
                    rightBar.setRightBarStatus(RightBar.CENTER_ON);
                    showHideFragment(mFragments[SECOND], mFragments[fragmentCur]);
                    fragmentCur = SECOND;
                }
            }
        });

        rightBar.setBottomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCur != THIRD) {
                    rightBar.setRightBarStatus(RightBar.BOTTOM_ON);
                    showHideFragment(mFragments[THIRD], mFragments[fragmentCur]);
                    fragmentCur = THIRD;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragmentCur", fragmentCur);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
