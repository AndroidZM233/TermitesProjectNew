package com.termites.tools;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.termites.R;

/**
 * Created by LF on 16/10/21.
 */

public class MyProgressDialog {
    private Dialog dialog;
    private ImageView imageView;
    private TextView textView;
    private Animation animation;

    public MyProgressDialog(Context context) {
        dialog = new Dialog(context, R.style.loading_dialog);
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
        imageView = (ImageView) mView.findViewById(R.id.img);
        textView = (TextView) mView.findViewById(R.id.tipTextView);
        animation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        dialog.setContentView(mView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public void show() {
        dialog.show();
        if (imageView.getAnimation() == null) {
            imageView.startAnimation(animation);
        }
    }

    public void show(String txt) {
        if (!TextUtils.isEmpty(txt)) {
            textView.setText(txt);
        }
        show();
    }

    public void dismiss() {
        imageView.clearAnimation();
        dialog.dismiss();
    }
}
