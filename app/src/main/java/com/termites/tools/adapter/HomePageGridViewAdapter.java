package com.termites.tools.adapter;

import android.content.Context;

import com.termites.R;
import com.termites.tools.javabean.HomePageGvBean;


/**
 * 我的资产适配器
 *
 * @author lufan  2015年11月16日上午10:47:41
 */
public class HomePageGridViewAdapter extends CommonAdapter<HomePageGvBean> {

    public HomePageGridViewAdapter(Context context) {
        super(context, R.layout.layout_homepage_fg_item);

    }

    public void convert(CommonViewHolder vh, HomePageGvBean mGvBean, int position) {
        vh.setText(R.id.homepage_gv_text, mGvBean.getText())
                .setImageResource(R.id.homepage_gv_img, mGvBean.getIconSource());
    }
}
