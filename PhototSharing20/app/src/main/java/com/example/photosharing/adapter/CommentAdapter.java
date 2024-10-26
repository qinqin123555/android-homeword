package com.example.photosharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosharing.R;
import com.example.photosharing.entity.CommentBean;

import java.util.ArrayList;
import java.util.List;

//将评论数据绑定到 RecyclerView 中的视图项上
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final Context context;

    private List<CommentBean.DataBean.RecordsBean> list = new ArrayList<>();

    //两个参数：context（上下文对象）和 list（评论数据列表）
    public CommentAdapter(Context context, List<CommentBean.DataBean.RecordsBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_com, null);
        return new CommentViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if (list.size() > 0) {
            CommentBean.DataBean.RecordsBean comment = list.get(position);
            holder.tv_name.setText(comment.getUserName());
            holder.tv_time.setText(comment.getCreateTime().substring(0, 10));
            holder.tv_content.setText(comment.getContent());
        }

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_animation));
    }

    //返回评论数据列表 list 的大小，即列表中的评论项数量
    @Override
    public int getItemCount() {
        return list.size();
    }

    //包含了评论项的各个视图组件（TextView）的引用
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;

        TextView tv_time;

        TextView tv_content;

        private RecyclerView.Adapter adapter;


        public CommentViewHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.remark_username);
            this.tv_time = itemView.findViewById(R.id.remark_date);
            this.tv_content = itemView.findViewById(R.id.remark_content);
            this.adapter = adapter;
        }
    }
}
