package com.termites.ui.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.termites.R;


/**
 * Created by LF on 16/10/20.
 */
public class BaseWithTitleBackActivity extends BaseActivity {

    private LinearLayout layout_title_ly;
    private LinearLayout layout_title_back;
    private TextView layout_titles_img;
    private TextView layout_title_lefttv;
    private TextView layout_title_tv;
    private TextView layout_title_righttv;
    private TextView layout_title_righttv_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initTitleWithBack();
    }

    public void initTitleWithBack() {

        layout_title_ly = $_Act(R.id.layout_title_ly);

        layout_title_back = $_Act(R.id.layout_title_back);
        layout_title_back.setOnClickListener(mListener);

        layout_titles_img = $_Act(R.id.layout_titles_img);

        layout_title_lefttv = $_Act(R.id.layout_title_lefttv);
        layout_title_lefttv.setOnClickListener(mListener);

        layout_title_tv = $_Act(R.id.layout_title_tv);

        layout_title_righttv = $_Act(R.id.layout_title_righttv);
        layout_title_righttv.setOnClickListener(mListener);

        layout_title_righttv_two = $_Act(R.id.layout_title_righttv_two);
        layout_title_righttv_two.setOnClickListener(mListener);

    }

    OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_title_back:
                    setRepeat(layout_title_back);
                    onClickTitleBack(layout_title_back);
                    break;
                case R.id.layout_title_lefttv:
                    setRepeat(layout_title_lefttv);
                    onClickTitleLeftTxt(layout_title_lefttv);
                    break;
                case R.id.layout_title_righttv_two:
                    setRepeat(layout_title_righttv_two);
                    onClickTitleRightTwoTxt(layout_title_righttv_two);
                    break;
                case R.id.layout_title_righttv:
                    setRepeat(layout_title_righttv);
                    onClickTitleRightTxt(layout_title_righttv);
                    break;
            }
        }
    };

    /**
     * 返回键的点击事件方法
     */
    public void onClickTitleBack(LinearLayout mLinearLayout) {
        finish();
    }

    /**
     * 左边文本的点击事件
     */
    public void onClickTitleLeftTxt(TextView mTextView) {
    }

    /**
     * 右边的文本点击事件方法
     */
    public void onClickTitleRightTxt(TextView mTextView) {
    }

    /**
     * 右边的文本点击事件方法
     */
    public void onClickTitleRightTwoTxt(TextView mTextView) {
    }


    /**
     * 设置根部局的背景颜色
     */
    public void setTitleRootLayoutBackgroundColor(String resBackgrondColor) {
        layout_title_ly.setBackgroundColor(Color.parseColor(resBackgrondColor));
    }

    /***
     * 设置根布局背景图片
     */
    public void setTitleRootLayoutBackgroundResource(int resBackgroundResource) {
        layout_title_ly.setBackgroundResource(resBackgroundResource);
    }

    /**
     * 设置返回键箭头的图片
     */
    public void setTitleBackIcon(int resId) {
        layout_titles_img.setBackgroundResource(resId);
    }

    /**
     * 是否显示返回键
     */
    public void setTitleBackTextVisility(int visility) {
        layout_title_back.setClickable(visility == View.VISIBLE ? true : false);
        layout_title_back.setVisibility(visility);
    }

    /**
     * 得到返回键对象
     */
    public LinearLayout getTitleBackView() {
        return layout_title_back;
    }

    /**
     * 返回键是否启用
     */
    public void setTitleBackEnable(boolean isEnable) {
        layout_title_back.setEnabled(isEnable);
    }

    /**
     * 是否显示左边的文字
     */
    public void setTitleLeftTextVisility(int visility) {
        layout_title_lefttv.setClickable(visility == View.VISIBLE ? true : false);
        layout_title_lefttv.setVisibility(visility);
    }

    /**
     * 左边文字是否可以点击
     */
    public void setTitleLeftTextEnabled(boolean isEnabled) {
        layout_title_lefttv.setEnabled(isEnabled);
    }


    /**
     * 设置左边的文字
     */
    public void setTitleLeftText(String Text) {
        layout_title_lefttv.setText(Text);
    }

    /**
     * 获取左边的文字
     */
    public String getTitleLeftText() {
        return layout_title_lefttv.getText().toString();
    }

    /**
     * 设置标题的文字
     */
    public void setTitleTxt(String Text) {
        layout_title_tv.setText(Text);
    }

    /**
     * 设置标题的颜色
     */
    public void setTitleTextColor(int resTextColor) {
        layout_title_tv.setTextColor(getResources().getColor(resTextColor));
    }

    /**
     * 设置标题的颜色
     */
    public void setTitleTextColor(String resTextColor) {
        layout_title_tv.setTextColor(Color.parseColor(resTextColor));
    }

    /**
     * 是否显示标题的文字
     */
    public void setTitleTextVisility(int visility) {
        layout_title_tv.setVisibility(visility);
    }

    /**
     * 设置右边的文字
     */
    public void setTitleRightTxt(String Text) {
        layout_title_righttv.setText(Text);
    }

    /**
     * 获取右边的文字
     */
    public String getTitleRightTxt() {
        return layout_title_righttv.getText().toString();
    }

    /**
     * 右边的文字是否启用
     */
    public void setTitleRightEnable(boolean isEnable) {
        layout_title_righttv.setEnabled(isEnable);
    }

    /**
     * 设置右边文字的颜色
     */
    public void setTitleRightTextColor(int resColorStateList) {
        layout_title_righttv.setTextColor(getResources().getColorStateList(resColorStateList));
    }

    /**
     * 设置右边的文字是否显示
     */
    public void setTitleRightTextVisility(int visility) {
        layout_title_righttv.setClickable(visility == View.VISIBLE ? true : false);
        layout_title_righttv.setVisibility(visility);
    }


    /**
     * 设置右边的第二个文字
     */
    public void setTitleRightTwoTxt(String Text) {
        layout_title_righttv_two.setText(Text);
    }

    /**
     * 获取右边的第二个文字
     */
    public String getTitleRightTwoTxt() {
        return layout_title_righttv_two.getText().toString();
    }

    /**
     * 右边的第二个文字是否启用
     */
    public void setTitleRightTwoEnable(boolean isEnable) {
        layout_title_righttv_two.setEnabled(isEnable);
    }

    /**
     * 设置右边第二个文字的颜色
     */
    public void setTitleRightTwoTextColor(int resColorStateList) {
        layout_title_righttv_two.setTextColor(getResources().getColorStateList(resColorStateList));
    }

    /**
     * 设置右边的第二个文字是否显示
     */
    public void setTitleRightTwoTextVisility(int visility) {
        layout_title_righttv_two.setClickable(visility == View.VISIBLE ? true : false);
        layout_title_righttv_two.setVisibility(visility);
    }

}
