package com.example.code6;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbOpenHelper extends SQLiteOpenHelper { // 确保类名与构造函数名一致

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

    public MyDbOpenHelper(Context context) { // 确保构造函数名称正确
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        initDb(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    private void initDb(SQLiteDatabase sqLiteDatabase) {
        Resources resources = mContext.getResources();
        String[] titles = resources.getStringArray(R.array.titles);
        String[] authors = resources.getStringArray(R.array.authors);
        // String[] contents = resources.getStringArray(R.array.contents);
        TypedArray images = resources.obtainTypedArray(R.array.images);

        int length = Math.min(titles.length, Math.min(authors.length, images.length()));

        for (int i = 0; i < length; i++) {
            ContentValues values = new ContentValues();
            values.put(NewsContract.NewsEntry.COLUMN_NAME_TITLE, titles[i]);
            values.put(NewsContract.NewsEntry.COLUMN_NAME_AUTHOR, authors[i]);
            // values.put(NewsContract.NewsEntry.COLUMN_NAME_CONTENT, contents[i]);
            values.put(NewsContract.NewsEntry.COLUMN_NAME_IMAGE, images.getString(i));

            long r = sqLiteDatabase.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
        }

        images.recycle(); // 回收 TypedArray 对象
    }

    public void updateNewsTitleByIdUsingSql(long id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + NewsContract.NewsEntry.TABLE_NAME +
                " SET " + NewsContract.NewsEntry.COLUMN_NAME_TITLE + " = ? " +
                "WHERE " + NewsContract.NewsEntry._ID + " = ?";
        db.execSQL(sql, new Object[]{newTitle, id});
    }
}
