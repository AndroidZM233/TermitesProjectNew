package com.termites.tools;

import android.app.Activity;

/**
 * 销毁Activity
 * Created by LF on 16/10/20.
 */
public class ActivityManagerDone {

	// 把某一个activity放入栈的方法
	public static void addOneActivity(Activity act) {
		ActivityManagerUtils.getInstance().pushOneActivity(act);
	}

	// 遍历栈中的所有的activity 并把所有在栈中的activity finish掉
	public static void finishAllActivities() {
		ActivityManagerUtils.getInstance().finishAllActivity();
	}

    // 把某一个activity移除栈,不finish该Activity，因为Activity自己会调用finish
    public static void finishOneActivityWithOutFinish(Activity act) {
        ActivityManagerUtils.getInstance().popOneActivityWithoutFinish(act);
    }

	// 把某一个activity移除栈, 并且finish该Activity
	public static void finishOneActivity(Activity act) {
		ActivityManagerUtils.getInstance().popOneActivity(act);
	}

}
