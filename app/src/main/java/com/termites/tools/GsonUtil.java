package com.termites.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 使用Gson解析JSON格式数据
 * Created by LF on 16/10/20.
 */
public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }

    public static Gson getIntanaces() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * @param object
     * @return
     */
    public static String GsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    public static Object JsontoString(String result, String key) {
        if (result == null || key == null)
            throw new NullPointerException("json or key is null");
        try {
            JSONObject obj = new JSONObject(result);
            if (obj != null && obj.opt(key) == null) {
                return null;
            }
            return obj.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJson(String gsonString, String key, String flag) {
        if (gsonString == null || key == null)
            throw new NullPointerException("json or key is null");
        try {
            JSONObject jsonObject = new JSONObject(gsonString);
            if (flag.equals("JSONObject")) {
                if (jsonObject.optJSONObject(key) == null) {
                    return "";
                }
                return jsonObject.optJSONObject(key).toString();
            } else if (flag.equals("JSONArray")) {
                if (jsonObject.optJSONArray(key) == null) {
                    return "";
                }
                return jsonObject.optJSONArray(key).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> GsonToList(String gsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> GsonToListMaps(String gsonString) {
        List<Map<String, T>> list = new ArrayList<Map<String, T>>();
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
            }.getType());
        }
        return list;
    }

    /**
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> GsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

}