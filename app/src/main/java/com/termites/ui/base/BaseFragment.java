package com.termites.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.termites.interfaces.IBase;
import com.termites.tools.PopWindowShowOneButton;
import com.termites.tools.RepeatClick;
import com.termites.tools.ViewUtils;

/**
 * Created by LF on 16/10/20.
 */
public class BaseFragment extends Fragment implements IBase {

    private View mContentView = null;
    // 重复点击限制
    private RepeatClick rc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rc = new RepeatClick();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentView = null;
    }

    @Override
    public void toast(int resId) {
        BaseActivity base = (BaseActivity) getActivity();
        base.toast(resId);
    }

    @Override
    public void toast(String text) {
        BaseActivity base = (BaseActivity) getActivity();
        base.toast(text);
    }

    @Override
    public void toast(int resId, int resImgId) {
        BaseActivity base = (BaseActivity) getActivity();
        base.toast(resId, resImgId);
    }

    @Override
    public void toast(String text, int resImgId) {
        BaseActivity base = (BaseActivity) getActivity();
        base.toast(text, resImgId);
    }

    @Override
    public PopWindowShowOneButton onCreateErrorDialog() {
        return null;
    }

    @Override
    public void showErrorDialog(String message, View mView) {
        BaseActivity base = (BaseActivity) getActivity();
        base.showErrorDialog(message, mView);
    }

    @Override
    public void showErrorDialog(String message, View.OnClickListener listener, View mView) {
        BaseActivity base = (BaseActivity) getActivity();
        base.showErrorDialog(message, listener, mView);
    }

    @Override
    public void hideErrorDialog() {
        BaseActivity base = (BaseActivity) getActivity();
        base.hideErrorDialog();
    }

    @Override
    public void setRepeat(View v) {
        rc.setRepeatClick(v);
    }

    @Override
    public void showProgress(String message) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showProgress(message);
    }

    @Override
    public void showProgress(int message) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showProgress(message);
    }

    @Override
    public void hideProgress() {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.hideProgress();
    }

    /**
     * 简化findViewById
     */
    public <E extends View> E $_View(int resId) {
        return ViewUtils.findViewById(get_$_View(), resId);
    }

    public void set_$_View(View mView) {
        this.mContentView = mView;
    }

    public View get_$_View() {
        return this.mContentView;
    }

}
