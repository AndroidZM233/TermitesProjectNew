package com.termites.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.termites.R;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.rfid.AuxiliaryFunctionTools.ApplicationType;
import com.termites.tools.rfid.AuxiliaryFunctionTools;
import com.termites.tools.adapter.MorePageListViewAdapter;
import com.termites.tools.javabean.AuxiliaryFunctionBean;
import com.termites.ui.FlashLightActivity;
import com.termites.ui.LoginActivity;
import com.termites.ui.base.BaseWithTitleBackFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LF on 16/10/20.
 */

public class MoreFragment extends BaseWithTitleBackFragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private View mMoreView;
    private Button more_cancelLationaccount;
    private ListView mListView;
    private MorePageListViewAdapter mMorePageListViewAdapter;
    private boolean isOpenFlashLight = false;
    private int[] iconSources = {
            R.drawable.more_camera,
            R.drawable.more_calendar,
            R.drawable.more_calculator,
            R.drawable.more_flashlight,
            R.drawable.more_clock,
            R.drawable.more_photo
    };
    private String[] appNames = {
            "相机",
            "日历",
            "计算器",
            "手电筒",
            "闹钟",
            "相册"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mMoreView == null) {

            mMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_more_fg, null);
            initTitleWithBack(mMoreView);
            setTitleTxt("辅助功能");
            setTitleBackTextVisility(View.GONE);

            mListView = $_View(R.id.more_fg_lv);
            mListView.setOnItemClickListener(this);
            mMorePageListViewAdapter = new MorePageListViewAdapter(getActivity());
            mListView.setAdapter(mMorePageListViewAdapter);
            initListViewData();

            more_cancelLationaccount = $_View(R.id.more_cancelLationaccount);
            more_cancelLationaccount.setOnClickListener(this);
        }

        ViewGroup parent = (ViewGroup) mMoreView.getParent();
        if (parent != null) {
            parent.removeView(mMoreView);
        }

        return mMoreView;
    }

    public void initListViewData() {
        List<AuxiliaryFunctionBean> auxiliaryFunctionBeanList = new ArrayList<>();
        for (int i = 0; i < iconSources.length; i++) {
            AuxiliaryFunctionBean auxiliaryFunctionBean = new AuxiliaryFunctionBean();
            auxiliaryFunctionBean.setAppName(appNames[i]);
            auxiliaryFunctionBean.setIconSource(iconSources[i]);
            auxiliaryFunctionBean.setAppType(i + 1);
            auxiliaryFunctionBeanList.add(auxiliaryFunctionBean);
        }
        mMorePageListViewAdapter.clear();
        mMorePageListViewAdapter.addAll(auxiliaryFunctionBeanList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AuxiliaryFunctionBean auxiliaryFunctionBean = mMorePageListViewAdapter.getItem(position);
        switch (auxiliaryFunctionBean.getAppType()) {
            case ApplicationType.CAMERA:
                AuxiliaryFunctionTools.openCamera(getActivity());
                break;
            case ApplicationType.CALENDAR:
                AuxiliaryFunctionTools.openCalendar(getActivity());
                break;
            case ApplicationType.CALCULATOR:
                AuxiliaryFunctionTools.openCalculator(getActivity());
                break;
            case ApplicationType.FLASHLIGHT:
                // 老版的手持仪调用手电筒的方法,新版没有提供方法
//                if (LocalcacherConfig.isUseNewDeviceCode) {
//                    if (isOpenFlashLight == false) {
//                        MethodConfig.openNewFlash();
//                    } else {
//                        MethodConfig.closeNewFlash();
//                    }
//                    isOpenFlashLight = !isOpenFlashLight;
//                } else {
//                    AuxiliaryFunctionTools.flashLight(!isOpenFlashLight);
//                    isOpenFlashLight = !isOpenFlashLight;
//                }
                startActivity(new Intent(getActivity(), FlashLightActivity.class));
                break;
            case ApplicationType.CLOCK:
                AuxiliaryFunctionTools.openClock(getActivity());
                break;
            case ApplicationType.PHOTOALBUM:
                AuxiliaryFunctionTools.openPhotoAlbum(getActivity());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        showProgress("正在注销账户,请稍后...");
        more_cancelLationaccount.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        }, 1000);
    }
}
