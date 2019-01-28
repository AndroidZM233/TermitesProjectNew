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
            vh.setText(history_record_numer, mContext.getString(R.string.checkin_adapter1) + historyRecordBean.getEquipmentId() + "")
                    .setText(history_record_idorstate, mContext.getString(R.string.checkin_adapter2) + historyRecordBean.getEquipmentProjectId())
                    .setText(history_record_time, mContext.getString(R.string.checkin_adapter3) + historyRecordBean.getEquipmentCheckTime().replace("%20", " "));
            vh.setVisibility_TextView(history_record_longitude, View.VISIBLE)
                    .setVisibility_TextView(history_record_latitude, View.VISIBLE)
                    .setText(history_record_longitude, mContext.getString(R.string.checkin_adapter4) + Double.parseDouble(historyRecordBean.getEquipmentLongitude()))
                    .setText(history_record_latitude, mContext.getString(R.string.checkin_adapter5) + Double.parseDouble(historyRecordBean.getEquipmentLatitude()));
        } else if (mType.equals("inspection")) {
            vh.setText(history_record_numer, mContext.getString(R.string.ins_adapter1) + historyRecordBean.getInspecId() + "")
                    .setText(history_record_idorstate, mContext.getString(R.string.ins_adapter2)+ historyRecordBean.getInspectionTermiteState())
                    .setText(history_record_time, mContext.getString(R.string.ins_adapter3) + historyRecordBean.getInspectionTime().replace("%20", " "));
            vh.setVisibility_TextView(history_record_longitude, View.GONE)
                    .setVisibility_TextView(history_record_latitude, View.GONE);
        }
        vh.setText(R.id.history_record_uploadstate, mContext.getString(R.string.ins_adapter4) + historyRecordBean.getUploadState());

    }
}
