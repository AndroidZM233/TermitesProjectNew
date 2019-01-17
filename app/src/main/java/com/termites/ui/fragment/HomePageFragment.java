package com.termites.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.MyGridView;
import com.termites.tools.adapter.HomePageGridViewAdapter;
import com.termites.tools.javabean.HomePageGvBean;
import com.termites.ui.DataUploadActivity;
import com.termites.ui.EquipmentCheckInActivity;
import com.termites.ui.InspectionManagerActivity;
import com.termites.ui.LoginActivity;
import com.termites.ui.base.BaseWithTitleBackFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LF on 16/10/20.
 */

public class HomePageFragment extends BaseWithTitleBackFragment implements AdapterView.OnItemClickListener {
    private View mHomePageView;
    private MyGridView mMyGridView;
    private HomePageGridViewAdapter mHomePageGridViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mHomePageView == null) {
            mHomePageView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_homepage_fg, null);

            initTitleWithBack(mHomePageView);
            setTitleTxt("首页");
            setTitleBackTextVisility(View.GONE);

            mMyGridView = $_View(R.id.homepage_gv);
            mMyGridView.setOnItemClickListener(this);
            mHomePageGridViewAdapter = new HomePageGridViewAdapter(getActivity());
            mMyGridView.setAdapter(mHomePageGridViewAdapter);
            mHomePageGridViewAdapter.clear();
            mHomePageGridViewAdapter.addAll(getGvData());
        }
        ViewGroup parent = (ViewGroup) mHomePageView.getParent();
        if (parent != null) {
            parent.removeView(mHomePageView);
        }
        return mHomePageView;
    }

    public List<HomePageGvBean> getGvData() {
        List<HomePageGvBean> mDatas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HomePageGvBean mBean = new HomePageGvBean();
            if (i == 0) {
                mBean.setJumpType(LocalcacherConfig.JumpTypes.KEY_InspectionManager);
                mBean.setText("巡检管理");
                mBean.setIconSource(R.drawable.inspection_icon);
            } else if (i == 1) {
                mBean.setJumpType(LocalcacherConfig.JumpTypes.KEY_EquipmentCheckin);
                mBean.setText("装置登记");
                mBean.setIconSource(R.drawable.checkin_icon);
            } else if (i == 2) {
                mBean.setJumpType(LocalcacherConfig.JumpTypes.KEY_DataUpload);
                mBean.setText("数据上传");
                mBean.setIconSource(R.drawable.upload_icon);
            }
            mDatas.add(i, mBean);
        }
        return mDatas;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HomePageGvBean mBean = mHomePageGridViewAdapter.getItem((int) id);
        switch (mBean.getJumpType()) {
            case LocalcacherConfig.JumpTypes.KEY_InspectionManager:
                getActivity().startActivity(new Intent(getActivity(), InspectionManagerActivity.class));
                break;
            case LocalcacherConfig.JumpTypes.KEY_EquipmentCheckin:
                getActivity().startActivity(new Intent(getActivity(), EquipmentCheckInActivity.class));
                break;
            case LocalcacherConfig.JumpTypes.KEY_DataUpload:
                getActivity().startActivity(new Intent(getActivity(), DataUploadActivity.class));
                break;
        }

    }
}
