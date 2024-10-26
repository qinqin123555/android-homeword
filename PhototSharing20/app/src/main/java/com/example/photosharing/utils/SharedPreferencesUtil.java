package com.example.photosharing.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesUtil {
    public static String PREFERENCES_NAME = "user";

    /**
     * 保存map信息
     */
    public static boolean putMap(Context context, Map<String, Object> map) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            editor.putString(entry.getKey(), (String) entry.getValue());
        }
        return editor.commit();
    }

    /**
     * 保存String信息
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 获取某个值
     */
    public static String getValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    /**
     * 获取map
     */
    public static Map<String, String> getMap(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            if (entry.getValue() instanceof String) {
                map.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return map;
    }

    /**
     * 更新某个值
     */
    public static boolean updateValue(Context context, Map<String, Object> map) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            editor.putString(entry.getKey(), (String) entry.getValue());
        }
        return editor.commit();
    }

    /**
     * 清除数据
     */
    public static boolean clearPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        return editor.commit();
    }
}

