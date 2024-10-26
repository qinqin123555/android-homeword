package com.example.code6;

import android.content.Context;
import android.provider.BaseColumns;

public final class NewsContract {
    private NewsContract(){}
    public static class NewsEntry implements BaseColumns {
        // 表名
        public static final String TABLE_NAME = "tbl_news";

        // 列名
        public static final String COLUMN_NAME_TITLE = "title";  // 新闻标题
        public static final String COLUMN_NAME_AUTHOR = "author";  // 新闻作者
        public static final String COLUMN_NAME_CONTENT = "content";  // 新闻内容
        public static final String COLUMN_NAME_IMAGE = "image";  // 新闻图片
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
                    NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY, " +
                    NewsContract.NewsEntry.COLUMN_NAME_TITLE + " VARCHAR(200), " +
                    NewsContract.NewsEntry.COLUMN_NAME_AUTHOR + " VARCHAR(100), " +
                    NewsContract.NewsEntry.COLUMN_NAME_CONTENT + " TEXT, " +
                    NewsContract.NewsEntry.COLUMN_NAME_IMAGE + " VARCHAR(100) " +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "news.db";

    private Context mContext;

}
