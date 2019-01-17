package com.termites.dao;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.termites.tools.ShowToast;
import com.termites.tools.VolleyErrorHelper;
import com.termites.tools.config.HttpConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LF on 16/10/21.
 * 接口请求封装
 */

public class NetConnection {

    private final Object lock = new Object();
    private static RequestQueue mQueue = null;
    private static boolean isToast = true;
    private String mNetUrl;
    private Map<String, String> post_data = new HashMap<String, String>();
    private String get_data = "";

    public NetConnection(final Context context, final String url, final int method, final SuccessCallback successCallback,
                         final FailCallback failCallback, final Object... kvs) {
        // 得到RequestQueue对象
        mQueue = getRequestQueueInstance(context);

        mNetUrl = HttpConfig.SEVER_URL + url;
        if (method == Request.Method.POST) {
            for (int i = 0; i < kvs.length; i += 2) {
                post_data.put(kvs[i].toString(), kvs[i + 1] + "");
            }
        } else if (method == Request.Method.GET) {
            get_data = kvs[0].toString();
            mNetUrl = mNetUrl + get_data;
        }

        switch (method) {
            case Request.Method.POST:
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, mNetUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (!TextUtils.isEmpty(response)) {
                                        if (successCallback != null) {
                                            successCallback.onSuccess(response.toString());
                                            logMethod(mNetUrl, post_data.toString(), null, "Success", response.toString());
                                        }
                                    } else {
                                        if (failCallback != null) {
                                            failCallback.onFail();
                                            logMethod(mNetUrl, post_data.toString(), null, "Fail", null);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (failCallback != null) {
                                        failCallback.onFail();
                                        logMethod(mNetUrl, post_data.toString(), null, "Exception", null);
                                    }
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (isToast) {
                                    ShowToast.getInstance().show(VolleyErrorHelper.getMessage(error, context), 0);
                                    isToast = false;
                                    sendHandler(context);
                                }
                                if (failCallback != null) {
                                    failCallback.onFail();
                                    logMethod(mNetUrl, post_data.toString(), error, "Error", null);
                                }
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return post_data;
                    }
                };
                requestQueue.add(stringRequest);

                break;
            case Request.Method.GET:
                StringRequest request = new StringRequest(Request.Method.GET, mNetUrl, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            if (response != null) {
                                if (successCallback != null) {
                                    successCallback.onSuccess(response);
                                    logMethod(mNetUrl, get_data, null, "Success", response);
                                }
                            } else {
                                if (failCallback != null) {
                                    failCallback.onFail();
                                    logMethod(mNetUrl, get_data, null, "Fail", null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (failCallback != null) {
                                failCallback.onFail();
                                logMethod(mNetUrl, get_data, null, "Exception", null);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isToast) {
                            ShowToast.getInstance().show(VolleyErrorHelper.getMessage(error, context), 0);
                            isToast = false;
                            sendHandler(context);
                        }
                        if (failCallback != null) {
                            failCallback.onFail();
                            logMethod(mNetUrl, get_data, error, "Error", null);
                        }
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(300 * 1000, 1, 1.0f));
                mQueue.add(request);
                break;
        }
    }

    private void logMethod(String urls, String data, VolleyError error, String state, String respone) {
        System.out.println("Request Url:" + urls);
        System.out.println("Request Data:" + data.toString());
        switch (state) {
            case "Success":
                System.out.println("Return Success Data: " + respone);
                break;
            case "Fail":
                System.out.println("Return Fail Data: " + respone);
                break;
            case "Exception":
                System.out.println("Return Exception Data: " + respone);
                break;
            case "Error":
                if (error != null) {
                    System.out.println("Return Error Data: " + error.getMessage());
                }
                break;
        }
    }

    // 1分钟后解禁ShowToast
    private void sendHandler(Context context) {
        Activity activity = (Activity) context;
        Window window = activity.getWindow();
        if (window != null) {
            window.getDecorView().postDelayed(new Runnable() {

                @Override
                public void run() {
                    isToast = true;
                }
            }, 60 * 1000);
        }
    }

    private RequestQueue getRequestQueueInstance(Context context) {
        if (mQueue == null) {
            synchronized (lock) {
                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(context);
                }
            }
        }
        return mQueue;
    }

    // 成功传递一个结果进来
    public interface SuccessCallback {
        void onSuccess(String result);
    }

    // 失败不做操作
    public interface FailCallback {
        void onFail();
    }
}
