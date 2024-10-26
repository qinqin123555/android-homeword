package com.example.photosharing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photosharing.R;

import com.example.photosharing.activity.ItemOptions;
import com.example.photosharing.entity.ShareBean;

import com.example.photosharing.net.MyHeaders;
import com.example.photosharing.net.MyRequest;
import com.example.photosharing.entity.MyResponse;
import com.example.photosharing.net.URLS;
import com.example.photosharing.utils.MergeURLUtil;
import com.example.photosharing.utils.SharedPreferencesUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StaggeredGridAdapter extends RecyclerView.Adapter<StaggeredGridAdapter.ItemViewHolder> {

    private final Context context;

    private boolean isMine;

    private List<ShareBean.DataBean.RecordsBean> shareList = new ArrayList<>();

    public OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    private MyRequest request = new MyRequest();

    private Map<String, String> map = new HashMap<>();

    private boolean isScrolling = false;


    //点赞toast消息显示
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            getItemInfo();
            switch (msg.what) {
                case ItemOptions.CancelLike:
                    Toast.makeText(context, "取消点赞", Toast.LENGTH_SHORT).show();
                    break;
                case ItemOptions.Like:
                    Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    //向服务器发送一个 HTTP GET 请求，获取与某个项目（Item）相关的信息
    protected void getItemInfo() {
        final String[] likeId = new String[1];//用于存储获取到的点赞 ID
        //发送一个 GET 请求，请求的 URL 是 URLS.ItemInfo.getUrl()，请求头是 MyHeaders.getHeaders()，请求参数是 map
        request.doGet(URLS.ItemInfo.getUrl(),
                MyHeaders.getHeaders(),
                map,
                new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        //从响应中提取出点赞 ID，并将其存储到 likeId 数组中，最后将 likeId 添加到 map 参数中
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                likeId[0] = jsonObject.getJSONObject("data").getString("likeId");

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            map.put("likeId", likeId[0]);
                        }
                    }
                });
    }


    //创建关联适配器
    public StaggeredGridAdapter(Context context, List<ShareBean.DataBean.RecordsBean> shareList) {
        this.context = context;//上下文内容
        this.shareList = shareList;//分享数据列表
        this.isMine = false;
    }

    //这个方法可在类外部改变 isMine 的状态，以控制适配器的行为
    // 如当调用 setMine(true) 时，表示数据是用户自己的分享；当调用 setMine(false) 时，表示数据不是用户自己的分享
    public void setMine(boolean mine) {
        isMine = mine;
    }


    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);

        void onLongItemClick(int position);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    // 创建列表组件
    @NonNull
    @Override
    public StaggeredGridAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null);
        return new ItemViewHolder(view, this);
    }


    //在适配器中绑定数据到视图项
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (shareList.size() > 0) {
            ShareBean.DataBean.RecordsBean share = shareList.get(position);
            if (share.getImageUrlList().size() > 0)
                Glide.with(context)
                        .load(share.getImageUrlList().get(0))
                        .placeholder(R.drawable.no_picture)
                        .error(R.drawable.no_picture)
                        .into(holder.ivPicture);
            //将分享数据中的标题、作者、喜欢按钮的状态和喜欢数量设置给对应的视图组件
            holder.tvTitle.setText(share.getTitle());
            holder.tvAuthor.setText(share.getUsername());
            holder.ivLove.setSelected(share.isHasLike());
            holder.tvLike.setText(share.getLikeNum());

            holder.ivPicture.setAdjustViewBounds(true);
            holder.ivPicture.setMaxHeight(1000);

            //建一个Map（映射）对象 map，并向其中存储了分享ID、喜欢ID和用户ID等键值对信息
            map.put("shareId", share.getId());
            map.put("likeId", share.getId());
            map.put("userId", SharedPreferencesUtil.getValue(context, "id"));

            getItemInfo();

            //如果是用户自己的分享，这行代码将设置 holder.ivClose 的可见性为可见，即显示删除按钮
            if (this.isMine) {
                holder.ivClose.setVisibility(View.VISIBLE);

                //设置点击事件监听器，当点击删除按钮时，会弹出一个对话框询问是否要删除分享
                holder.ivClose.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(context)
                                .setTitle("删除操作")
                                .setMessage("是否删除？")
                                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        request.doPost(MergeURLUtil.merge(URLS.DELETE_SHARE.getUrl(), map),
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
                                                            System.out.println(response);
                                                            Message msg = new Message();
                                                            msg.what = 4;
                                                            handler.sendMessage(msg);
                                                        }
                                                    }
                                                });

                                    }
                                })
                                .show();
                    }
                });
            }

            //检查是否存在点击监听器 onRecyclerViewItemClickListener
            //如果存在，它会为视图项设置点击和长点击监听器，用于处理相应的点击事件和长点击事件
            if (onRecyclerViewItemClickListener != null) {
                //为 holder.view 设置点击事件和长按事件的监听器
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRecyclerViewItemClickListener.onItemClick(position);
                    }
                });
                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onRecyclerViewItemClickListener.onLongItemClick(position);
                        return true;
                    }
                });
            }


            // holder.ivLove 设置点击事件监听器，用于处理用户点击喜欢按钮的事件
            holder.ivLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String loveNum = (String) holder.tvLike.getText();
                    if (holder.ivLove.isSelected()) { // 取消喜欢
                        holder.ivLove.setSelected(false);
                        holder.tvLike.setText(String.valueOf(Integer.parseInt(loveNum) - 1));
                        share.setHasLike(false);
                        share.setLikeNum(String.valueOf(Integer.parseInt(loveNum) - 1));

                        request.doPost(MergeURLUtil.merge(URLS.CANCEL_LIKE.getUrl(), map),
                                MyHeaders.getHeaders(),
                                new HashMap<>(),
                                new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }


                                    //回调方法，用于处理HTTP请求的响应
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            //创建一个msg 消息对象，用于在Android中进行线程间通信
                                            Message msg = new Message();
                                            msg.what = ItemOptions.CancelLike;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });


                    } else { // 点赞+1
                        holder.ivLove.setSelected(true);
                        if (loveNum.isEmpty()) {
                            loveNum = "0";
                        }
                        holder.tvLike.setText(String.valueOf(Integer.parseInt(loveNum) + 1));
                        share.setHasLike(true);
                        share.setLikeNum(String.valueOf(Integer.parseInt(loveNum) + 1));

                        request.doPost(MergeURLUtil.merge(URLS.LIKE.getUrl(), map),
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
                                            MyResponse MyResponse = new Gson().fromJson(response.body().string(), MyResponse.class);
                                            Message msg = new Message();
                                            msg.what = ItemOptions.Like;
                                            //handle将消息发送给处理程序，以便在主线程中处理点赞操作的相关UI更新。
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });
                    }
                }
            });

            //为整个视图项 holder.itemView 设置动画效果，通过加载一个动画资源文件 R.anim.recycler_animation 来实现动画效果
            holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_animation));
        }
    }


    // 返回列表总数
    @Override
    public int getItemCount() {
        return shareList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPicture;

        ImageView ivClose;

        TextView tvTitle;

        ImageView ivAvatar;

        TextView tvAuthor;

        ImageView ivLove;

        TextView tvLike;

        View view;

        private RecyclerView.Adapter adapter;


        //封装Item中图片、标题、作者、喜欢按钮等各类视图组件数据，进行绑定数据操作
        public ItemViewHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
            super(itemView);
            this.ivPicture = itemView.findViewById(R.id.picture);
            this.tvTitle = itemView.findViewById(R.id.title);
            this.ivAvatar = itemView.findViewById(R.id.avatar);
            this.tvAuthor = itemView.findViewById(R.id.author);
            this.ivLove = itemView.findViewById(R.id.love);
            this.tvLike = itemView.findViewById(R.id.like_num);
            this.view = itemView.findViewById(R.id.container);
            this.ivClose = itemView.findViewById(R.id.post_del);
            this.adapter = adapter;
        }
    }
}
