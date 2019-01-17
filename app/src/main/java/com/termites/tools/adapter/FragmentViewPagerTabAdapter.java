package com.termites.tools.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.termites.ui.base.BaseFragment;

import java.util.List;


/**
 * MainActivity的ViewPager适配器
 *
 * @author lufan  2016年4月23日上午9:48:20
 */
public class FragmentViewPagerTabAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mFragments;

    public FragmentViewPagerTabAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public BaseFragment getItem(int arg0) {
        return mFragments.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
