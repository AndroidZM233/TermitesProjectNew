package com.termites.tools;

import android.app.Activity;
import android.view.View;

/**
 * 简化findViewById
 * Created by LF on 16/10/20.
 */
@SuppressWarnings("unchecked")
public class ViewUtils {
	public static <E extends View> E findViewById(Activity activity, int resId) {
		return (E) activity.findViewById(resId);
	}

	public static <E extends View> E findViewById(View view, int resId) {
		return (E) view.findViewById(resId);
	}
}
