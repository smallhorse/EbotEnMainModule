package com.ubt.en.alpha1e.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ubt.en.alpha1e.R;


/**
 * @author ubt
 *
 * 设置多语言
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configFragment();
    }

    private void configFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commitAllowingStateLoss();
    }


}
