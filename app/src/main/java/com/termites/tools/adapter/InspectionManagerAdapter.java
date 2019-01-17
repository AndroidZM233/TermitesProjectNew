package com.termites.tools.adapter;

import android.content.Context;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.javabean.InspectionBean;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by LF on 16/10/20.
 */

public class InspectionManagerAdapter extends CommonAdapter<InspectionBean> {
    private TextView inspection_num;
    private TextView inspection_equipment_num;
    private TextView inspection_termites_state;
    private Context mContext;

    public InspectionManagerAdapter(Context context) {
        super(context, R.layout.layout_inspection_manager_item);
        mContext = context;
    }

    /**
     * 添加新数据列表项
     */
    public void addNewsItem(InspectionBean newsitem) {
        mDatas.add(newsitem);
        Collections.sort(mDatas, new Comparator<InspectionBean>() {

            @Override
            public int compare(InspectionBean lhs, InspectionBean rhs) {
                Integer lhsShowId = lhs.getShowId();
                Integer rhsShowId = rhs.getShowId();
                return rhsShowId.compareTo(lhsShowId);
            }
        });
        notifyDataSetChanged();
    }


    @Override
    public void convert(CommonViewHolder vh, InspectionBean inspectionBean, int position) {
        inspection_num = vh.getView(R.id.inspection_num);
        inspection_equipment_num = vh.getView(R.id.inspection_equipment_num);
        inspection_termites_state = vh.getView(R.id.inspection_termites_state);

        int textColor = inspectionBean.getInspectionTermiteState().equals("有") ?
                R.color.text_red : R.color.black;

        vh.setText(inspection_num, inspectionBean.getShowId() + "")
                .setTextColor(inspection_num, textColor)
                .setText(inspection_equipment_num, inspectionBean.getInspecId())
                .setTextColor(inspection_equipment_num, textColor)
                .setText(inspection_termites_state, inspectionBean.getInspectionTermiteState())
                .setTextColor(inspection_termites_state, textColor);
    }
}
