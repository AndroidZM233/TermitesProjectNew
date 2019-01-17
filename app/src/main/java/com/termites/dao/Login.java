package com.termites.dao;

import android.content.Context;

import com.android.volley.Request;
import com.termites.tools.GsonUtil;
import com.termites.tools.config.HttpConfig;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.javabean.NetConnectionBean;

/**
 * Created by LF on 16/10/21.
 */

public class Login {
    public Login(Context context, String device, String name, String pwd, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(context, HttpConfig.URL_LOGIN, Request.Method.GET, new NetConnection.SuccessCallback() {

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
        }, "?" + LocalcacherConfig.KEY_Device + "=" + device +
                "&" + LocalcacherConfig.KEY_Name + "=" + name +
                "&" + LocalcacherConfig.KEY_Pwd + "=" + pwd);
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
