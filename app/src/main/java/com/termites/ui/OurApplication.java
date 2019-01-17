package com.termites.ui;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.termites.tools.CrashHandlers;
import com.termites.tools.ShowToast;
import com.termites.tools.config.LocalcacherConfig;

/**
 * Created by LF on 16/10/20.
 */

public class OurApplication extends Application {
    public static IUHFService mReader;
    private static OurApplication m_application; // 单例

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化百度地图的SDK
        SDKInitializer.initialize(getApplicationContext());
        // 初始化ShowToast
        ShowToast.init(this);
        // 初始化缓存
        LocalcacherConfig.init(this);
        // 异常信息捕捉
        CrashHandlers handler = CrashHandlers.getInstance();
        handler.init(this);

        mReader = UHFManager.getUHFService(this);
    }

    public IUHFService getmReader() {
        return mReader = UHFManager.getUHFService(this);
    }

    // 单例获取对象
    public static OurApplication getInstances() {
        if (m_application == null) {
            m_application = new OurApplication();
        }
        return m_application;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        UHFManager.closeUHFService();
        mReader = null;
    }
}
