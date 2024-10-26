package com.example.mymusic;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service {
    MediaPlayer mMediaPlayer;
    private static final int ONGOING_NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "Music channel";

//    步骤概述
//    创建通知渠道
//    创建通知内容
//    将服务设置为前台服务
    NotificationManager mNotificationManager;

    private final IBinder mBinder = new MusicServiceBinder();

    public MusicService() {
    }

    @Override
    public void onDestroy(){
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        super.onDestroy();
    }

    @Override
    public  void onCreate(){
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    public void pause(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    public void play(){
        if (mMediaPlayer != null){
            mMediaPlayer.start();
        }
    }

    public int getDuration(){
        int duration = 0;
        if(mMediaPlayer != null){
            duration = mMediaPlayer.getDuration();
        }

        return duration;
    }

    public int getCurrentPosition(){
        int positon = 0;
        if(mMediaPlayer != null){
            positon = mMediaPlayer.getCurrentPosition();
        }
        return positon;
    }

    public boolean isPlaying(){
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }


    //接收播放请求，解析音乐文件的URI，初始化MediaPlayer，并开始播放。
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        String data = intent.getStringExtra(MainActivity.DATA_URI);
        Uri dataUri = Uri.parse(data);
        String title = intent.getStringExtra("TITLE");
        String artist = intent.getStringExtra("ARTIST");


        if (mMediaPlayer != null){
            try{
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getApplicationContext(),dataUri);
                mMediaPlayer.prepare();
                mMediaPlayer.start();

                Intent musicStartIntent = new Intent(MainActivity.ACTION_MUSIC_START);
                sendBroadcast(musicStartIntent);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            mMediaPlayer = new MediaPlayer();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 获取通知管理器
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // 创建一个通知渠道
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,                   // 渠道ID
                    "Music Channel",              // 渠道名称（用户在设置中看到的名称）
                    NotificationManager.IMPORTANCE_HIGH  // 渠道重要性级别
            );

            // 如果通知管理器不为空，则创建通知渠道
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }


        Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);

        //创建通知内容
        Notification notification = builder.setContentTitle(title).setContentText(artist).setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();

        //将服务提升为前台服务，并显示通知
        startForeground(ONGOING_NOTIFICATION_ID,notification);
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicServiceBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }
}
