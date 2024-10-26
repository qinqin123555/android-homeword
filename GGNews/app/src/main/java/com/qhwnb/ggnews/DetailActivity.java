package com.qhwnb.ggnews;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// DetailActivity.java
public class DetailActivity extends AppCompatActivity {
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // 根据需要启用JavaScript

// 设置WebViewClient来处理 SSL 证书错误
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 选择忽略所有SSL证书错误
                handler.proceed(); // 继续加载页面
            }
        });

        // 获取Intent中的数据
        Intent intent = getIntent();
        String url = intent.getStringExtra(Constants.NEWS_DETAIL_URL_KEY);

        if (url != null && url.startsWith("http://")) {
            url = url.replaceFirst("http://", "https://");
        } else if(!url.startsWith("https://")) {
            url = url.replaceFirst("", "https:");
        }
        final String safeUrl = url;
        Log.d("dadad",safeUrl+"ddddddddddddddddddddddd");
        webView.loadUrl(safeUrl);
    }
}