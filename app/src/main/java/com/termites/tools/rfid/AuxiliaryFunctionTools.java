package com.termites.tools.rfid;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.provider.MediaStore;
import android.serialport.SerialPort;

import com.termites.tools.ShowToast;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 手持仪辅助功能的工具类
 * Created by LF on 16/10/26.
 */

public class AuxiliaryFunctionTools {
    // 打开相机的requestCode
    public static final int CameraRequestCode = 10;
    // 打开相册的requestCode
    public static final int PhotoAlbumRequestCode = 20;
    private static SerialPort mSerialport = null;

    public static class ApplicationType {
        public static final int CAMERA = 1;
        public static final int CALENDAR = 2;
        public static final int CALCULATOR = 3;
        public static final int FLASHLIGHT = 4;
        public static final int CLOCK = 5;
        public static final int PHOTOALBUM = 6;
    }

    // 打开系统相机
    public static void openCamera(Activity mActivity) {
        if (!MethodConfig.checkCameraDevice(mActivity)) {
            ShowToast.getInstance().show("抱歉,您摄像头功能不可用");
            return;
        }

        if (!LocalcacherConfig.isUseNewDeviceCode) {
            if (!MethodConfig.hardwareSupportCheck()) {
                ShowToast.getInstance().show("请检查是否授权应用相机的权限");
                return;
            }
        }

        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
            mActivity.startActivityForResult(openCameraIntent, CameraRequestCode);
        } catch (ActivityNotFoundException e) {
            ShowToast.getInstance().show("抱歉,您摄像头功能不可用");
        }
    }

    // 打开系统日历
    public static void openCalendar(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName componentName;
            if (Build.VERSION.SDK_INT >= 8) {
                componentName = new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity");
            } else {
                componentName = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            ShowToast.getInstance().show("系统日历打开失败");
        }

    }

    // 打开系统计算器
    public static void openCalculator(Context context) {
        try {
            Intent mIntent = new Intent();
            mIntent.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
            context.startActivity(mIntent);
        } catch (Exception e) {
            ShowToast.getInstance().show("系统计算器打开失败");
        }
    }


    // 打开或关闭手持仪的手电筒
    public static void flashLight(boolean isOpen) {
        try {
            if (mSerialport == null) {
                mSerialport = new SerialPort();
            }
            if (isOpen) {
//                mSerialport.power3v3on();
            } else {
//                mSerialport.power3v3off();
            }
            mSerialport = null;
        } catch (Exception e) {
            ShowToast.getInstance().show("手电筒打开失败");
        }

    }

    // 打开系统闹钟
    public static void openClock(Context context) {
        // 过滤系统应用
        List<PackageInfo> allPackageInfos = context.getPackageManager()
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES |
                        PackageManager.GET_ACTIVITIES); // 取得系统安装所有软件信息
        List<PackageInfo> sysPackageInfos = new ArrayList<>();
        if (allPackageInfos != null && !allPackageInfos.isEmpty()) {
            for (PackageInfo apckageInfo : allPackageInfos) {
                ApplicationInfo appInfo = apckageInfo.applicationInfo;// 得到每个软件信息
                if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                        || (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    sysPackageInfos.add(apckageInfo);// 系统软件
                }
            }
        }

        String activityName = "";
        String packageName = "";
        String alarmPackageName = "";
        for (int i = 0; i < sysPackageInfos.size(); i++) {
            PackageInfo packageInfo = sysPackageInfos.get(i);
            packageName = packageInfo.packageName;
            // 包名中包含clock
            if (packageName.indexOf("clock") != -1) {
                if (!(packageName.indexOf("widget") != -1)) {
                    ActivityInfo activityInfo = packageInfo.activities[0];
                    // activity名中包含 Alarm和 DeskClock 大部分的闹钟程序名中都是按照这种规则命名 不能保证所有闹钟都是这样的
                    if (activityInfo.name.indexOf("Alarm") != -1 ||
                            activityInfo.name.indexOf("DeskClock") != -1) {
                        activityName = activityInfo.name;
                        alarmPackageName = packageName;
                    }
                }
            }
        }
        if ((activityName != "") && (alarmPackageName != "")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(alarmPackageName, activityName));
            context.startActivity(intent);
        } else {
            ShowToast.getInstance().show("系统闹钟打开失败");
        }
    }

    // 打开系统相册
    public static void openPhotoAlbum(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((Activity) context).startActivityForResult(intent, PhotoAlbumRequestCode);
        } catch (Exception e) {
            ShowToast.getInstance().show("系统相册打开失败");
        }
    }

}
