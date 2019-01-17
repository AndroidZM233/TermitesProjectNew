package com.termites.tools;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity管理类
 * Created by LF on 16/10/20.
 */
public class ActivityManagerUtils {
	private static ActivityManagerUtils instance = new ActivityManagerUtils();
	private Stack<Activity> activityStack;// activity栈,存放所有的activity

	private ActivityManagerUtils() {// 构造函数
	}

	// 单例模式
	public static ActivityManagerUtils getInstance() {
		return instance;
	}

	// 把一个activity压入栈中
	public synchronized void pushOneActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	// 获取栈顶的activity，先进后出原则
	public Activity getLastActivity() {
		return activityStack.lastElement();
	}

	// 移除一个activity, 并且调用该Activity的finish方法
	public synchronized void popOneActivity(Activity activity) {
		if (activityStack != null && activityStack.size() > 0) {
			if (activity != null) {
				activity.finish();
				activityStack.remove(activity);
			}
		}
	}

    // 移除一个activity, 不需要调用该Activity的finish方法
    public synchronized void popOneActivityWithoutFinish(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activity != null) {
                activityStack.remove(activity);
            }
        }
    }

	// 退出所有activity
	public void finishAllActivity() {
		if (activityStack != null) {
			while (activityStack.size() > 0) {
				Activity activity = getLastActivity();
				if (activity == null)
					break;
				popOneActivity(activity);
			}
		}
	}
}