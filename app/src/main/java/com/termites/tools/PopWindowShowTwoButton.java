package com.termites.tools;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.config.MethodConfig;
import com.termites.ui.base.BaseActivity;


/**
 * 信息弹出框,取消和确定按钮
 */
@SuppressLint("InflateParams")
public class PopWindowShowTwoButton extends PopupWindow {
    public Button btnSure, btnCancel;
    public TextView tv;
    private BaseActivity mActivity;
    private int mPosition;

    public PopWindowShowTwoButton(Context context) {
        mActivity = (BaseActivity) context;
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_alertdialog_twobutton, null);
        mActivity.set_$_View(mView);
        setView(mView);
    }

    private void setView(View mView) {
        setBackGround(mView);
        btnSure = mActivity.$_View(R.id.forgethandpwd_sure);
        btnCancel = mActivity.$_View(R.id.forgethandpwd_cancel);
        tv = mActivity.$_View(R.id.twobtn_dialog_tv);
    }

    public void setBackGround(View mView) {
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true); // 设置PopupWindow可获得焦点
        this.setTouchable(true); // 设置PopupWindow可触摸
        this.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        this.setContentView(mView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOnDismissListener(onDismissListener);
    }

    // 让builder消失
    public void dismissPop() {
        dismiss();
        MethodConfig.hideActivityAlpha(mActivity);
    }

    public int getPageIndex() {
        return this.mPosition;
    }


    public void show(String message, View mView, int gravity) {
        tv.setText(message);
        this.showAtLocation(mView, gravity, 0, 0);
        MethodConfig.showActivityAlpha(mActivity);
    }

    public void show(String message, int pageIndex, View mView, int gravity) {
        this.mPosition = pageIndex;
        tv.setText(message);
        show(message, mView, gravity);
    }

    OnDismissListener onDismissListener = new OnDismissListener() {

        @Override
        public void onDismiss() {
            MethodConfig.hideActivityAlpha(mActivity);
        }
    };


    public void setBtnSureText(String text) {
        btnSure.setText(text);
    }

    public void setOnClickListener(OnClickListener listener) {
        btnCancel.setOnClickListener(listener);
        btnSure.setOnClickListener(listener);
    }

    public void setBtnCancelText(String text) {
        btnCancel.setText(text);
    }

    public String getTextViewText() {
        return tv.getText().toString();
    }

    public String getBtnSureText() {
        return btnSure.getText().toString();
    }

    public String getBtnCancelText() {
        return btnCancel.getText().toString();
    }

}
