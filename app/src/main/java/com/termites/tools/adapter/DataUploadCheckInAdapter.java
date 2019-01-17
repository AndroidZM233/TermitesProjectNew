package com.termites.tools.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.javabean.EquipmentBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LF on 16/10/21.
 */

public class DataUploadCheckInAdapter extends CommonAdapter<EquipmentBean> {

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> mSelectedMap = new HashMap<>();
    // 用来控制CheckBox是否显示
    private boolean IsOpenEditModal = false;

    private CheckBox dataupload_lv_check_cb;
    private TextView dataupload_lv_longitude;
    private TextView dataupload_lv_latitude;

    public HashMap<Integer, Boolean> getSelected() {
        return mSelectedMap;
    }

    // 初始化isSelected的数据
    public void initSelectedDate(List<EquipmentBean> datas) {
        for (int i = 0; i < datas.size(); i++) {
            getSelected().put(i, false);
        }
    }

    public DataUploadCheckInAdapter(Context context) {
        super(context, R.layout.layout_dataupload_lv_item);
    }

    public void setOpenEditModal(boolean isOpen) {
        this.IsOpenEditModal = isOpen;
    }

    public boolean getOpenEditModal() {
        return IsOpenEditModal;
    }

    @Override
    public void addAll(List<EquipmentBean> datas) {
        super.addAll(datas);
        initSelectedDate(datas);
    }

    @Override
    public void convert(CommonViewHolder vh, EquipmentBean dataUploadLvBean, int position) {
        dataupload_lv_longitude = vh.getView(R.id.dataupload_lv_longitude);
        dataupload_lv_latitude = vh.getView(R.id.dataupload_lv_latitude);

        vh.setText(R.id.dataupload_lv_id, "监测点编号: " + dataUploadLvBean.getEquipmentId())
                .setText(R.id.dataupload_lv_idorstate, "项目编号: " + dataUploadLvBean.getEquipmentProjectId())
                .setText(R.id.dataupload_lv_time, "登记时间: " + dataUploadLvBean.getEquipmentCheckTime().replace("%20", " "))
                .setVisibility_TextView(dataupload_lv_longitude, View.VISIBLE)
                .setText(dataupload_lv_longitude, "经       度: " + Double.parseDouble(dataUploadLvBean.getEquipmentLongitude()))
                .setVisibility_TextView(dataupload_lv_latitude, View.VISIBLE)
                .setText(dataupload_lv_latitude, "纬       度: " + Double.parseDouble(dataUploadLvBean.getEquipmentLatitude()));

        dataupload_lv_check_cb = vh.getView(R.id.dataupload_lv_check_cb);
        if (IsOpenEditModal) {
            vh.setVisibility_CheckBox(dataupload_lv_check_cb, View.VISIBLE);
        } else {
            vh.setVisibility_CheckBox(dataupload_lv_check_cb, View.GONE);
        }
        vh.setChecked(dataupload_lv_check_cb, getSelected().get(position).booleanValue());

    }
}
