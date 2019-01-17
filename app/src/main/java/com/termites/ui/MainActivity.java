package com.termites.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.ActivityManagerDone;
import com.termites.tools.adapter.FragmentViewPagerTabAdapter;
import com.termites.ui.base.BaseActivity;
import com.termites.ui.base.BaseFragment;
import com.termites.ui.fragment.HomePageFragment;
import com.termites.ui.fragment.MoreFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment的容器
 * Created by LF on 16/10/20.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private List<BaseFragment> mFragments;
    private RelativeLayout home_page_rl;
    private TextView btv_home_page_text;
    private RelativeLayout more_rl;
    private TextView btv_more_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        initView();
    }

    private void initView() {
        home_page_rl = $_Act(R.id.home_page_rl);
        home_page_rl.setOnClickListener(this);
        btv_home_page_text = $_Act(R.id.btv_home_page_text);

        more_rl = $_Act(R.id.more_rl);
        more_rl.setOnClickListener(this);
        btv_more_text = $_Act(R.id.btv_more_text);

        mViewPager = $_Act(R.id.main_tab_viewpager);

        mFragments = new ArrayList<>();

        // 加载首页
        mFragments.add(0, new HomePageFragment());
        // 加载更多
        mFragments.add(1, new MoreFragment());

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int pageIndex) {
                onSwitch(pageIndex);
            }
        });

        FragmentViewPagerTabAdapter mAdapter = new FragmentViewPagerTabAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);

        // 默认选中第一个
        onSwitch(0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_page_rl:
                onSwitch(0);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.more_rl:
                onSwitch(1);
                mViewPager.setCurrentItem(1, false);
                break;
        }
    }

    /**
     * 底部导航按钮点击效果切换
     * 从0开始
     */
    private void onSwitch(int isSwitch) {
        home_page_rl.setActivated(false);
        more_rl.setActivated(false);
        btv_home_page_text.setTextColor(getResources().getColor(R.color.text_gray));
        btv_more_text.setTextColor(getResources().getColor(R.color.text_gray));
        if (isSwitch == 0) {
            btv_home_page_text.setTextColor(getResources().getColor(R.color.text_green));
            home_page_rl.setActivated(true);
            setAnimator(home_page_rl);
        } else if (isSwitch == 1) {
            btv_more_text.setTextColor(getResources().getColor(R.color.text_green));
            more_rl.setActivated(true);
            setAnimator(more_rl);
        }
    }

    private void setAnimator(View mView) {
        mView.clearAnimation();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mView, "scaleX", 1f, 0.4f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mView, "scaleY", 1f, 0.4f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animatorX).with(animatorY);
        animSet.setDuration(240);
        animSet.start();
    }


    private long exitTime;

    // 主界面点击两次返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {// 是实体返回键并且已经点击
            if ((System.currentTimeMillis() - exitTime) > 2000) {// 间隔时间
                // 自定义Toast
                toast("再按一次退出程序", 0);
                exitTime = System.currentTimeMillis();
            } else {
                ActivityManagerDone.finishAllActivities();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
