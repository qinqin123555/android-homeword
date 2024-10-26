package com.example.mymusic;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import com.example.mymusic.MusicService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* 底部导航组件 */
    /**
     * 底部导航视图
     */
    private BottomNavigationView bottomNavigation;
    /**
     * 歌曲名
     */
    private TextView tvBottomTitle;
    /**
     * 歌手名
     */
    private TextView tvBottomArtist;
    /**
     * 专辑封面图
     */
    private ImageView ivAlbumThumbnail;
    /**
     * 播放按钮图标
     */
    private ImageView ivPlay;
    /**
     * 播放状态
     */
    private Boolean ivPlayStatus = true;

    /* 媒体播放器 MediaPlayer */
    /**
     * 媒体播放器
     */
    private MediaPlayer mMediaPlayer = null;

    /* 内容解析器 ContentResolver */
    /**
     * 内容解析器
     */
    private ContentResolver mContentResolver;
    /**
     * 选择语句where子句
     */
    private final String SELECTION = MediaStore.Audio.Media.IS_MUSIC + " = ? " + " AND " + MediaStore.Audio.Media.MIME_TYPE + " LIKE ? ";
    /**
     * where子句参数
     */
    private final String[] SELECTION_ARGS = {Integer.toString(1), "audio/mpeg"};
    /**
     * 请求外部存储
     */
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    /**
     * 权限存储
     */
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,  // 读取外部存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE  // 写入外部存储
    };

    /* 媒体光标适配器 MediaCursorAdapter */
    /**
     * 列表显示
     */
    private ListView mPlaylist;
    /**
     * 媒体光标适配器
     */
    private MediaCursorAdapter mCursorAdapter;

    /* 后台服务 */
    /**
     * 数据 URI
     */
    public static final String DATA_URI = "com.musiclist.DATA_URI";
    /**
     * 歌名
     */
    public static final String TITLE = "com.musiclist.TITLE";
    /**
     * 歌手
     */
    public static final String ARTIST = "com.musiclist.ARTIST";

    public static final String ACTION_MUSIC_START = "com.example.code10.ACTION_MUSIC_START";
    public static final String ACTION_MUSIC_STOP = "com.example.code10.ACTION_MUSIC_STOP";


    private Boolean mPlayStatus = true;

    private MusicReceiver musicReceiver;


    /* 监听器 */
    /**
     * 歌曲界面点击 监听器
     */
    private final ListView.OnItemClickListener mPlaylistClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int i, long l) {
            // i -> 所需获取的多媒体音乐信息在Cursor对象中的位置,即点击了第几首歌
            Log.d("intI", Integer.toString(i));

            Cursor cursor = mCursorAdapter.getCursor();
            if (cursor != null && cursor.moveToPosition(i)) { // cursor.moveToPosition(i) 移动到指定行
                /* 获取索引 */
                int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                /* 获取资源 */
                String title = cursor.getString(titleIndex);
                String artist = cursor.getString(artistIndex);
                long albumId = cursor.getLong(albumIdIndex);
                String data = cursor.getString(dataIndex);


                Uri dataUri = Uri.parse(data);

                /* 启动后台服务播放音乐 */
                //通过Intent发送数据到MusicService来启动播放
                Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
                serviceIntent.putExtra(MainActivity.DATA_URI, data);
                serviceIntent.putExtra(MainActivity.TITLE, title);
                serviceIntent.putExtra(MainActivity.ARTIST, artist);
                startService(serviceIntent); // 启动服务

                if (mMediaPlayer != null) {
                    try {
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(MainActivity.this, dataUri);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                /* 设置底部导航栏开始 不可见 */
                bottomNavigation.setVisibility(View.VISIBLE);

                /* 将 歌名 绑定到控件上 */
                if (tvBottomTitle != null) tvBottomTitle.setText(title);
                /* 将 歌手 绑定到控件上 */
                if (tvBottomArtist != null) tvBottomArtist.setText(artist);

                /* 将 封面 绑定到控件上 */
                Uri albumUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId); // 查询
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        Bitmap album = mContentResolver.loadThumbnail(albumUri, new Size(640, 480), null);
                        ivAlbumThumbnail.setImageBitmap(album);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    };
    /**
     * 播放按钮单击 监听器
     */
    private final View.OnClickListener ivPlayButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_play) {
                ivPlayStatus = !ivPlayStatus;
                if (ivPlayStatus) {
                    ivPlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                } else {
                    ivPlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 实例化相关对象
        mContentResolver = getContentResolver();
        mCursorAdapter = new MediaCursorAdapter(MainActivity.this);

        // 绑定 ListView
        mPlaylist = findViewById(R.id.lv_playlist);

        // 设置适配器
        mPlaylist.setAdapter(mCursorAdapter);

        // 绑定 BottomNavigationView
        bottomNavigation = findViewById(R.id.navigation); // 拿到主布局的底部导航栏对象
        LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_media_toolbar, bottomNavigation, true); // 把 bottom_media_toolbar 放到主布局的底部导航栏中

        // 让底部导航栏一开始消失的
        bottomNavigation.setVisibility(View.GONE);

        // 绑定 BottomNavigationView 其他控件
        tvBottomTitle = bottomNavigation.findViewById(R.id.tv_bottom_title);
        tvBottomArtist = bottomNavigation.findViewById(R.id.tv_bottom_artist);
        ivAlbumThumbnail = bottomNavigation.findViewById(R.id.iv_thumbnail);
        ivPlay = bottomNavigation.findViewById(R.id.iv_play);
        pbProgress = bottomNavigation.findViewById(R.id.progress); // 初始化 ProgressBar


        // 查询权限，判断第一次打开APP时是否点了是
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE); // 如果第一次点了是，下次不会弹出; 如果点了否，下次还会弹出
        } else initPlaylist();

        // 设置监听器
        if (ivPlay != null) ivPlay.setOnClickListener(ivPlayButtonClickListener);
        mPlaylist.setOnItemClickListener(mPlaylistClickListener);

        // 设置播放按钮的点击事件监听器
        if (ivPlay != null) {
            ivPlay.setOnClickListener(MainActivity.this);
        }

        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MUSIC_START);
        intentFilter.addAction(ACTION_MUSIC_STOP);
        registerReceiver(musicReceiver,intentFilter);

    }


    //处理用户的权限请求
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {// 判断授权结果
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initPlaylist();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }*/

        Intent intent = new Intent(MainActivity.this,MusicService.class);
        bindService(intent,mConn, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop(){
           /* if (mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                Log.d(TAG,"onStop invoked!");
            }*/

        unbindService(mConn);
        mBound = false;
        super.onStop();
    }

    private void initPlaylist() {
        // 游标对象查询MP3数据
        Cursor mCursor = mContentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // 表名
                null,   // 所有列
                SELECTION,        // where子句
                SELECTION_ARGS,   // where参数
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER // 默认排序方式
        );
        mCursorAdapter.swapCursor(mCursor);
        mCursorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }



    private MusicService mService;
    private boolean mBound = false;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicServiceBinder binder = (MusicService.MusicServiceBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_play) {
            mPlayStatus = !mPlayStatus;
            if (mPlayStatus) {
                //播放
                mService.play();
                ivPlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
            } else {
                //暂停
                mService.pause();
                ivPlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
            }
        }
    }

    public static final int UPDATE_PROGRESS = 1;

    private ProgressBar pbProgress;
    private android.os.Handler mHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_PROGRESS:
                    int position = msg.arg1;
                    pbProgress.setProgress(position);
                    Log.d("ProgressUpdate", "Progress updated to: " + position); // 添加日志记录
                    break;
                default:
                    break;
            }
        }
    };

    //接收器中接收播放状态的广播，并启动MusicProgressRunnable线程更新进度条。
    private class MusicProgressRunnable implements Runnable{
        public MusicProgressRunnable(){
        }
        @Override
        public void run() {
            boolean mThreadWorking = true;
            while (mThreadWorking) {
                try {
                    if (mService != null && mService.isPlaying()) {
                        int position = mService.getCurrentPosition();
                        Message message = new Message();
                        message.what = UPDATE_PROGRESS;
                        message.arg1 = position;
                        mHandler.sendMessage(message);
                    }
                    Thread.sleep(100); // 降低频率以减少CPU消耗
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

    }

    //注册广播接收器，在接收到广播时更新进度条
    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent){

            try {
                if(mService != null){
                    pbProgress.setMax(mService.getDuration());
                    new Thread(new MusicProgressRunnable()).start();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(musicReceiver);
        super.onDestroy();
    }


}
