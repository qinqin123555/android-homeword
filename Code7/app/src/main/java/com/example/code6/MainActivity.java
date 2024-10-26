package com.example.code6;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public static final String NEWS_ID = "news_id";

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



        // 创建 MyDbOpenHelper 实例
        MyDbOpenHelper myDbHelper = new MyDbOpenHelper(MainActivity.this);

        // 更新新闻标题
        myDbHelper.updateNewsTitleByIdUsingSql(1, "5555");

// 获取可读数据库
        SQLiteDatabase db = myDbHelper.getReadableDatabase();

// 查询数据库中的数据
        Cursor cursor = db.query(
                NewsContract.NewsEntry.TABLE_NAME, // 表名
                null, // 列名（null 表示选择所有列）
                null, // 选择条件（null 表示没有条件）
                null, // 选择参数
                null, // 分组
                null, // 分组筛选条件
                null  // 排序
        );



        // 创建一个存储 News 对象的列表
        List<News> newsList = new ArrayList<>();

// 获取每一列在游标中的索引
        int titleIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NAME_TITLE);
        int authorIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NAME_AUTHOR);
        int imageIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NAME_IMAGE);

// 遍历游标中的每一行数据
        while (cursor.moveToNext()) {
            // 创建一个新的 News 对象
            News news = new News();

            // 从游标中获取数据
            String title = cursor.getString(titleIndex);
            String author = cursor.getString(authorIndex);
            String image = cursor.getString(imageIndex);

            // 将图像路径转换为 Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(

                    getClass().getResourceAsStream("/" + image));


            // 将数据设置到 News 对象中
            news.setmTitle(title);
            news.setmAuthor(author);
            news.setImage(bitmap);

            // 将 News 对象添加到列表中
            newsList.add(news);
        }

// 创建适配器并设置给 ListView
        NewAdapter newAdapter = new NewAdapter(
                MainActivity.this, // 上下文
                R.layout.list_item, // 列表项布局
                newsList // 数据源
        );

        ListView lvNewsList = findViewById(R.id.lv_news_list);
        lvNewsList.setAdapter(newAdapter);

    }

}
