package com.example.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        // 获取传递过来的新闻URL
        webView = findViewById(R.id.webview_news_detail);

        // 启用JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // 设置WebViewClient以处理页面加载
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在这里可以处理URL重定向等，这里简单地加载URL
                view.loadUrl(url);
                return true;
            }
        });

        // 从Intent中获取新闻详情URL并加载
        String newsUrl = getIntent().getStringExtra(Constants.NEWS_DETAIL_URL_KEY);
        if (newsUrl != null) {
            webView.loadUrl(newsUrl);
        } else {
            // 处理URL为空的情况
            webView.loadUrl("about:blank"); // 或者显示一个错误页面
        }
    }
}


/*public class DetailActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        // 获取传递过来的新闻URL
        String newsUrl = getIntent().getStringExtra(Constants.NEWS_DETAIL_URL_KEY);

        // 假设我们这里直接显示URL作为新闻内容（实际中你可能需要网络请求获取详情）
        TextView textView = findViewById(R.id.tv_news_detail);
        textView.setText("新闻详细内容URL: " + newsUrl);

        // 注意：这里只是简单示例，实际开发中你可能需要异步加载新闻详情内容

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}*/
