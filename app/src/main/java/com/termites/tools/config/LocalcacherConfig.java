package com.termites.tools.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by LF on 16/10/20.
 */

public class LocalcacherConfig {

    private static final String APP_ID = "com.termites";
    public static final String DeviceNumerFilePath = Environment.getExternalStorageDirectory().toString() + File.separator + "deviceNumber.txt";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private LocalcacherConfig() {

    }

    public static final void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    // 是否关闭测试代码
    public static final boolean isCloseTest = true;
    // 是否使用新的手持仪硬件代码
    public static final boolean isUseNewDeviceCode = false;

    public static final String KEY_HistoryRecordType = "HistoryRecordType";
    public static final String KEY_Name = "name";
    public static final String KEY_Pwd = "password";
    public static final String KEY_Device = "devince";
    public static final String KEY_MachineCode = "machinecode";
    public static final String KEY_IsError = "isError";
    public static final String KEY_Message = "message";
    public static final String KEY_CustomId = "customId";
    public static final String KEY_CustomCode = "customCode";
    public static final String KEY_SycnData = "SycnData";
    public static final String KEY_Custom = "custom";
    public static final String KEY_Project = "project";
    public static final String KEY_Longitude = "longitude";
    public static final String KEY_Latitude = "latitude";
    public static final String KEY_EquipmentTime = "equipmenttime";
    public static final String KEY_Location = "location";
    public static final String KEY_EquipmentId = "EquipmentId";
    public static final String KEY_Status = "status";
    public static final String KEY_InspectionTime = "inspectiontime";
    public static final String KEY_IsDataUpload = "IsDataUpload";
    public static final String KEY_IsHistoryData = "IsHistoryData";
    public static final String KEY_InspectionUser = "inspectionuser";

    public static final String KEY_CurrentEpc = "currentepc";
    public static final String KEY_Epc = "epc";
    public static final String KEY_Successed = "successed";

    public static class JumpTypes {
        public static final String KEY_EquipmentCheckin = "EquipmentCheckin";
        public static final String KEY_DataUpload = "DataUpload";
        public static final String KEY_InspectionManager = "InspectionManager";
    }

    // 缓存账号
    public static void cacheUserName(String Account) {
        mEditor.putString(KEY_Name, Account).commit();
    }

    // 获取账号
    public static String getUserName() {
        return mSharedPreferences.getString(KEY_Name, null);
    }

    // 缓存密码
    public static void cacheUserPwd(String pwd) {
        mEditor.putString(KEY_Pwd, pwd).commit();
    }

    // 获取密码
    public static String getUserPwd() {
        return mSharedPreferences.getString(KEY_Pwd, null);
    }

    // 缓存用户ID
    public static void cacheCustomId(int Id) {
        mEditor.putInt(KEY_CustomId, Id).commit();
    }

    // 获取用户ID
    public static int getCustomId() {
        return mSharedPreferences.getInt(KEY_CustomId, 0);
    }

    // 缓存用户地区编号
    public static void cacheCustomAreaCode(String customCode) {
        mEditor.putString(KEY_CustomCode, customCode).commit();
    }

    // 获取用户地区编号
    public static String getCustomAreaCode() {
        return mSharedPreferences.getString(KEY_CustomCode, null);
    }


    // 缓存数据同步过来的数据
    public static void cacheSyncData(String jsonStr) {
        mEditor.putString(KEY_SycnData, jsonStr).commit();
    }

    // 获取数据同步过来的数据
    public static String getSyncData() {
        return mSharedPreferences.getString(KEY_SycnData, null);
    }

    // 缓存纬度
    public static void cacheCurrentLatitude(double la) {
        mEditor.putString(KEY_Latitude, la + "").commit();
    }

    // 获取纬度
    public static double getCurrentLatitude() {
        return Double.parseDouble(mSharedPreferences.getString(KEY_Latitude, "0"));
    }

    // 缓存经度
    public static void cacheCurrentLongitude(double lo) {
        mEditor.putString(KEY_Longitude, lo + "").commit();
    }

    // 获取经度
    public static double getCurrentLongitude() {
        String aa = mSharedPreferences.getString(KEY_Longitude, "0");
        return Double.parseDouble(aa);
    }

}
