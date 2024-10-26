package com.example.code6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class NewAdapter extends ArrayAdapter<News> {
    private List<News> mNewsData;
    private Context mContext;
    private int resourceId;

    public NewAdapter(@NonNull Context context, int resource, @NonNull List<News> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.mNewsData=objects;
        this.resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        View view;
        view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvAuthor = view.findViewById(R.id.tv_subtitle);
        ImageView ivImage = view.findViewById(R.id.iv_image);

        tvTitle.setText(news.getmTitle());
        tvAuthor.setText(news.getmAuthor());
        ivImage.setImageResource(news.getmImageId());

        return view;
    }
}
