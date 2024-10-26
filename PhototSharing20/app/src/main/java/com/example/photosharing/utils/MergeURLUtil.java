package com.example.photosharing.utils;

import java.util.Map;

public class MergeURLUtil {

    public static String merge(String url, Map<String, String> map) {
        StringBuilder paramStr = new StringBuilder();
        for (String key : map.keySet()) {
            paramStr.append("&").append(key).append("=").append(map.get(key));
        }
        System.out.println(url + "?" + paramStr.substring(1));
        return url + "?" + paramStr.substring(1);
    }
}
