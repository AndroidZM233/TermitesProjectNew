package com.termites.tools;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 未捕获异常捕捉类
 *
 * @author lufan 2015年5月6日
 */
public class CrashHandlers implements UncaughtExceptionHandler {

    public static final String TGA = "CrashHandlers";

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler实例
    private static CrashHandlers instance;
    // 程序的Context对象
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandlers() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public synchronized static CrashHandlers getInstance() {
        if (instance == null) {
            instance = new CrashHandlers();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread, ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TGA, e.toString());
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ShowToast.getInstance().show("程序出现异常,请稍后再重新打开!");
                Looper.loop();
            }
        }.start();
        return true;
    }

}
