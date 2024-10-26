package com.example.mymusic;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MediaCursorAdapter extends CursorAdapter {

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    public MediaCursorAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public static class ViewHolder {
        TextView tvTitle;
        TextView tvArtist;
        TextView tvOrder;
        View divider;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = mLayoutInflater.inflate(R.layout.list_item, viewGroup, false);
        if (view != null) {
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tv_title);
            viewHolder.tvArtist = view.findViewById(R.id.tv_artist);
            viewHolder.tvOrder = view.findViewById(R.id.tv_order);
            viewHolder.divider = view.findViewById(R.id.divider);
            view.setTag(viewHolder);
            return view;
        }
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

        String title = cursor.getString(titleIndex);
        String artist = cursor.getString(artistIndex);

        int position = cursor.getPosition();

        if (viewHolder != null) {
            viewHolder.tvTitle.setText(title);
            viewHolder.tvArtist.setText(artist);
            viewHolder.tvOrder.setText(Integer.toString(position + 1));
        }
    }
}

