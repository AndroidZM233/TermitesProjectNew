package com.termites.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.termites.R;
import com.termites.dao.UploadEquipmentData;
import com.termites.dao.UploadInspectionData;
import com.termites.tools.PopWindowShowTwoButton;
import com.termites.tools.NoScrollViewPager;
import com.termites.tools.adapter.DataUploadCheckInAdapter;
import com.termites.tools.adapter.DataUploadInspectionAdapter;
import com.termites.tools.adapter.DataUploadViewPagerAdapter;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.database.DataHelper;
import com.termites.tools.javabean.EquipmentBean;
import com.termites.tools.javabean.InspectionBean;
import com.termites.tools.javabean.NetConnectionBean;
import com.termites.tools.swipemenulistview.SwipeMenu;
import com.termites.tools.swipemenulistview.SwipeMenuCreator;
import com.termites.tools.swipemenulistview.SwipeMenuItem;
import com.termites.tools.swipemenulistview.SwipeMenuListView;
import com.termites.ui.base.BaseWithTitleBackActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LF on 16/10/20.
 */

public class DataUploadActivity extends BaseWithTitleBackActivity implements View.OnClickListener {
    // Tab根布局
    private RelativeLayout data_upload_rl;
    // 登记数据
    private TextView data_upload_tab_checkdata;
    // 巡检数据
    private TextView data_upload_tab_inspectiondata;
    // Tab下面的线
    private View data_upload_tab_line;
    // 滑动控件
    private NoScrollViewPager data_upload_vp;
    private DataUploadViewPagerAdapter viewPagerAdapter;
    // 屏幕的宽度
    private int mScreenWidth;
    // 最下面按钮的布局
    private LinearLayout data_upload_bottom_rl;
    //  上传按钮
    private RelativeLayout data_upload_submit_rl;
    // 查看地图按钮
    private RelativeLayout data_upload_checkmap_rl;
    /**
     * 登记数据上传控件
     */
    private SwipeMenuListView mCheckInLv;
    private DataUploadCheckInAdapter mCheckInAdapter;
    private int mCheckInLv_checkNum = 0;
    /**
     * 巡检数据上传
     */
    private SwipeMenuListView mInspectionLv;
    private DataUploadInspectionAdapter mInspectionAdapter;
    private int mInspectionLv_checkNum = 0;
    // 数据库工具类
    private DataHelper dataHelper;
    // 删除数据的弹框
    private PopWindowShowTwoButton alertDialogShowTwoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_data_upload);

        dataHelper = new DataHelper(this);
        mScreenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        initView();
    }

    private void initView() {
        setTitleTxt("数据上传");
        setTitleRightTwoTextVisility(View.VISIBLE);
        setTitleRightTextVisility(View.VISIBLE);
        setTitleLeftTextVisility(View.VISIBLE);
        // Tab根布局
        data_upload_rl = $_Act(R.id.data_upload_rl);
        // 登记数据
        data_upload_tab_checkdata = $_Act(R.id.data_upload_tab_checkdata);
        data_upload_tab_checkdata.setOnClickListener(this);
        // 巡检数据
        data_upload_tab_inspectiondata = $_Act(R.id.data_upload_tab_inspectiondata);
        data_upload_tab_inspectiondata.setOnClickListener(this);
        // Tab下面的线
        data_upload_tab_line = $_Act(R.id.data_upload_tab_line);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mScreenWidth / 2 - 2, 6);
        params.addRule(RelativeLayout.BELOW, R.id.data_upload_tab_ll);
        data_upload_tab_line.setLayoutParams(params);

        // 最下面按钮的布局
        data_upload_bottom_rl = $_Act(R.id.data_upload_bottom_rl);
        //  上传按钮
        data_upload_submit_rl = $_Act(R.id.data_upload_submit_rl);
        data_upload_submit_rl.setOnClickListener(this);
        // 查询地图按钮
        data_upload_checkmap_rl = $_Act(R.id.data_upload_checkmap_rl);
        data_upload_checkmap_rl.setOnClickListener(this);

        // 滑动控件
        data_upload_vp = $_Act(R.id.data_upload_vp);
        List<View> mViews = new ArrayList<>();
        mViews.add(0, initCheckInDataView());
        mViews.add(1, initInspectionDataView());
        viewPagerAdapter = new DataUploadViewPagerAdapter(mViews);
        data_upload_vp.setAdapter(viewPagerAdapter);
        data_upload_vp.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTab(position);
            }
        });
        setTab(0);
    }

    public void setTab(int index) {
        if (index == 0) {
            data_upload_tab_checkdata.setTextColor(getResources().getColor(R.color.text_green));
            data_upload_tab_inspectiondata.setTextColor(getResources().getColor(R.color.text_gray));
            getCheckInData();
        } else if (index == 1) {
            data_upload_tab_checkdata.setTextColor(getResources().getColor(R.color.text_gray));
            data_upload_tab_inspectiondata.setTextColor(getResources().getColor(R.color.text_green));
            getInspectionData();
        }
        startLineAnimation(index);
    }

    /**
     * TAB下面的线的平移动画
     */
    public void startLineAnimation(int index) {
        data_upload_tab_line.clearAnimation();
        if (index == 0) {
            data_upload_tab_line.animate()
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(100)
                    .x(0);
        } else if (index == 1) {
            data_upload_tab_line.animate()
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(100)
                    .x(mScreenWidth / 2 - 2);
        }
    }

    /**
     * 整个TAB的平移动画
     */
    public void startTabAnimation(boolean isHide) {
        data_upload_rl.clearAnimation();
        if (isHide) {
            data_upload_rl.animate()
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(200)
                    .y(-data_upload_rl.getHeight())
                    .alpha(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            data_upload_rl.setVisibility(View.GONE);
                        }
                    });
        } else {
            data_upload_rl.setVisibility(View.VISIBLE);
            data_upload_rl.animate()
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(200)
                    .y(0)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            data_upload_rl.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    // 初始化登记数据页面的控件
    public View initCheckInDataView() {
        View mView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dataupload_checkdata_view, null);
        set_$_View(mView);
        mCheckInLv = $_View(R.id.dataupload_checkdata_lv);
        mCheckInLv.setEmptyView(MethodConfig.getEmptyViewWithText(getActivity(), "暂无未上传的登记数据", R.drawable.emptydata_checkin_icon));
        mCheckInLv.setOnItemClickListener(onItemClickListener_CheckIn);
        mCheckInAdapter = new DataUploadCheckInAdapter(getActivity());
        mCheckInLv.setAdapter(mCheckInAdapter);
        mCheckInAdapter.setOpenEditModal(false);
        mCheckInLv.isOpenSlide(false);
        mCheckInLv.setMenuCreator(mCreator_CheckIn);
        mCheckInLv.setOnMenuItemClickListener(mOnMenuItemListener_CheckIn);
        return mView;
    }

    // 初始化巡检数据页面的控件
    public View initInspectionDataView() {
        View mView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dataupload_inspectiondata_view, null);
        set_$_View(mView);
        mInspectionLv = $_View(R.id.dataupload_inspectiondata_lv);
        mInspectionLv.setEmptyView(MethodConfig.getEmptyViewWithText(getActivity(), "暂无未上传的巡检数据", R.drawable.emptydata_insepction_icon));
        mInspectionLv.setOnItemClickListener(onItemClickListener_Inspection);
        mInspectionAdapter = new DataUploadInspectionAdapter(getActivity());
        mInspectionLv.setAdapter(mInspectionAdapter);
        mInspectionAdapter.setOpenEditModal(false);
        mInspectionLv.isOpenSlide(false);
        mInspectionLv.setMenuCreator(mCreator_Inspection);
        mInspectionLv.setOnMenuItemClickListener(mOnMenuItemListener_Inspection);
        return mView;
    }

    // 获取登记数据
    public void getCheckInData() {
        // 查询本地数据
        List<EquipmentBean> equipmentBeanList = dataHelper.getEquipmentAllData(false);
        if (equipmentBeanList.size() > 0) {
            mCheckInAdapter.clear();
            mCheckInAdapter.addAll(equipmentBeanList);
            setTitleRightTxt("编辑");
        } else {
            mCheckInAdapter.clear();
            setTitleRightTxt("");
        }
        mCheckInLv_checkNum = 0;
    }

    // 获取巡检数据
    public void getInspectionData() {
        // 查询本地数据
        List<InspectionBean> inspectionBeanList = dataHelper.getInspectionAllDataMaxTime();
        if (inspectionBeanList.size() > 0) {
            mInspectionAdapter.clear();
            mInspectionAdapter.addAll(inspectionBeanList);
            setTitleRightTxt("编辑");
        } else {
            mInspectionAdapter.clear();
            setTitleRightTxt("");
        }
        mInspectionLv_checkNum = 0;
    }

    @Override
    public void onClickTitleRightTxt(TextView mTextView) {
        super.onClickTitleRightTxt(mTextView);
        if (data_upload_vp.getCurrentItem() == 0) {
            // 处于编辑模式
            if (mCheckInAdapter.getOpenEditModal()) {
                if (mTextView.getText().toString().equals("全选")) {
                    for (int i = 0; i < mCheckInAdapter.getCount(); i++) {
                        mCheckInAdapter.getSelected().put(i, true);
                    }
                    mCheckInLv_checkNum = mCheckInAdapter.getCount();
                    setTitleRightTxt("取消全选");
                } else if (mTextView.getText().toString().equals("取消全选")) {
                    for (int i = 0; i < mCheckInAdapter.getCount(); i++) {
                        mCheckInAdapter.getSelected().put(i, false);
                    }
                    mCheckInLv_checkNum = 0;
                    setTitleRightTxt("全选");
                }
                setTitleLeftText("已选" + mCheckInLv_checkNum);
                mCheckInAdapter.notifyDataSetChanged();
            }
            // 不处于编辑模式
            else {
                if (mCheckInAdapter.getCount() == 0) {
                    return;
                }
                mCheckInAdapter.setOpenEditModal(true);
                mCheckInLv.isOpenSlide(true);
                for (int i = 0; i < mCheckInAdapter.getSelected().size(); i++) {
                    mCheckInAdapter.getSelected().put(i, false);
                }
                mCheckInLv_checkNum = 0;
                setTitleLeftText("已选0");
                setTitleBackIcon(R.drawable.left_close);
                setTitleRightTwoTextVisility(View.VISIBLE);
                setTitleRightTwoTxt("删除");
                setTitleRightTxt("全选");
                startTabAnimation(true);
                data_upload_bottom_rl.setVisibility(View.VISIBLE);
                data_upload_tab_checkdata.setEnabled(false);
                data_upload_tab_inspectiondata.setEnabled(false);
                data_upload_vp.setNoScroll(true);
            }
            mCheckInAdapter.notifyDataSetChanged();
        } else if (data_upload_vp.getCurrentItem() == 1) {
            // 处于编辑模式
            if (mInspectionAdapter.getOpenEditModal()) {
                if (mTextView.getText().toString().equals("全选")) {
                    for (int i = 0; i < mInspectionAdapter.getCount(); i++) {
                        mInspectionAdapter.getSelected().put(i, true);
                    }
                    mInspectionLv_checkNum = mInspectionAdapter.getCount();
                    setTitleRightTxt("取消全选");
                } else if (mTextView.getText().toString().equals("取消全选")) {
                    for (int i = 0; i < mInspectionAdapter.getCount(); i++) {
                        mInspectionAdapter.getSelected().put(i, false);
                    }
                    mInspectionLv_checkNum = 0;
                    setTitleRightTxt("全选");
                }
                setTitleLeftText("已选" + mInspectionLv_checkNum);
                mInspectionAdapter.notifyDataSetChanged();
            }
            // 不处于编辑模式
            else {
                if (mInspectionAdapter.getCount() == 0) {
                    return;
                }
                mInspectionAdapter.setOpenEditModal(true);
                mInspectionLv.isOpenSlide(true);
                for (int i = 0; i < mInspectionAdapter.getSelected().size(); i++) {
                    mInspectionAdapter.getSelected().put(i, false);
                }
                mInspectionLv_checkNum = 0;
                setTitleLeftText("已选0");
                setTitleBackIcon(R.drawable.left_close);
                setTitleRightTwoTextVisility(View.VISIBLE);
                setTitleRightTwoTxt("删除");
                setTitleRightTxt("全选");
                startTabAnimation(true);
                data_upload_bottom_rl.setVisibility(View.VISIBLE);
                data_upload_tab_checkdata.setEnabled(false);
                data_upload_tab_inspectiondata.setEnabled(false);
                data_upload_vp.setNoScroll(true);
            }
            mInspectionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickTitleBack(LinearLayout mLinearLayout) {
        if (data_upload_vp.getCurrentItem() == 0) {
            // 处于编辑模式
            if (mCheckInAdapter.getOpenEditModal()) {
                mCheckInAdapter.setOpenEditModal(false);
                mCheckInLv.isOpenSlide(false);
                setTitleLeftText("");
                setTitleBackIcon(R.drawable.left_triangle_white);
                setTitleRightTwoTextVisility(View.GONE);
                setTitleRightTwoTxt("");
                startTabAnimation(false);
                data_upload_bottom_rl.setVisibility(View.GONE);
                data_upload_tab_checkdata.setEnabled(true);
                data_upload_tab_inspectiondata.setEnabled(true);
                data_upload_vp.setNoScroll(false);
                for (int i = 0; i < mCheckInAdapter.getSelected().size(); i++) {
                    mCheckInAdapter.getSelected().put(i, false);
                }
                setTitleRightTxt(mCheckInAdapter.getCount() > 0 ? "编辑" : "");
                mCheckInLv_checkNum = 0;
            } else {
                super.onClickTitleBack(mLinearLayout);
            }
            mCheckInAdapter.notifyDataSetChanged();
        } else if (data_upload_vp.getCurrentItem() == 1) {
            // 处于编辑模式
            if (mInspectionAdapter.getOpenEditModal()) {
                mInspectionAdapter.setOpenEditModal(false);
                mInspectionLv.isOpenSlide(false);
                setTitleLeftText("");
                setTitleRightTwoTextVisility(View.GONE);
                setTitleRightTwoTxt("");
                setTitleBackIcon(R.drawable.left_triangle_white);
                startTabAnimation(false);
                data_upload_bottom_rl.setVisibility(View.GONE);
                data_upload_tab_checkdata.setEnabled(true);
                data_upload_tab_inspectiondata.setEnabled(true);
                data_upload_vp.setNoScroll(false);
                for (int i = 0; i < mInspectionAdapter.getSelected().size(); i++) {
                    mInspectionAdapter.getSelected().put(i, false);
                }
                mInspectionLv_checkNum = 0;
                setTitleRightTxt(mCheckInAdapter.getCount() > 0 ? "编辑" : "");
            } else {
                super.onClickTitleBack(mLinearLayout);
            }
            mInspectionAdapter.notifyDataSetChanged();
        }
    }

    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.forgethandpwd_sure:
                    alertDialogShowTwoButton.dismissPop();
                    if (data_upload_vp.getCurrentItem() == 0) {
                        if (mCheckInAdapter.getCount() == 0) {
                            return;
                        }
                        for (int i = 0; i < mCheckInAdapter.getSelected().size(); i++) {
                            if (mCheckInAdapter.getSelected().get(i).booleanValue()) {
                                EquipmentBean bean = mCheckInAdapter.getItem(i);
                                dataHelper.deleteEquipmentOneData(bean);
                            }
                        }
                        showProgress("删除数据中,请稍后...");
                        mCheckInLv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onClickTitleBack(getTitleBackView());
                                hideProgress();
                                getCheckInData();
                            }
                        }, 1000);
                    } else {
                        if (mInspectionAdapter.getCount() == 0) {
                            return;
                        }
                        for (int i = 0; i < mInspectionAdapter.getSelected().size(); i++) {
                            if (mInspectionAdapter.getSelected().get(i).booleanValue()) {
                                dataHelper.deleteInspectionOneData(mInspectionAdapter.getItem(i).getInspecId());
                            }
                        }
                        showProgress("删除数据中,请稍后...");
                        mInspectionLv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onClickTitleBack(getTitleBackView());
                                hideProgress();
                                getInspectionData();
                            }
                        }, 1000);
                    }
                    break;
                case R.id.forgethandpwd_cancel:
                    alertDialogShowTwoButton.dismissPop();
                    break;
            }
        }
    };

    @Override
    public void onClickTitleRightTwoTxt(TextView mTextView) {
        super.onClickTitleRightTwoTxt(mTextView);
        int CheckSelectedLen = 0;
        int InspectionSelectedLen = 0;
        if (data_upload_vp.getCurrentItem() == 0) {
            if (mCheckInAdapter.getCount() == 0) {
                return;
            }
            for (int i = 0; i < mCheckInAdapter.getSelected().size(); i++) {
                if (mCheckInAdapter.getSelected().get(i).booleanValue()) {
                    CheckSelectedLen++;
                }
            }
        } else {
            if (mInspectionAdapter.getCount() == 0) {
                return;
            }
            for (int i = 0; i < mInspectionAdapter.getSelected().size(); i++) {
                if (mInspectionAdapter.getSelected().get(i).booleanValue()) {
                    InspectionSelectedLen++;
                }
            }
        }
        if (CheckSelectedLen > 0 || InspectionSelectedLen > 0) {
            if (alertDialogShowTwoButton == null) {
                alertDialogShowTwoButton = new PopWindowShowTwoButton(getActivity());
                alertDialogShowTwoButton.setBtnCancelText("取消");
                alertDialogShowTwoButton.setBtnSureText("确定");
            }
            alertDialogShowTwoButton.setOnClickListener(deleteClickListener);
            alertDialogShowTwoButton.show("确定删除该数据吗?", mTextView, Gravity.CENTER);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.data_upload_tab_checkdata:
                setTab(0);
                data_upload_vp.setCurrentItem(0, false);
                break;
            case R.id.data_upload_tab_inspectiondata:
                setTab(1);
                data_upload_vp.setCurrentItem(1, false);
                break;
            case R.id.data_upload_submit_rl:
                setRepeat(data_upload_submit_rl);
                if (data_upload_vp.getCurrentItem() == 0) {
                    if (mCheckInAdapter.getCount() == 0) {
                        return;
                    }
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < mCheckInAdapter.getSelected().size(); i++) {
                        if (mCheckInAdapter.getSelected().get(i).booleanValue()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                //
                                jsonObject.put(LocalcacherConfig.KEY_Custom, LocalcacherConfig.getCustomId());
                                EquipmentBean bean = mCheckInAdapter.getItem(i);
                                jsonObject.put(LocalcacherConfig.KEY_Device, bean.getEquipmentId());
                                jsonObject.put(LocalcacherConfig.KEY_Project, bean.getEquipmentProjectId());
                                jsonObject.put(LocalcacherConfig.KEY_Longitude, bean.getEquipmentLongitude());
                                jsonObject.put(LocalcacherConfig.KEY_Latitude, bean.getEquipmentLatitude());
                                jsonObject.put(LocalcacherConfig.KEY_EquipmentTime, bean.getEquipmentCheckTime());
                                jsonObject.put(LocalcacherConfig.KEY_Location, bean.getEquipmentLocation());
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (jsonArray.length() == 0) {
                        toast("请选择上传的数据");
                        return;
                    }
                    uploadCheckInData(jsonArray);
                } else if (data_upload_vp.getCurrentItem() == 1) {
                    if (mInspectionAdapter.getCount() == 0) {
                        return;
                    }
                    boolean isAllClear;

                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < mInspectionAdapter.getSelected().size(); i++) {
                        if (mInspectionAdapter.getSelected().get(i).booleanValue()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                InspectionBean bean = mInspectionAdapter.getItem(i);

                                jsonObject.put(LocalcacherConfig.KEY_Device,bean.getInspecId());
                                jsonObject.put(LocalcacherConfig.KEY_Status,bean.getInspectionTermiteState().equals("有") ? "1" : "0");
                                jsonObject.put(LocalcacherConfig.KEY_InspectionTime,bean.getInspectionTime());
                                jsonObject.put(LocalcacherConfig.KEY_InspectionUser,LocalcacherConfig.getUserName() + "");
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (jsonArray.length() == mInspectionAdapter.getCount()) {
                        isAllClear = true;
                    } else {
                        isAllClear = false;
                    }
                    if (jsonArray.length() == 0) {
                        toast("请选择上传的数据");
                        return;
                    }
                    uploadInspectionData(isAllClear, jsonArray);
                }
                break;
            case R.id.data_upload_checkmap_rl:
                setRepeat(data_upload_checkmap_rl);
                // 在地图上面显示当前的经度和纬度
                if (data_upload_vp.getCurrentItem() == 0) {
                    if (mCheckInAdapter.getCount() == 0) {
                        return;
                    }
                    int len = 0;
                    String longitude = "";
                    String latitude = "";
                    String id = "";
                    for (int i = 0; i < mCheckInAdapter.getSelected().size(); i++) {
                        if (mCheckInAdapter.getSelected().get(i).booleanValue()) {
                            len++;
                            EquipmentBean bean = mCheckInAdapter.getItem(i);
                            longitude += bean.getEquipmentLongitude() + ",";
                            latitude += bean.getEquipmentLatitude() + ",";
                            id += bean.getEquipmentId() + ",";
                        }
                    }
                    if (len == 1) {
                        longitude = longitude.replace(",", "");
                        latitude = latitude.replace(",", "");
                        id = id.replace(",", "");
                    }
                    if (len == 0) {
                        toast("请选择需要查看地图的数据");
                        return;
                    }
                    Intent intent = new Intent(getActivity(), MapViewActivity.class);
                    intent.putExtra(LocalcacherConfig.KEY_Longitude, longitude);
                    intent.putExtra(LocalcacherConfig.KEY_Latitude, latitude);
                    intent.putExtra(LocalcacherConfig.KEY_EquipmentId, id);
                    intent.putExtra(LocalcacherConfig.KEY_IsDataUpload, true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.out_from_top, R.anim.out_from);
                    onClickTitleBack(getTitleBackView());
                }
                break;
            case R.id.forgethandpwd_sure:
                alertDialogShowTwoButton.dismissPop();
                deleteData(alertDialogShowTwoButton.getPageIndex());
                break;
            case R.id.forgethandpwd_cancel:
                alertDialogShowTwoButton.dismissPop();
                break;
        }
    }

    SwipeMenuCreator mCreator_CheckIn = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // 创建一个SwipeMenuItem
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
            // 设置Item的背景
            deleteItem.setBackground(new ColorDrawable(Color.rgb(55, 185, 71)));
            // 设置背景的宽度
            deleteItem.setWidth(dp2px(60));
            // 设置Icon
            deleteItem.setIcon(R.drawable.data_upload_delete);
            // 添加item
            menu.addMenuItem(deleteItem);
        }
    };

    // 2.添加OnMenuItemClickListener事件
    SwipeMenuListView.OnMenuItemClickListener mOnMenuItemListener_CheckIn = new SwipeMenuListView.OnMenuItemClickListener() {

        @Override
        public void onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    // 弹框提示
                    if (alertDialogShowTwoButton == null) {
                        alertDialogShowTwoButton = new PopWindowShowTwoButton(getActivity());
                        alertDialogShowTwoButton.setBtnCancelText("取消");
                        alertDialogShowTwoButton.setBtnSureText("确定");
                    }
                    alertDialogShowTwoButton.setOnClickListener(DataUploadActivity.this);
                    alertDialogShowTwoButton.show("确定删除该数据吗?", position, mCheckInLv, Gravity.CENTER);
                    break;
            }
        }
    };


    SwipeMenuCreator mCreator_Inspection = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // 创建一个SwipeMenuItem
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
            // 设置Item的背景
            deleteItem.setBackground(new ColorDrawable(Color.rgb(55, 185, 71)));
            // 设置背景的宽度
            deleteItem.setWidth(dp2px(60));
            // 设置Icon
            deleteItem.setIcon(R.drawable.data_upload_delete);
            // 添加item
            menu.addMenuItem(deleteItem);
        }
    };


    // 2.添加OnMenuItemClickListener事件
    SwipeMenuListView.OnMenuItemClickListener mOnMenuItemListener_Inspection = new SwipeMenuListView.OnMenuItemClickListener() {

        @Override
        public void onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    // 弹框提示
                    if (alertDialogShowTwoButton == null) {
                        alertDialogShowTwoButton = new PopWindowShowTwoButton(getActivity());
                        alertDialogShowTwoButton.setBtnCancelText("取消");
                        alertDialogShowTwoButton.setBtnSureText("确定");
                    }
                    alertDialogShowTwoButton.setOnClickListener(DataUploadActivity.this);
                    alertDialogShowTwoButton.show("确定删除该数据吗?", position, mInspectionLv, Gravity.CENTER);
                    break;
            }
        }
    };

    AdapterView.OnItemClickListener onItemClickListener_CheckIn = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mCheckInAdapter.getOpenEditModal())
                setSelectItem(position);
        }
    };

    AdapterView.OnItemClickListener onItemClickListener_Inspection = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mInspectionAdapter.getOpenEditModal())
                setSelectItem(position);
        }
    };

    public void setSelectItem(int position) {
        if (data_upload_vp.getCurrentItem() == 0) {
            if (mCheckInAdapter.getSelected().get(position).booleanValue()) {
                mCheckInAdapter.getSelected().put(position, false);
                mCheckInLv_checkNum--;
            } else {
                mCheckInAdapter.getSelected().put(position, true);
                mCheckInLv_checkNum++;
            }
            if (mCheckInLv_checkNum == mCheckInAdapter.getCount()) {
                setTitleRightTxt("取消全选");
            } else {
                setTitleRightTxt("全选");
            }
            setTitleLeftText("已选" + mCheckInLv_checkNum);
            mCheckInAdapter.notifyDataSetChanged();
        } else {
            if (mInspectionAdapter.getSelected().get(position).booleanValue()) {
                mInspectionAdapter.getSelected().put(position, false);
                mInspectionLv_checkNum--;
            } else {
                mInspectionAdapter.getSelected().put(position, true);
                mInspectionLv_checkNum++;
            }
            if (mInspectionLv_checkNum == mInspectionAdapter.getCount()) {
                setTitleRightTxt("取消全选");
            } else {
                setTitleRightTxt("全选");
            }
            setTitleLeftText("已选" + mInspectionLv_checkNum);
            mInspectionAdapter.notifyDataSetChanged();
        }

    }

    // 装置登记数据上传
    public void uploadCheckInData(final JSONArray jsonArray) {
        showProgress("数据上传中,请稍后...");
        new UploadEquipmentData(getActivity(), jsonArray, new UploadEquipmentData.SuccessCallback() {

            @Override
            public void onSuccess(NetConnectionBean bean) {
                hideProgress();
                if (bean.isError()) {
                    toast(bean.getMessage());
                } else {
                    toast("登记数据上传成功", R.drawable.toast_icon_suc);
                    updateCheckInData(jsonArray);
                    mCheckInLv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCheckInData();
                            onClickTitleBack(getTitleBackView());
                        }
                    }, 1000);
                }
            }
        }, new UploadEquipmentData.FailCallback() {

            @Override
            public void onFail() {
                hideProgress();
                toast("登记数据上传失败");
            }
        });
    }

    // 删除装置登记数据
    public void deleteCheckInOneData(EquipmentBean bean) {
        showProgress("删除数据中,请稍后...");
        dataHelper.deleteEquipmentOneData(bean);
        mCheckInLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCheckInData();
                hideProgress();
                onClickTitleBack(getTitleBackView());
                toast("数据删除成功", R.drawable.toast_icon_suc);
            }
        }, 1000);
    }

    // 修改装置登记数据的状态
    public void updateCheckInData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                EquipmentBean bean = new EquipmentBean();
                bean.setEquipmentId(jsonObject.getString(LocalcacherConfig.KEY_Device));
                bean.setEquipmentCheckTime(jsonObject.getString(LocalcacherConfig.KEY_EquipmentTime));
                bean.setEquipmentUploadState("已上传");
                dataHelper.updateEpuipmentData(bean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //装置巡检数据上传
    public void uploadInspectionData(final boolean isAllClear, final JSONArray jsonArray) {
        showProgress("数据上传中,请稍后...");
        new UploadInspectionData(getActivity(), jsonArray, new UploadInspectionData.SuccessCallback() {

            @Override
            public void onSuccess(NetConnectionBean bean) {
                hideProgress();
                if (bean.isError()) {
                    toast(bean.getMessage());
                } else {
                    toast("巡检数据上传成功", R.drawable.toast_icon_suc);
                    // 上传成功,删除掉对应的数据并重新查询数据库
                    if (isAllClear) {
                        dataHelper.clearInspectionData();
                        mInspectionAdapter.clear();
                    } else {
                        deleteInspectionData(jsonArray);
                    }
                    mInspectionLv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAllClear) {
                                getInspectionData();
                            }
                            onClickTitleBack(getTitleBackView());
                        }
                    }, 1000);
                }
            }
        }, new UploadInspectionData.FailCallback() {

            @Override
            public void onFail() {
                hideProgress();
                toast("巡检数据上传失败");
            }
        });
    }

    // 删除多条巡检数据
    public void deleteInspectionData(JSONArray jsonArray) {
//        if (device.contains(",")) {
//            String[] device_s = device.split(",");
//            for (int i = 0; i < device_s.length; i++) {
//                dataHelper.deleteInspectionOneData(device_s[i]);
//            }
//        } else {
//            dataHelper.deleteInspectionOneData(device);
//        }
    }

    // 删除某条巡检数据
    public void deleteInspectionOneData(String InspecId) {
        showProgress("删除数据中,请稍后...");
        dataHelper.deleteInspectionOneData(InspecId);
        mInspectionLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                getInspectionData();
                hideProgress();
                onClickTitleBack(getTitleBackView());
                toast("数据删除成功", R.drawable.toast_icon_suc);
            }
        }, 1000);
    }


    public void deleteData(int pageIndex) {
        if (data_upload_vp.getCurrentItem() == 0) {
            deleteCheckInOneData(mCheckInAdapter.getItem(pageIndex));
        } else {
            deleteInspectionOneData(mInspectionAdapter.getItem(pageIndex).getInspecId());
        }
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            onClickTitleBack(getTitleBackView());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
