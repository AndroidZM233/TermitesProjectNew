package com.termites.tools.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.javabean.EquipmentBean;
import com.termites.tools.javabean.HistoryRecordBean;

import java.util.Collections;

/**
 * Created by LF on 16/10/21.
 */

public class EquipmentOrInspectionHistoryRecordAdapter extends CommonAdapter<HistoryRecordBean> {
    private TextView history_record_numer;
    private TextView history_record_idorstate;
    private TextView history_record_time;
    private TextView history_record_longitude;
    private TextView history_record_latitude;
    private String mType;

    public void setType(String mType) {
        this.mType = mType;
    }

    public EquipmentOrInspectionHistoryRecordAdapter(Context context) {
        super(context, R.layout.layout_eq_in_history_record_lv_item);
    }

    @Override
    public void convert(CommonViewHolder vh, HistoryRecordBean historyRecordBean, int position) {
        history_record_numer = vh.getView(R.id.history_record_numer);
        history_record_idorstate = vh.getView(R.id.history_record_idorstate);
        history_record_time = vh.getView(R.id.history_record_time);
        history_record_longitude = vh.getView(R.id.history_record_longitude);
        history_record_latitude = vh.getView(R.id.history_record_latitude);

        if (mType.equals("checkin")) {
            vh.setText(history_record_numer, "监测点编号: " + historyRecordBean.getEquipmentId() + "")
                    .setText(history_record_idorstate, "项目编号: " + historyRecordBean.getEquipmentProjectId())
                    .setText(history_record_time, "登记时间: " + historyRecordBean.getEquipmentCheckTime().replace("%20", " "));
            vh.setVisibility_TextView(history_record_longitude, View.VISIBLE)
                    .setVisibility_TextView(history_record_latitude, View.VISIBLE)
                    .setText(history_record_longitude, "经       度: " + Double.parseDouble(historyRecordBean.getEquipmentLongitude()))
                    .setText(history_record_latitude, "纬       度: " + Double.parseDouble(historyRecordBean.getEquipmentLatitude()));
        } else if (mType.equals("inspection")) {
            vh.setText(history_record_numer, "监测装置编号: " + historyRecordBean.getInspecId() + "")
                    .setText(history_record_idorstate, "白蚁状态: " + historyRecordBean.getInspectionTermiteState())
                    .setText(history_record_time, "巡检时间: " + historyRecordBean.getInspectionTime().replace("%20", " "));
            vh.setVisibility_TextView(history_record_longitude, View.GONE)
                    .setVisibility_TextView(history_record_latitude, View.GONE);
        }
        vh.setText(R.id.history_record_uploadstate, "上传状态: " + historyRecordBean.getUploadState());

    }
}
