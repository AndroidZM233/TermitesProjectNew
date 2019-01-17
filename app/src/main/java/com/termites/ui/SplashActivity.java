package com.termites.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.termites.ui.base.BaseActivity;

/**
 * Created by LF on 16/10/20.
 * 程序入口,解决启动白屏停留的问题
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        startActivity(new Intent(getActivity(), InitActivity.class));
        finish();
    }
}
