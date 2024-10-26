package com.example.photosharing.net;

import com.example.photosharing.utils.MergeURLUtil;
import com.google.gson.Gson;

import java.util.Map;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


//实现MyHttps 接口
//封装发送HTTP请求的逻辑，通过接口MyHttps定义方法，以发送GET或POST请求，并处理响应结果
public class MyRequest implements MyHttps {

    //创建了一个私有的静态变量requestBody，用于存储MyRequest类的单例对象
    private static MyRequest requestBody = new MyRequest();

    //声明了一个名为client的不可变的OkHttpClient对象，用于发送HTTP请求
    private final OkHttpClient client;


    //MyRequest类的构造函数，在实例化对象时初始化client成员变量
    public MyRequest() {
        this.client = new OkHttpClient().newBuilder().build();
    }

    //getRequestBody()获取MyRequest类的单例对象
    //如果requestBody为空，则第一次调用该方法时，会创建一个新的对象并赋值给requestBody
    public static MyRequest getRequestBody() {
        if (requestBody == null) {
            requestBody = new MyRequest();
        }
        return requestBody;
    }

    //实现了MyHttps接口中的doGet()方法，用于发送HTTP GET请求。
    //接受一个URL、请求头和回调函数作为参数，创建一个GET请求，并通过OkHttpClient发送异步请求
    @Override
    public void doGet(String url, Headers headers, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //还接受一个Map<String,String>类型的参数 params，用于将查询参数添加到URL中
    @Override
    public void doGet(String url, Headers headers, Map<String, String> params, Callback callback) {
        Request request = new Request.Builder()
                .url(MergeURLUtil.merge(url, params))
                .headers(headers)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //发送HTTP POST请求
    //URL、请求头和一个Map<String, Object>类型的参数params，将参数转换为JSON字符串，并创建一个POST请求，通过OkHttpClient 发送异步请求
    //回调函数作为参数传递给 doPost 方法的第四个参数callback
    @Override
    public void doPost(String url, Headers headers, Map<String, Object> params, final Callback callback) {
        //向服务器提交数据
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(params));
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
