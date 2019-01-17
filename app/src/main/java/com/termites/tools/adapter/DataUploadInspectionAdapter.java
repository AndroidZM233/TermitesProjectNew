package com.termites.tools.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.termites.R;
import com.termites.tools.javabean.EquipmentBean;
import com.termites.tools.javabean.InspectionBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LF on 16/10/21.
 */

public class DataUploadInspectionAdapter extends CommonAdapter<InspectionBean> {

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> mSelectedMap = new HashMap<>();
    // 用来控制CheckBox是否显示
    private boolean IsOpenEditModal = false;

    private CheckBox dataupload_lv_check_cb;

    public HashMap<Integer, Boolean> getSelected() {
        return mSelectedMap;
    }

    // 初始化isSelected的数据
    public void initSelectedDate(List<InspectionBean> datas) {
        for (int i = 0; i < datas.size(); i++) {
            getSelected().put(i, false);
        }
    }

    public DataUploadInspectionAdapter(Context context) {
        super(context, R.layout.layout_dataupload_lv_item);
    }

    public void setOpenEditModal(boolean isOpen) {
        this.IsOpenEditModal = isOpen;
    }

    public boolean getOpenEditModal() {
        return IsOpenEditModal;
    }

    @Override
    public void addAll(List<InspectionBean> datas) {
        super.addAll(datas);
        initSelectedDate(datas);
    }

    @Override
    public void convert(CommonViewHolder vh, InspectionBean dataUploadLvBean, int position) {
        vh.setText(R.id.dataupload_lv_id, "监测装置编号: " + dataUploadLvBean.getInspecId())
                .setText(R.id.dataupload_lv_idorstate, "白蚁状态: " + dataUploadLvBean.getInspectionTermiteState())
                .setText(R.id.dataupload_lv_time, "巡检时间: " + dataUploadLvBean.getInspectionTime().replace("%20", " "))
                .setVisibility_TextView(R.id.dataupload_lv_longitude, View.GONE)
                .setVisibility_TextView(R.id.dataupload_lv_latitude, View.GONE);

        dataupload_lv_check_cb = vh.getView(R.id.dataupload_lv_check_cb);

        if (IsOpenEditModal) {
            vh.setVisibility_CheckBox(dataupload_lv_check_cb, View.VISIBLE);
        } else {
            vh.setVisibility_CheckBox(dataupload_lv_check_cb, View.GONE);
        }
        vh.setChecked(dataupload_lv_check_cb, getSelected().get(position).booleanValue());

    }
}
