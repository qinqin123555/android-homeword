package com.example.code10;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
           Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ContentResolver mContentResolver;
    private ListView mPlaylist;
    private MediaCursorAdapter mCursorAdapter;

    private final String SELECTION = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND " +
            MediaStore.Audio.Media.MIME_TYPE + " LIKE ?";
    private final String[] SELECTION_ARGS = {"audio/mpeg"};


    private BottomNavigationView navigation;
    private TextView tvBottomTitle;
    private TextView tvBottomArtist;
    private ImageView ivAlbumThumbnail;
    private ImageView ivPlay;

    private MediaPlayer mMediaPlayer = null;

    public static final String DATA_URI = "com.example.code10.DATA_URI";
    public static final String TITLE = "com.example.code10.TITLE";
    public static final String ARTIST = "com.example.code10.ARTIST";
    public static final String ACTION_MUSIC_START = "com.example.code10.ACTION_MUSIC_START";
    public static final String ACTION_MUSIC_STOP = "com.example.code10.ACTION_MUSIC_STOP";

    private MyService mService;
    private boolean mBound = false;
    private MusicReceiver musicReceiver;

    private Boolean mPlayStatus = true;
    public static final int UPDATE_PROGRESS = 1;
    private ProgressBar pbProgress;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        public void handlerMessage(Message msg){
            switch (msg.what){
                case UPDATE_PROGRESS:
                    int position = msg.arg1;
                    pbProgress.setProgress(position);
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MusicServiceBinder binder = (MyService.MusicServiceBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    private class MusicProgressRunnable implements Runnable{
        public MusicProgressRunnable(){
        }
        @Override
        public void run(){
            boolean mThreadWording = true;
            while(mThreadWording){
                try{
                    if (mService != null){
                        int position = mService.getCurrentPosition();
                        Message message = new Message();
                        message.what = UPDATE_PROGRESS;
                        message.arg1 = position;
                        mHandler.sendMessage(message);
                    }
                    mThreadWording = mService.isPlaying();
                    Thread.sleep(100);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        }
    }

    public class MusicReceiver extends BroadcastReceiver{
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

    private void initPlaylist(){
        Cursor mCursor = mContentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            SELECTION,
            SELECTION_ARGS,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        mCursorAdapter.swapCursor(mCursor);
        mCursorAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mPlaylist = findViewById(R.id.lv_playlist);

        //实例化MediaCursorAdapter
        mContentResolver = getContentResolver();
        mCursorAdapter = new MediaCursorAdapter(MainActivity.this);
        mPlaylist.setAdapter(mCursorAdapter);


        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
            }else {
                requestPermissions(PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        }else {
            initPlaylist();
        }

        navigation = findViewById(R.id.navigation);
        LayoutInflater.from(MainActivity.this)
             .inflate(R.layout.bottom_media_toolbar,navigation,true);
//
//        pbProgress = navigation.findViewById(R.id.progress);

        ivPlay = navigation.findViewById(R.id.iv_play);
        tvBottomTitle = navigation.findViewById(R.id.tv_bottom_title);
        tvBottomArtist = navigation.findViewById(R.id.tv_bottom_artist);
        ivAlbumThumbnail = navigation.findViewById(R.id.iv_thumbnail);
        if (ivPlay != null) {
            ivPlay.setOnClickListener(MainActivity.this);
        }
        navigation.setVisibility(View.VISIBLE);
        //navigation.setVisibility(View.GONE);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                System.out.println("click");
                Cursor cursor = mCursorAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(i)) {
                    int titleIndex = cursor.getColumnIndex(
                            MediaStore.Audio.Media.TITLE);
                    int artistIndex = cursor.getColumnIndex(
                            MediaStore.Audio.Media.ARTIST);
                    int albumIdIndex = cursor.getColumnIndex(
                            MediaStore.Audio.Media.ALBUM_ID);
                    int dataIndex = cursor.getColumnIndex(
                            MediaStore.Audio.Media.DATA);
                    String title = cursor.getString(titleIndex);
                    String artist = cursor.getString(artistIndex);
                    Long albumId = cursor.getLong(albumIdIndex);
                    String data = cursor.getString(dataIndex);

                    Uri dataUri = Uri.parse(data);

                    Intent serviceIntent = new Intent(MainActivity.this,MyService.class);
                    serviceIntent.putExtra(MainActivity.DATA_URI,data);
                    serviceIntent.putExtra(MainActivity.TITLE,title);
                    serviceIntent.putExtra(MainActivity.ARTIST,artist);
                    startService(serviceIntent);

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

                    if (tvBottomTitle != null){
                        tvBottomTitle.setText(title);
                    }
                    if (tvBottomArtist != null){
                        tvBottomArtist.setText(artist);
                    }

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
        });

        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MUSIC_START);
        intentFilter.addAction(ACTION_MUSIC_STOP);
        registerReceiver(musicReceiver,intentFilter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initPlaylist();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() ==R.id.iv_play){
            mPlayStatus = !mPlayStatus;
            if(mPlayStatus ==true){
                mService.play();
                ivPlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
            }else{
                mService.pause();
                mService.pause();
                ivPlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }*/

        Intent intent = new Intent(MainActivity.this,MyService.class);
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


}