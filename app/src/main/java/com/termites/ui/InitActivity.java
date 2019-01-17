package com.termites.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.GetEdition;
import com.termites.ui.base.BaseActivity;

/**
 * Created by LF on 16/10/20.
 */

public class InitActivity extends BaseActivity {
    private MyCount count;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_init);


        tv_version = $_Act(R.id.tv_version);
        tv_version.setText("当前版本：" + GetEdition.getVersionName(this));
        // 开启倒计时,这里后面做一些数据初始化或者,数据上传,同步之类的操作
        count = new MyCount(3000, 1000);
        count.start();
    }

    private class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            // 跳转到登录界面
            startActivity(new Intent(getActivity(), LoginActivity.class));
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (count != null) {
            count.cancel();
            count = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
