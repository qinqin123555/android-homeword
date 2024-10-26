package com.example.code6;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private String[] titles = null;
//    private String[] authors = null;

    public static final String NEWS_ID = "news_id";
    private List<News> newsList = new ArrayList<>();
//
//    private static final String NEWS_TITLE = "news_title";
//    private static final String NEWS_AUTHOR = "news_author";

    private List<Map<String,String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initData();

//        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,
//                dataList, //要显示的数据
//                android.R.layout.simple_list_item_2, //布局资源ID，它指定了一个包含两个文本视图（TextView）的布局，用于在ListView中显示数据。
//                new String[]{NEWS_TITLE, NEWS_AUTHOR}, //SimpleAdapter中需要绑定到布局中的数据的键
//                new int[]{android.R.id.text1, android.R.id.text2}); //布局中每个文本视图的ID
//
//        ListView lvNewsList = findViewById(R.id.lv_news_list);
//        lvNewsList.setAdapter(simpleAdapter);

        initData();

        NewAdapter newsAdapter = new NewAdapter(MainActivity.this, R.layout.list_item, newsList);
        ListView lvNewsList = findViewById(R.id.lv_news_list);
        lvNewsList.setAdapter(newsAdapter);
    }

    private void initData() {
        int length;
//        titles = getResources().getStringArray(R.array.titles);
//        authors = getResources().getStringArray(R.array.authors);
//
//        if (titles.length > authors.length) {
//            length = authors.length;
//        } else {
//            length = titles.length;
//        }
//
//        for (int i = 0; i < length; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put(NEWS_TITLE, titles[i]);
//            map.put(NEWS_AUTHOR, authors[i]);
//            dataList.add(map);
//        }

        String[] titles = getResources().getStringArray(R.array.titles);
        String[] authors = getResources().getStringArray(R.array.authors);
        TypedArray images = getResources().obtainTypedArray(R.array.images);

        length = Math.min(titles.length, authors.length);

        for (int i = 0; i < length; i++) {
            News news = new News();
            news.setmTitle(titles[i]);
            news.setmAuthor(authors[i]);
            news.setmImageId(images.getResourceId(i, 0));

            newsList.add(news);
        }

        images.recycle();  // Recycle the TypedArray to free up resources
    }
    }
