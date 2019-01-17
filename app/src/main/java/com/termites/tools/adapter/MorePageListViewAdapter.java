package com.termites.tools.adapter;

import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.javabean.AuxiliaryFunctionBean;
import com.termites.tools.rfid.AuxiliaryFunctionTools;

/**
 * Created by LF on 16/10/20.
 */

public class MorePageListViewAdapter extends CommonAdapter<AuxiliaryFunctionBean> {
    private TextView more_lv_calendar_text;

    public MorePageListViewAdapter(Context context) {
        super(context, R.layout.layout_more_fg_item);
    }

    public int getDay() {
        Time time = new Time("GMT+8");
        time.setToNow();
        return time.monthDay;
    }

    @Override
    public void convert(CommonViewHolder vh, AuxiliaryFunctionBean auxiliaryFunctionBean, int position) {
        more_lv_calendar_text = vh.getView(R.id.more_lv_calendar_text);
        vh.setText(R.id.more_lv_text, auxiliaryFunctionBean.getAppName())
                .setImageResource(R.id.more_lv_img, auxiliaryFunctionBean.getIconSource());

        if (auxiliaryFunctionBean.getAppType() == AuxiliaryFunctionTools.ApplicationType.CALENDAR) {
            vh.setVisibility_TextView(more_lv_calendar_text, View.VISIBLE).setText(more_lv_calendar_text, getDay() + "");
        } else {
            vh.setVisibility_TextView(more_lv_calendar_text, View.GONE);
        }

    }
}
