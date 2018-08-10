package com.ubt.mainmodule.user;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.baselib.mvp.MVPBaseFragment;
import com.ubt.baselib.utils.ContextUtils;
import com.ubt.mainmodule.R;
import com.ubt.mainmodule.R2;
import com.ubt.mainmodule.user.about.AboutFragment;
import com.ubt.mainmodule.user.contact.ContactFragment;
import com.ubt.mainmodule.user.help.HelpFragment;
import com.ubt.mainmodule.user.language.LanguageFragment;
import com.ubt.mainmodule.user.logout.LogoutFragment;
import com.ubt.mainmodule.user.privacy.PrivacyFragment;
import com.ubt.mainmodule.user.profile.ProfileFragment;
import com.ubt.mainmodule.user.recycleview.LeftAdapter;
import com.ubt.mainmodule.user.security.SecurityFragment;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/18 10:30
 * @描述:
 */
public class UserMainFragment extends MVPBaseFragment<UserMainContract.View, UserMainPresenter> implements UserMainContract.View {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOUR = 3;
    public static final int FIVE = 4;
    public static final int SIX = 5;
    public static final int SEVEN = 6;
    public static final int EIGHT = 7;
//    public static final int NINE = 8;


    public static final int MAX_FRAGMENT = 8;

    @BindView(R2.id.rv_setting_left)
    RecyclerView recyclerView;
    @BindView(R2.id.frame_content_user)
    FrameLayout frameContentUser;
    Unbinder unbinder;

    private LeftAdapter leftAdapter;
    private SupportFragment[] mFragments = new SupportFragment[MAX_FRAGMENT];
    private int fragmentCur = FIRST; //标识当前fragment标号

    public static UserMainFragment newInstance() {

        Bundle args = new Bundle();

        UserMainFragment fragment = new UserMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRecycleView();
        if(savedInstanceState != null){
            fragmentCur = savedInstanceState.getInt("fragmentCur", FIRST);
        }
        initFragment();
        leftAdapter.clearChick();
        leftAdapter.getItem(fragmentCur).setChick(true);
        return view;

    }

    private void initFragment(){
        SupportFragment firstFragment = findChildFragment(ProfileFragment.class);
        if (firstFragment == null) {
            //            mFragments[SECOND] = NotificationFragment.newInstance();


            mFragments[FIRST] = ProfileFragment.newInstance();
            mFragments[SECOND] = AboutFragment.newInstance();
            mFragments[THIRD] = ContactFragment.newInstance();
            mFragments[FOUR] = HelpFragment.newInstance();
            mFragments[FIVE] = LanguageFragment.newInstance();
            mFragments[SIX] = PrivacyFragment.newInstance();
            mFragments[SEVEN] = LogoutFragment.newInstance();
            mFragments[EIGHT] = SecurityFragment.newInstance();
//            mFragments[NINE] = SecurityFragment.newInstance();


            loadMultipleRootFragment(R.id.frame_content_user, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOUR],
                    mFragments[FIVE],
                    mFragments[SIX],
                    mFragments[SEVEN],
                    mFragments[EIGHT]
//                    mFragments[NINE]
                    );
        } else {
            ViseLog.i("fragmentCur == "+ fragmentCur);
//            mFragments[SECOND] = findChildFragment(NotificationFragment.class);

            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(AboutFragment.class);
            mFragments[THIRD] = findChildFragment(ContactFragment.class);
            mFragments[FOUR] = findChildFragment(HelpFragment.class);
            mFragments[FIVE] = findChildFragment(LanguageFragment.class);
            mFragments[SIX] = findChildFragment(PrivacyFragment.class);
            mFragments[SEVEN] = findChildFragment(LogoutFragment.class);
            mFragments[EIGHT] = findChildFragment(SecurityFragment.class);
//            mFragments[NINE] = findChildFragment(SecurityFragment.class);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (leftAdapter!=null){
            leftAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragmentCur", fragmentCur);
    }

    private void initRecycleView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(ContextUtils.getContext()));
        leftAdapter = new LeftAdapter();
        leftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(fragmentCur != position) {
                    showHideFragment(mFragments[position], mFragments[fragmentCur]);
                    fragmentCur = position;
                    leftAdapter.clearChick();
                    leftAdapter.getItem(position).setChick(true);
                    leftAdapter.notifyDataSetChanged();
                }
            }
        });
        recyclerView.setAdapter(leftAdapter);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }
}
