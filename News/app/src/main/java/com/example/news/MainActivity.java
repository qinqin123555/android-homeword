package com.example.news;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private ListView lvNewsList;
    private NewsAdapter adapter;
    private List<News> newsData ;


    private final int page = 1;
    private int mCurrentColIndex = 0;
    private int[] mCols = new int[]{Constants.NEWS_COL5,
            Constants.NEWS_COL7, Constants.NEWS_COL8,
            Constants.NEWS_COL10, Constants.NEWS_COL11};

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Failed to connect server!");
            e.printStackTrace();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body(); // 首先获取ResponseBody对象
                if (responseBody != null) { // 检查ResponseBody是否为null
                    String body = responseBody.string(); // 如果不为null，则安全地调用string()方法
                    try {
                        Gson gson = new Gson();
                        Type jsonType = new TypeToken<BaseResponse<List<News>>>() {}.getType();
                        BaseResponse<List<News>> newsListResponse = gson.fromJson(body, jsonType);
                        if (newsListResponse != null && newsListResponse.getData() != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (News news : newsListResponse.getData()) {
                                        adapter.add(news);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JsonSyntaxException e) {
                        // 处理JSON解析错误
                        Log.e(TAG, "Error parsing JSON response", e);
                    } finally {
                        // 释放ResponseBody的资源，注意：string()方法调用后，ResponseBody将不再可用
                        responseBody.close();
                    }
                } else {
                    // 处理ResponseBody为null的情况
                    Log.e(TAG, "Received response with null body");
                }
            } else {
                // 处理不成功的响应，例如4xx或5xx状态码
                Log.e(TAG, "Unsuccessful response: " + response.code());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initView();
        initData();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initView() {
        lvNewsList = findViewById(R.id.lv_news_list);
        lvNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                News news = adapter.getItem(i);
                intent.putExtra(Constants.NEWS_DETAIL_URL_KEY,
                        news.getContentUrl());
                startActivity(intent);
            }
        });
    }



    private void initData() {
        newsData = new ArrayList<>();
        adapter = new NewsAdapter(MainActivity.this,
                R.layout.list_item, newsData);
        lvNewsList.setAdapter(adapter);
        refreshData(1);
    }

    private void refreshData(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsRequest requestObj = new NewsRequest();
                requestObj.setCol(mCols[mCurrentColIndex]);
                requestObj.setNum(Constants.NEWS_NUM);
                requestObj.setPage(page);
                String urlParams = requestObj.toString();
                Request request = new Request.Builder().url(Constants.GENERAL_NEWS_URL + urlParams).get().build();
                try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(callback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}

