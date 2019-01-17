package com.termites.tools;

import android.view.View;

/**
 * 解决按钮重复点击的问题
 * Created by LF on 16/10/20.
 */
public class RepeatClick {

	public void setRepeatClick(final View v) {
		v.setEnabled(false);// 变灰不可点击
		v.postDelayed(new Runnable() {
			public void run() {
				v.setEnabled(true);
			};
		}, 1000);// 一秒钟后可以点击
	}
}
