package com.termites.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.termites.tools.GsonUtil;
import com.termites.tools.config.HttpConfig;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.javabean.NetConnectionBean;

import org.json.JSONArray;

/**
 * Created by LF on 16/10/22.
 */

public class UploadEquipmentData {
    public UploadEquipmentData(Context context, JSONArray jsonArray, final SuccessCallback successCallback, final FailCallback failCallback) {

        new NetConnection(context, HttpConfig.URL_UPLOAD_EQUIPMENT, Request.Method.POST, new NetConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) {
                if (successCallback != null) {
                    NetConnectionBean bean = GsonUtil.GsonToBean(result, NetConnectionBean.class);
                    successCallback.onSuccess(bean);
                }
            }
        }, new NetConnection.FailCallback() {

            @Override
            public void onFail() {
                if (failCallback != null) {
                    failCallback.onFail();
                }
            }
        }, "list", jsonArray
                /*"?" + LocalcacherConfig.KEY_Custom + "=" + custom +
                "&" + LocalcacherConfig.KEY_Device + "=" + device +
                "&" + LocalcacherConfig.KEY_Project + "=" + project +
                "&" + LocalcacherConfig.KEY_Longitude + "=" + longitude +
                "&" + LocalcacherConfig.KEY_Latitude + "=" + latitude +
                "&" + LocalcacherConfig.KEY_EquipmentTime + "=" + equipmenttime +
                "&" + LocalcacherConfig.KEY_Location + "=" + location*/);
    }

    // 成功传递一个结果进来
    public interface SuccessCallback {
        void onSuccess(NetConnectionBean bean);
    }

    // 失败不做操作
    public interface FailCallback {
        void onFail();
    }
}
