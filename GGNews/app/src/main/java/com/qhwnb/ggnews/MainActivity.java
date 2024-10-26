package com.qhwnb.ggnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private ListView lvNewsList;

    private List<News> newsData;

    private NewsAdapter adapter;

    private int page = 1;


    private int[] mCols = new int[]{Constants.NEWS_COL5,
            Constants.NEWS_COL7, Constants.NEWS_COL8,
            Constants.NEWS_COL10, Constants.NEWS_COL11};


    private int mCurrentColIndex = 0;

    private okhttp3.Callback callback = new okhttp3.Callback() {


        @Override

        public void onFailure(Call call, IOException e) {

            Log.e("网络请求", "请求失败: " + e.getMessage());

            e.printStackTrace();

        }

        @Override

        public void onResponse(Call call, Response response)

                throws IOException {

            if (response.isSuccessful()) {
                // 打印成功信息到控制台
                Log.i("网络请求", "请求成功，状态码: " + response.code());
                final String body = response.body().string();

                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        Gson gson = new Gson();

                        Type jsonType =

                                new TypeToken<BaseResponse<List<News>>>() {
                                }.getType();

                        BaseResponse<List<News>> newsListResponse =

                                gson.fromJson(body, jsonType);

                        for (News news : newsListResponse.getData()) {

                            adapter.add(news);

                        }

                        adapter.notifyDataSetChanged();
                        isDataLoading = false;

                    }

                });

            } else {
// 打印失败的状态码和原因
                Log.e("网络请求", "请求失败，状态码: " + response.code() + ", 原因: " + response.message());
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }



    private void initView() {

        lvNewsList = findViewById(R.id.lv_news_list);

        lvNewsList.setOnItemClickListener(

        new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 边界检查，确保 position 不超出 newsData 列表的范围
                if (position >= 0 && position < newsData.size()) {
                    News news = newsData.get(position);
                    Log.d("NewsItemClick", "Clicked news title: " + news.getTitle());
                    Log.d("NewsItemClick", "Clicked news URL: " + news.getContentUrl());

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(Constants.NEWS_DETAIL_URL_KEY, news.getContentUrl());
                    startActivity(intent);
                } else {
                    Log.e("NewsItemClick", "Invalid position: " + position);
                    Toast.makeText(MainActivity.this, "无效的新闻位置", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }


    private void initData() {

        newsData = new ArrayList<>();

        adapter = new NewsAdapter(MainActivity.this,

                R.layout.list_item, newsData);

        lvNewsList.setAdapter(adapter);

        refreshData(1);

        initScrollListener();

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

                Request request = new Request.Builder()

                        .url(Constants.GENERAL_NEWS_URL + urlParams)

                        .get().build();

                try {

                    OkHttpClient client = new OkHttpClient();

                    client.newCall(request).enqueue(callback);

                } catch (NetworkOnMainThreadException ex) {

                    ex.printStackTrace();

                }

            }

        }).start();

    }


    private void initScrollListener() {
        lvNewsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 滚动状态改变时的回调，这里不需要做特殊处理
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判断是否滚动到底部
                if (firstVisibleItem + visibleItemCount == totalItemCount && !isDataLoading) {
                    // 滚动到底部，加载下一页数据
                    page++; // 增加页数
                    refreshData(page);
                    isDataLoading = true; // 设置数据加载标志
                }
            }
        });
    }

    // 标记数据是否正在加载，避免重复加载
    private boolean isDataLoading = false;
}