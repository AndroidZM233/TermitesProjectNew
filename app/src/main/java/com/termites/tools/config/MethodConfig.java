package com.termites.tools.config;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.termites.R;
import com.termites.tools.GsonUtil;
import com.termites.tools.SecuritUtil;
import com.termites.tools.ShowToast;
import com.termites.tools.javabean.CustomBean;
import com.termites.ui.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LF on 16/10/20.
 */

public class MethodConfig {


    /**
     * 获取机器码
     * DEVICE_ID+IMSI+设备的序列号 三个相加的MD5值
     *
     * @return
     */
    public static String getRobotCode(Context context) {
        try {
            return SecuritUtil.md5(getDevice_Id(context) + getSubscriber_Id(context) + getSerial());
        } catch (Exception e) {
            return "机器码获取异常";
        }

    }

    /**
     * 获取设备的唯一标识DEVICE_ID
     */
    private static String getDevice_Id(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取设备的IMSI 储存在SIM卡中
     */
    private static String getSubscriber_Id(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    /**
     * 获取设备的序列号
     */
    private static String getSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 当前网络是否可用
     */

    public static boolean isNetWorkAvailables(final Context context) {
        if (isNetworkAvailable(context)) {
            return true;
        }
        ShowToast.getInstance().show(context.getResources().getString(R.string.no_internet), 0);
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 加载EmptyView
     */
    public static View getEmptyViewWithText(Context context, String emptyText, int resImgId) {
        View mEmptyView = LayoutInflater.from(context).inflate(R.layout.layout_empty_lv, null);
        ((TextView) mEmptyView.findViewById(R.id.lv_empty_text)).setText(emptyText);
        ((ImageView) mEmptyView.findViewById(R.id.lv_empty_icon)).setImageResource(resImgId);
        return mEmptyView;
    }

    /**
     * 获取当前系统的时间
     */
    public static String getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 解析数据同步JSON
     */
    public static CustomBean getJsonSycnData(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        CustomBean bean = GsonUtil.GsonToBean(GsonUtil.getJson(jsonStr, LocalcacherConfig.KEY_Message, "JSONObject"), CustomBean.class);
        return bean;
    }

    /**
     * 检测Android设备是否支持摄像机
     */
    public static boolean checkCameraDevice(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断相机是否开启了
     */
    public static boolean hardwareSupportCheck() {
        // Camera needs to open
        Camera c = null;
        try {
            c = Camera.open();
        } catch (RuntimeException e) {
            // throw new RuntimeException();
        }
        if (c == null) {
            return false;
        } else {
            c.release();
        }
        return true;
    }

    /**
     * 修改Activity的透明度
     */
    public static void showActivityAlpha(BaseActivity mActivity) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.6f;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * 恢复Activity的透明度
     */
    public static void hideActivityAlpha(BaseActivity mActivity) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1f;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(lp);
    }

    private static DecimalFormat df = null;

    public static DecimalFormat format(String pattern) {
        if (df == null) {
            df = new DecimalFormat();
        }
        df.setRoundingMode(RoundingMode.FLOOR);
        df.applyPattern(pattern);
        return df;
    }

    private static Camera m_Camera = null;

    public static void openNewFlash() {
        try {
            m_Camera = Camera.open();
            Camera.Parameters mParameters;
            mParameters = m_Camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            m_Camera.setParameters(mParameters);
        } catch (Exception e) {
            ShowToast.getInstance().show("手电筒打开失败");
        }

    }

    public static void closeNewFlash() {
        try {
            Camera.Parameters mParameters;
            mParameters = m_Camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            m_Camera.setParameters(mParameters);
            m_Camera.release();
        } catch (Exception e) {
            ShowToast.getInstance().show("手电筒关闭失败");
        }
    }

    public static void saveFile(String content) {
        File file = new File(LocalcacherConfig.DeviceNumerFilePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String loadFile() {
        File file = new File(LocalcacherConfig.DeviceNumerFilePath);
        // 判断文件是否存在
        if (file.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                ByteArrayOutputStream bos = null;
                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }
                return bos.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
