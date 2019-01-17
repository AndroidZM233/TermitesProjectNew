package com.termites.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.termites.R;
import com.termites.interfaces.IBase;
import com.termites.tools.ActivityManagerDone;
import com.termites.tools.PopWindowShowOneButton;
import com.termites.tools.MyProgressDialog;
import com.termites.tools.RepeatClick;
import com.termites.tools.ShowToast;
import com.termites.tools.ViewUtils;

/**
 * Created by LF on 16/10/20.
 */
public class BaseActivity extends FragmentActivity implements IBase {
    private PopWindowShowOneButton errorDialog;
    private MyProgressDialog progressDialog;
    private View mView = null;
    // 重复点击限制
    private RepeatClick rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rc = new RepeatClick();
        ActivityManagerDone.addOneActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mView = null;
        ActivityManagerDone.finishOneActivityWithOutFinish(this);
        // 销毁Dialog
        destroyErrorDialog();
    }

    @Override
    public void toast(int resId) {
        ShowToast.getInstance().show(getString(resId), R.drawable.toast_icon_fail);
    }

    @Override
    public void toast(String text) {
        toast(text, R.drawable.toast_icon_fail);
    }

    @Override
    public void toast(int resId, int resImgId) {
        ShowToast.getInstance().show(resId, resImgId);
    }

    @Override
    public void toast(String text, int resImgId) {
        ShowToast.getInstance().show(text, resImgId);
    }

    // 提供子类使用的Context，不在需要调用Activity.this
    protected BaseActivity getActivity() {
        return this;
    }

    @Override
    public void setRepeat(View v) {
        rc.setRepeatClick(v);
    }

    @Override
    public void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new MyProgressDialog(getActivity());
        }
        progressDialog.show(message);
    }

    @Override
    public void showProgress(int message) {
        showProgress(getString(message));
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public PopWindowShowOneButton onCreateErrorDialog() {
        return new PopWindowShowOneButton(getActivity());
    }

    @Override
    public void showErrorDialog(String message, View mView) {
        showErrorDialog(message, null, mView);
    }

    @Override
    public void showErrorDialog(String message, View.OnClickListener listener, View mView) {
        if (errorDialog == null) {
            errorDialog = onCreateErrorDialog();
        }
        errorDialog.setOnClickListener(listener);
        errorDialog.show(message, mView);
    }

    @Override
    public void hideErrorDialog() {
        errorDialog.dismissPop();
    }

    private void destroyErrorDialog() {
        if (errorDialog != null) {
            errorDialog = null;
        }
    }

    /**
     * 简化findViewById
     */
    public <E extends View> E $_Act(int resId) {
        return ViewUtils.findViewById(getActivity(), resId);
    }

    /**
     * 简化findViewById
     */
    public <E extends View> E $_View(int resId) {
        return ViewUtils.findViewById(get_$_View(), resId);
    }

    public void set_$_View(View mView) {
        this.mView = mView;
    }

    public View get_$_View() {
        return this.mView;
    }
}
