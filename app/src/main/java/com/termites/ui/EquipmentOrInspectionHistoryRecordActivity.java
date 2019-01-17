package com.termites.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.termites.R;
import com.termites.tools.EmptyViewListView;
import com.termites.tools.adapter.EquipmentOrInspectionHistoryRecordAdapter;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.database.DataHelper;
import com.termites.tools.javabean.EquipmentBean;
import com.termites.tools.javabean.HistoryRecordBean;
import com.termites.tools.javabean.InspectionBean;
import com.termites.ui.base.BaseWithTitleBackActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LF on 16/10/21.
 */

public class EquipmentOrInspectionHistoryRecordActivity extends BaseWithTitleBackActivity {
    private EmptyViewListView history_record_lv;
    private EquipmentOrInspectionHistoryRecordAdapter historyRecordAdapter;
    private String mType;
    private DataHelper dataHelper;
    private int mCurrentClickItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_eq_in_history_record);

        dataHelper = new DataHelper(getActivity());

        mType = getIntent().getStringExtra(LocalcacherConfig.KEY_HistoryRecordType);
        history_record_lv = $_Act(R.id.history_record_lv);
        if (mType.equals("checkin")) {
            setTitleTxt("登记历史纪录");
            history_record_lv.setSelector(R.drawable.bg_white_selector);
            history_record_lv.setEmptyView(MethodConfig.getEmptyViewWithText(getActivity(), "暂无登记历史纪录", R.drawable.emptydata_checkin_icon));
        } else if (mType.equals("inspection")) {
            history_record_lv.setSelector(android.R.color.transparent);
            setTitleTxt("巡检历史纪录");
            history_record_lv.setEmptyView(MethodConfig.getEmptyViewWithText(getActivity(), "暂无巡检历史纪录", R.drawable.emptydata_insepction_icon));
        }
        history_record_lv.setOnItemClickListener(onItemClickListener);
        historyRecordAdapter = new EquipmentOrInspectionHistoryRecordAdapter(getActivity());
        historyRecordAdapter.setType(mType);
        history_record_lv.setAdapter(historyRecordAdapter);
        getData();
    }

    public void getData() {
        showProgress("获取数据中,请稍后...");
        if (mType.equals("checkin")) {
            // 查询本地数据
            List<EquipmentBean> equipmentBeanList = dataHelper.getEquipmentAllData(true);
            if (equipmentBeanList.size() > 0) {
                List<HistoryRecordBean> mDatas = new ArrayList<>();
                for (int i = 0; i < equipmentBeanList.size(); i++) {
                    HistoryRecordBean historyRecordBean = new HistoryRecordBean();
                    historyRecordBean.setEquipmentId(equipmentBeanList.get(i).getEquipmentId());
                    historyRecordBean.setEquipmentProjectId(equipmentBeanList.get(i).getEquipmentProjectId());
                    historyRecordBean.setEquipmentCheckTime(equipmentBeanList.get(i).getEquipmentCheckTime());
                    historyRecordBean.setUploadState(equipmentBeanList.get(i).getEquipmentUploadState());
                    historyRecordBean.setEquipmentLongitude(equipmentBeanList.get(i).getEquipmentLongitude());
                    historyRecordBean.setEquipmentLatitude(equipmentBeanList.get(i).getEquipmentLatitude());
                    mDatas.add(historyRecordBean);
                }
                historyRecordAdapter.clear();
                historyRecordAdapter.addAll(mDatas);
            }
        } else {
            // 查询本地数据
            List<InspectionBean> inspectionBeanList = dataHelper.getInspectionAllData();
            if (inspectionBeanList.size() > 0) {
                List<HistoryRecordBean> mDatas = new ArrayList<>();
                for (int i = 0; i < inspectionBeanList.size(); i++) {
                    HistoryRecordBean historyRecordBean = new HistoryRecordBean();
                    historyRecordBean.setInspecId(inspectionBeanList.get(i).getInspecId());
                    historyRecordBean.setInspectionTermiteState(inspectionBeanList.get(i).getInspectionTermiteState());
                    historyRecordBean.setInspectionTime(inspectionBeanList.get(i).getInspectionTime());
                    historyRecordBean.setUploadState(inspectionBeanList.get(i).getInspectionUploadState());
                    mDatas.add(historyRecordBean);
                }
                historyRecordAdapter.clear();
                historyRecordAdapter.addAll(mDatas);
            }
        }

        history_record_lv.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 1000);

    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mType.equals("checkin")) {
                mCurrentClickItemPosition = position;
                HistoryRecordBean historyRecordBean = historyRecordAdapter.getItem(position);
                // 把经纬度以及对应的装置点编号传给地图界面
                Intent intent = new Intent(getActivity(), MapViewActivity.class);
                intent.putExtra(LocalcacherConfig.KEY_Longitude, historyRecordBean.getEquipmentLongitude());
                intent.putExtra(LocalcacherConfig.KEY_Latitude, historyRecordBean.getEquipmentLatitude());
                intent.putExtra(LocalcacherConfig.KEY_EquipmentId, historyRecordBean.getEquipmentId());
                intent.putExtra(LocalcacherConfig.KEY_IsHistoryData, true);
                startActivityForResult(intent, 300);
                overridePendingTransition(R.anim.out_from_top, R.anim.out_from);

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            if (data != null) {
                double longitude = data.getDoubleExtra(LocalcacherConfig.KEY_Longitude, 0.0d);
                double latitude = data.getDoubleExtra(LocalcacherConfig.KEY_Latitude, 0.0d);
                if (longitude == 0.0d || latitude == 0.0d) {
                    return;
                }
                // 修改本地数据库对应装置编号的经纬度值,并重新查询数据库
                HistoryRecordBean historyRecordBean = historyRecordAdapter.getItem(mCurrentClickItemPosition);
                EquipmentBean equipmentBean = new EquipmentBean();
                equipmentBean.setEquipmentId(historyRecordBean.getEquipmentId());
                equipmentBean.setEquipmentLatitude(latitude + "");
                equipmentBean.setEquipmentLongitude(longitude + "");
                equipmentBean.setEquipmentProjectId(historyRecordBean.getEquipmentProjectId());
                equipmentBean.setEquipmentCheckTime(historyRecordBean.getEquipmentCheckTime());
                equipmentBean.setEquipmentUploadState(historyRecordBean.getUploadState());
                showProgress("数据修改中,请稍后...");
                dataHelper.updateEpuipmentData_LongitudeAndLatitude(equipmentBean);
                history_record_lv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                        hideProgress();
                    }
                }, 1000);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHelper.close();
    }
}
