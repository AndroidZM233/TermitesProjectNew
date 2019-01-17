package com.termites.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.termites.tools.GsonUtil;
import com.termites.tools.config.HttpConfig;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.javabean.CustomBean;

/**
 * Created by LF on 16/10/21.
 */

public class DataSync {
    public DataSync(Context context, String device, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(context, HttpConfig.URL_DATASYN, Request.Method.GET, new NetConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) {
                Log.d("+++++++++++++",result);
                if (!Boolean.parseBoolean(GsonUtil.JsontoString(result, LocalcacherConfig.KEY_IsError).toString())) {
                    if (successCallback != null) {
                        LocalcacherConfig.cacheSyncData(result);
                        CustomBean bean = GsonUtil.GsonToBean(GsonUtil.getJson(result, LocalcacherConfig.KEY_Message, "JSONObject"), CustomBean.class);
                        LocalcacherConfig.cacheCustomAreaCode(bean.getCustomCode());
                        LocalcacherConfig.cacheCustomId(bean.getCustomId());
                        successCallback.onSuccess();
                    }
                } else {
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }
            }
        }, new NetConnection.FailCallback() {

            @Override
            public void onFail() {
                if (failCallback != null) {
                    failCallback.onFail();
                }
            }
        }, "?" + LocalcacherConfig.KEY_Device + "=" + device);
    }

    // 成功传递一个结果进来
    public interface SuccessCallback {
        void onSuccess();
    }

    // 失败不做操作
    public interface FailCallback {
        void onFail();
    }
}
