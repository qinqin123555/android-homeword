package com.example.photosharing.net;

import okhttp3.Headers;

public class MyHeaders {

    //存储应用程序的身份验证信息
    private static String appId = "3422019bba8a4c0c89be9d4f64c96279";

    private static String appSecret = "853732836741dcee74506bd3895571eee4558";


    //公共的静态方法 getHeaders()，用于获取请求头信息。使用 Headers.Builder 创建一个新的请求头对象，并添加多个头字段
    //    "Accept"：指定客户端可以接受的响应内容类型
    //    "appId"：添加了之前声明的 appId 变量的值作为头字段的值
    //    "appSecret"：添加了之前声明的 appSecret 变量的值作为头字段的值
    //    "Content-Type"：指定请求的内容类型为 JSON
    //最后，通过调用 build() 方法构建并返回完整的请求头对象
    public static Headers getHeaders() {

        return new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", appId)
                .add("appSecret", appSecret)
                .add("Content-Type", "application/json")
                .build();
    }
}


