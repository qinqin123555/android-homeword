package com.example.code10;

import android.annotation.SuppressLint;
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

public class MyService extends Service {
    MediaPlayer mMediaPlayer;
    private static final int ONGOING_NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "Music channel";
    NotificationManager mNotificationManager;

    private final IBinder mBinder = new MusicServiceBinder();

    public MyService() {
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

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Music Channel",NotificationManager.IMPORTANCE_HIGH);
            if (mNotificationManager != null){
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        Notification notification = builder.setContentTitle(title).setContentText(artist).setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();

        startForeground(ONGOING_NOTIFICATION_ID,notification);
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicServiceBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }

}