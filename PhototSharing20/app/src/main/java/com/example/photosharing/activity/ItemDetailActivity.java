package com.example.photosharing.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.photosharing.R;
import com.example.photosharing.adapter.CommentAdapter;
import com.example.photosharing.entity.CommentBean;
import com.example.photosharing.entity.ShareBean;
import com.example.photosharing.databinding.ActivityItemDetailBinding;
import com.example.photosharing.net.MyHeaders;
import com.example.photosharing.net.MyRequest;
import com.example.photosharing.net.URLS;
import com.example.photosharing.utils.KeyBoardUtil;
import com.example.photosharing.utils.MergeURLUtil;
import com.example.photosharing.utils.SharedPreferencesUtil;
import com.example.photosharing.utils.ToolUtils;
import com.google.gson.Gson;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ItemDetailActivity extends AppCompatActivity {

    private List<ShareBean.DataBean.RecordsBean> items = new ArrayList<>();

    ShareBean.DataBean.RecordsBean item;

    private ActivityItemDetailBinding binding;

    private MyRequest request = new MyRequest();

    private List<CommentBean.DataBean.RecordsBean> list = new ArrayList<>();

    private int current = 0;

    private int size = 50;

    private int total;

    private CommentAdapter commentAdapter;

    Map<String, String> likeOrCollect = new HashMap<>();

    //Handler 是 Android 中用于处理线程间通信和消息处理的机制之一
    //处理不同消息类型的操作，并在主线程中更新UI(用户界面User Interface)，例如显示 Toast 提示或更新评论列表
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {


            getItemInfo();

            switch (msg.what) {
                case ItemOptions.CancelLike:
                    Toast.makeText(ItemDetailActivity.this, "取消点赞", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.Like:

                    Toast.makeText(ItemDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.CancelCollect:
                    Toast.makeText(ItemDetailActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.Collect:
                    Toast.makeText(ItemDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.CancelFocus:
                    Toast.makeText(ItemDetailActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.Focus:
                    Toast.makeText(ItemDetailActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.Comment:
                    Toast.makeText(ItemDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    binding.detailRemarkContent.setText("");
                    list.clear();
                    initCommentData();
                    commentAdapter.notifyDataSetChanged();
                    break;
                case ItemOptions.GetComment:
                    CommentBean commentBean = (CommentBean) msg.obj;
                    if (commentBean.getCode() == 200) {
                        list.addAll(commentBean.getData().getRecords());
                        if (list.size() != 0) {
                            current = commentBean.getData().getCurrent();
                            total = commentBean.getData().getTotal();
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    };




    // 获取项目的点赞和收藏标识符，并将它们存储在数组和对象中，以便在后续的操作中使用。
    // 可在项目详情页面展示用户是否已点赞或已收藏，或者用于用户的交互操作。
    protected void getItemInfo() {
        final String[] likeIdOrCollectId = new String[2];//存储获取到的点赞和收藏的标识符
        request.doGet(URLS.ItemInfo.getUrl(),//调用 request.doGet() 方法发送一个 GET 请求
                MyHeaders.getHeaders(),
                likeOrCollect,
                new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    //在回调函数中，处理请求的响应结果
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                //通过解析响应体中的 JSON 数据，创建一个 JSONObject 对象 jsonObject
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                //从 jsonObject 中获取 "data" 对象，并分别从该对象中获取 "likeId" 和 "collectId" 字段的值
                                //将它们分别赋给 likeIdOrCollectId 数组的第一个和第二个元素
                                likeIdOrCollectId[0] = jsonObject.getJSONObject("data").getString("likeId");
                                likeIdOrCollectId[1] = jsonObject.getJSONObject("data").getString("collectId");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            //将获取到的 "likeId" 和 "collectId" 分别存入 likeOrCollect 对象中
                            likeOrCollect.put("likeId", likeIdOrCollectId[0]);
                            likeOrCollect.put("collectId", likeIdOrCollectId[1]);
                        }
                    }
                });
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//调用父类的 onCreate() 方法来执行必要的初始化操作
        //获取与活动布局文件 activity_item_detail.xml 相关联的绑定类 binding
        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        //通过 binding.getRoot() 获取根视图，并通过 setContentView() 将该根视图设置为活动的内容视图
        View view = binding.getRoot();
        setContentView(view);
        // 系统toolbar隐藏
        Objects.requireNonNull(getSupportActionBar()).hide();


        //创建一个 LinearLayoutManager 对象 layoutManager，用于管理评论列表的布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(ItemDetailActivity.this);

        //创建一个 CommentAdapter 对象 commentAdapter，并传入当前的活动 (ItemDetailActivity.this) 和评论数据列表 list
        commentAdapter = new CommentAdapter(ItemDetailActivity.this, list);

        binding.detailRemark.setLayoutManager(layoutManager);

        binding.detailRemark.setAdapter(commentAdapter);

        //禁用嵌套滚动
        binding.detailRemark.setNestedScrollingEnabled(false);


        //从跳转到当前活动（ItemDetailActivity）的意图（Intent）中获取传递的数据，并解析其中的内容
        //包括帖子列表 items 和当前帖子在列表中的位置 position
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int position = intent.getIntExtra("position", 0);
        items = (List<ShareBean.DataBean.RecordsBean>) bundle.getSerializable("items");
        item = items.get(position);

        initCommentData();

        //评论数据的信息
        binding.detailUsername.setText(item.getUsername());
        binding.detailLoveNum.setText(item.getLikeNum());
        binding.detailLoveStatus.setSelected(item.isHasLike());
        binding.detailDescriptionTitle.setText(item.getTitle());
        binding.detailDescription.setText(item.getContent());
        binding.detailTime.setText(item.getCreateTime());
        binding.detailTime.setText(ToolUtils.formatTrackTime(Double.parseDouble(item.getCreateTime())));
        binding.detailLoveStatus.setSelected(item.isHasLike());
        binding.detailFollow.setSelected(item.isHasFocus());



        //根据项目的图片 URL 列表来显示图片轮播或者显示占位图片
        if (item.getImageUrlList().size() > 0) {
            //将项目的图片 URL 列表传递给适配器
            binding.banner.setAdapter(new BannerImageAdapter<String>(item.getImageUrlList()) {
                        @Override
                        public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                            Glide.with(holder.imageView)
                                    .load(data)
                                    .error(R.drawable.no_picture)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                                    .into(holder.imageView);
                        }
                    })
                    .addBannerLifecycleObserver(this)//添加生命周期观察者
                    .setIndicator(new CircleIndicator(this));
        } else {
            binding.banner.setBackgroundResource(R.drawable.no_picture);
        }


        //将帖子的相关标识符和用户信息存入 likeOrCollect 对象
        likeOrCollect.put("shareId", item.getId());
        likeOrCollect.put("likeId", item.getId());
        likeOrCollect.put("collectId", item.getId());
        likeOrCollect.put("userId", SharedPreferencesUtil.getValue(ItemDetailActivity.this, "id"));

        //重新获取帖子信息
        getItemInfo();



        // 喜欢点击事件
        binding.detailLoveStatus.setOnClickListener(new View.OnClickListener() {
            //设置喜欢按钮的点击事件，根据按钮的选中状态执行相应的操作
            //发送点赞或取消点赞的网络请求，并在请求成功后发送消息给 handler 处理
            @Override
            public void onClick(View view) {

                String loveNum = (String) binding.detailLoveNum.getText();
                if (binding.detailLoveStatus.isSelected()) { // 取消
                    binding.detailLoveStatus.setSelected(false);
                    binding.detailLoveNum.setText(String.valueOf(Integer.parseInt(loveNum) - 1));

                    request.doPost(MergeURLUtil.merge(URLS.CANCEL_LIKE.getUrl(), likeOrCollect),
                            MyHeaders.getHeaders(),
                            new HashMap<>(),
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Log.d("likeResponse", response.body().string());
                                        Message msg = new Message();
                                        msg.what = ItemOptions.CancelLike;
                                        handler.sendMessage(msg);
                                    }
                                }
                            });
                } else { // 喜欢
                    binding.detailLoveStatus.setSelected(true);
                    binding.detailLoveNum.setText(String.valueOf(Integer.parseInt(loveNum) + 1));
                    request.doPost(MergeURLUtil.merge(URLS.LIKE.getUrl(), likeOrCollect),
                            MyHeaders.getHeaders(),
                            new HashMap<>(),
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Message msg = new Message();

                                        msg.what = ItemOptions.Like;
                                        handler.sendMessage(msg);
                                    }
                                }
                            });
                }
            }
        });



        // 评论发送，内容读取
        binding.detailRemarkSubmit.setOnClickListener(new View.OnClickListener() {
            //设置评论发送按钮的点击事件，获取评论内容并发送评论的网络请求，并在请求成功后发送消息给 handler 处理
            @Override
            public void onClick(View view) {
                if (!binding.detailRemarkContent.getText().toString().equals("")) {
                    String content = binding.detailRemarkContent.getText().toString();
                    Map<String, Object> remark = new HashMap<>();
                    remark.put("content", content);
                    remark.put("shareId", item.getId());
                    remark.put("userId", SharedPreferencesUtil.getValue(ItemDetailActivity.this, "id"));
                    remark.put("userName", SharedPreferencesUtil.getValue(ItemDetailActivity.this, "username"));

                    request.doPost(URLS.ADD_COMMENT.getUrl(),
                            MyHeaders.getHeaders(),
                            remark,
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Message msg = new Message();
                                        msg.what = ItemOptions.Comment;
                                        handler.sendMessage(msg);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ItemDetailActivity.this, "评论内容为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Map<String, String> map = new HashMap<>();
        map.put("focusUserId", item.getPUserId());
        map.put("userId", SharedPreferencesUtil.getValue(this, "id"));




        // 监听关注
        binding.detailFollow.setOnClickListener(new View.OnClickListener() {
            //设置关注按钮的点击事件，根据按钮的选中状态执行相应的操作，发送关注或取消关注的网络请求，并在请求成功后发送消息给 handler 处理
            @Override
            public void onClick(View view) {
                if (binding.detailFollow.isSelected()) {
                    binding.detailFollow.setSelected(false);

                    request.doPost(MergeURLUtil.merge(URLS.CANCEL_FOLLOW.getUrl(), map),
                            MyHeaders.getHeaders(),
                            new HashMap<>(),
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Message msg = new Message();
                                        msg.what = ItemOptions.CancelFocus;
                                        handler.sendMessage(msg);
                                    }
                                }
                            });
                } else {
                    binding.detailFollow.setSelected(true);
                    request.doPost(MergeURLUtil.merge(URLS.ADD_FOLLOW.getUrl(), map),
                            MyHeaders.getHeaders(),
                            new HashMap<>(),
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Message msg = new Message();
                                    msg.what = ItemOptions.Focus;
                                    handler.sendMessage(msg);
                                }
                            });
                }
            }
        });
    }

    /**
     * 点击外部事件，关闭键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    KeyBoardUtil.closeKeyboard(v);//软键盘工具类
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        //必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    // 判断 点击事件是否在EditText输入框之外
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    // 初始化数据
    private void initCommentData() {
        Map<String, String> map = new HashMap<>();
        map.put("current", "0");
        map.put("size", String.valueOf(size));
        map.put("shareId", item.getId());

        request.doGet(URLS.GET_COMMENT.getUrl(),
                MyHeaders.getHeaders(),
                map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            CommentBean commentBean = new Gson().fromJson(response.body().string(), CommentBean.class);
                            Message msg = new Message();
                            msg.obj = commentBean;
                            msg.what = ItemOptions.GetComment;
                            handler.sendMessage(msg);
                        } else {
                            Log.d("ERROR", "ERROR initCommentData");
                        }
                    }
                });
    }


}
