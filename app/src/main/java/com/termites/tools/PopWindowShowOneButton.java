package com.termites.tools;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
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
 * 提示信息弹出框
 *
 * @author lufan  2015年6月9日下午8:30:46
 */
@SuppressLint("InflateParams")
public class PopWindowShowOneButton extends PopupWindow implements OnClickListener {
    public Button mSureButton;// 确定按钮
    public TextView mTextView;// 显示文字
    private BaseActivity mActivity;

    // 实例化builder,并加载布局
    public PopWindowShowOneButton(Context context) {
        mActivity = (BaseActivity) context;
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_alertdialog_onebutton, null);
        mActivity.set_$_View(mView);
        setView(mView);
    }

    // 获取Button和TextView的实例,并为Button设置点击事件
    private void setView(View mView) {
        mSureButton = mActivity.$_View(R.id.onebtn_dialog_btn);
        mTextView = mActivity.$_View(R.id.onebtn_dialog_tv);
        setBackGround(mView);
    }

    public void setBackGround(View mView) {
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true); // 设置PopupWindow可获得焦点
        this.setTouchable(true); // 设置PopupWindow可触摸
        this.setOutsideTouchable(false); // 设置非PopupWindow区域可触摸
        this.setContentView(mView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOnDismissListener(onDismissListener);
    }


    public void setOnClickListener(View.OnClickListener listener) {
        mSureButton.setOnClickListener(listener == null ? PopWindowShowOneButton.this : listener);
    }

    public void setTvTextSize(int size) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    // 让builder消失
    public void dismissPop() {
        dismiss();
        MethodConfig.hideActivityAlpha(mActivity);
    }

    public void show(String message, View mView) {
        mTextView.setText(message);
        this.showAtLocation(mView, Gravity.CENTER, 0, 0);
        MethodConfig.showActivityAlpha(mActivity);
    }

    OnDismissListener onDismissListener = new OnDismissListener() {

        @Override
        public void onDismiss() {
            MethodConfig.hideActivityAlpha(mActivity);
        }
    };


    public void setCancelTouchOutSide(boolean iscacncel) {
        this.setOutsideTouchable(iscacncel);
    }

    public void setCancelable(boolean iscancel) {
        this.setCancelable(iscancel);
    }

    @Override
    public void onClick(View v) {
        dismissPop();
    }
}
