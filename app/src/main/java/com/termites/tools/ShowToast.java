package com.termites.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.termites.R;

/**
 * 自定义toast消息类
 * Created by LF on 16/10/20.
 */
@SuppressLint({"InflateParams", "RtlHardcoded"})
public class ShowToast {

    private static ShowToast sShowToast = null;

    public static synchronized void init(Context context) {
        if (sShowToast == null) {
            sShowToast = new ShowToast(context);
        }
    }

    public static synchronized void destroy() {
        if (sShowToast != null) {
            sShowToast = null;
        }
    }

    public static final synchronized ShowToast getInstance() {
        if (sShowToast == null) {
            throw new IllegalArgumentException("You must call 'ShowToast.init()' in onCreate of 'application'");
        }
        return sShowToast;
    }

    private Toast toast = null;
    private View v;
    private TextView text;
    private ImageView toast_icon;

    private ShowToast(Context context) {
        toast = new Toast(context);
        v = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        text = (TextView) v.findViewById(R.id.toast_text);
        toast_icon = (ImageView) v.findViewById(R.id.toast_icon);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    public void show(String msg, int resImgId) {
        if (resImgId == 0) {
            toast_icon.setVisibility(View.GONE);
        } else {
            toast_icon.setVisibility(View.VISIBLE);
            toast_icon.setImageResource(resImgId);
        }
        show(msg);
    }

    public void show(int resId, int resImgId) {
        if (resImgId == 0) {
            toast_icon.setVisibility(View.GONE);
        } else {
            toast_icon.setVisibility(View.VISIBLE);
            toast_icon.setImageResource(resImgId);
        }
        show(resId);
    }

    public void show(String msg) {
        text.setText(msg);
        toast.show();
    }

    public void show(int resId) {
        text.setText(resId);
        toast.show();
    }
}
