package com.example.code10;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MediaCursorAdapter extends CursorAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public MediaCursorAdapter(Context context){
        super(context,null,0);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View itemView = mLayoutInflater.inflate(R.layout.list_item,viewGroup,false);
        if (itemView != null){
            ViewHolder vh = new ViewHolder();
            vh.tvTitle = itemView.findViewById(R.id.tv_title);
            vh.tvArtist = itemView.findViewById(R.id.tv_artist);
            vh.tvOrder = itemView.findViewById(R.id.tv_order);
            vh.divider = itemView.findViewById(R.id.divider);
            itemView.setTag(vh);

            return itemView;
        }
        return null;
    }

    public class ViewHolder{
        TextView tvTitle;
        TextView tvArtist;
        TextView tvOrder;
        View divider;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();

        int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

        String title = cursor.getString(titleIndex);
        String artist = cursor.getString(artistIndex);

        int position = cursor.getPosition();
        vh.tvTitle.setText(title);
        vh.tvArtist.setText(artist);
        vh.tvOrder.setText(Integer.toString(position+1));

    }
}
