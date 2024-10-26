package com.example.photosharing.net;

import java.util.Map;

import okhttp3.Callback;
import okhttp3.Headers;

public interface MyHttps {

    // 无请求参数get
    void doGet(String url, Headers headers, Callback callback);

    // 有请求参数get
    void doGet(String url, Headers headers, Map<String, String> params, Callback callback);

    // 带请求头post
    void doPost(String url, Headers headers, Map<String, Object> params, Callback callback);

}
