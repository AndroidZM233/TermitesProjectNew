package com.termites.tools;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘显示隐藏工具类
 * Created by LF on 16/10/21.
 */
public class ShowInputMethodManager {

    // 隐藏键盘
    public static void hideSoftInput(final View v) {
        InputMethodManager m = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    // 显示键盘
    public static void showInput(final View v) {
        v.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(v, 0);
            }
        }, 500);
    }

}
