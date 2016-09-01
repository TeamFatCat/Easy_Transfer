package com.fatcat.easy_transfer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.entity.Mp3Info;
import com.fatcat.easy_transfer.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FatCat on 2016/8/2.
 */
public class MusicAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Mp3Info> infoList;

    public MusicAdapter(Context context, List<Mp3Info> infoList) {
        this.context = context;
        this.infoList = (ArrayList) infoList;
        this.inflater =
                (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //在外面先定义，ViewHolder静态类
    class ViewHolder {
        public ImageView musicImg;
        public TextView musicTitle;
        public TextView musicAlbum;
        public TextView musicDuration;
        public TextView musicSize;
        public CheckBox musicSelect;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.music_item, null);
            holder.musicImg = (ImageView) convertView.findViewById(R.id.iv_music_item);
            holder.musicTitle = (TextView) convertView.findViewById(R.id.tv_music_item);
            holder.musicAlbum = (TextView) convertView.findViewById(R.id.tv_music_album);
            holder.musicDuration = (TextView) convertView.findViewById(R.id.tv_music_duration);
            holder.musicSize = (TextView) convertView.findViewById(R.id.tv_music_size);
            holder.musicSelect = (CheckBox) convertView.findViewById(R.id.rb_music_select);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Bitmap bitmap = MusicUtils.getArtwork(context, infoList.get(position).getId(), infoList.get(position).getAlbumId(), false);

        if (bitmap != null) {
            holder.musicImg.setImageBitmap(bitmap);
        }
        holder.musicDuration.setText(MusicUtils.formatTime(infoList.get(position).getDuration()) + "");

        holder.musicSize.setText(MusicUtils.getMp3Size(infoList.get(position).getSize()));

        holder.musicTitle.setText(infoList.get(position).getTitle());

        holder.musicAlbum.setText(infoList.get(position).getAlbum());


        if (MusicUtils.mp3Select.get(position + "").equals(MusicUtils.MEDIA_NO_CHECKED)) {
            holder.musicSelect.setChecked(false);
        } else {
            holder.musicSelect.setChecked(true);
        }

        final int mPosition = position;

        holder.musicSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MusicUtils.mp3Select.put(mPosition + "", MusicUtils.MEDIA_CHECKED);
                } else {
                    MusicUtils.mp3Select.put(mPosition + "", MusicUtils.MEDIA_NO_CHECKED);

                }
            }
        });

        return convertView;
    }

}