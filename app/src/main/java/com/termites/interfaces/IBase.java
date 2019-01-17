package com.termites.interfaces;

import android.view.View;

import com.termites.tools.PopWindowShowOneButton;

public interface IBase {

    void toast(int resId);

    void toast(String text);

    void toast(int resId, int resImgId);

    void toast(String text, int resImgId);

    PopWindowShowOneButton onCreateErrorDialog();

    void showErrorDialog(String message, View mView);

    void showErrorDialog(String message, View.OnClickListener listener, View mView);

    void hideErrorDialog();

    void showProgress(String message);

    void showProgress(int message);

    void hideProgress();

    void setRepeat(View v);
}
