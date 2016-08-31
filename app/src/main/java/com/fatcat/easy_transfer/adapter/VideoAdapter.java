package com.fatcat.easy_transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.entity.VideoInfo;
import com.fatcat.easy_transfer.utils.MusicUtils;
import com.fatcat.easy_transfer.utils.VideoUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by FatCat on 2016/8/5.
 */
public class VideoAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<VideoInfo> infoList;

    public VideoAdapter(Context context, ArrayList<VideoInfo> infoList) {
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
        public ImageView videoImg;
        public TextView videoTitle;
        public TextView videoSize;
        public CheckBox videoSelect;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.video_item, null);
            holder.videoImg = (ImageView) convertView.findViewById(R.id.iv_video_item);
            holder.videoTitle = (TextView) convertView.findViewById(R.id.tv_video_item);
            holder.videoSelect = (CheckBox) convertView.findViewById(R.id.cx_video_select);
            holder.videoSize = (TextView) convertView.findViewById(R.id.tv_video_size);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File videoImg =new File(infoList.get(position).getPath());

        Glide.with(context).load(videoImg).into(holder.videoImg);

        holder.videoTitle.setText(infoList.get(position).getTitle());

        holder.videoSize.setText(VideoUtils.getVideoSize(infoList.get(position).getSize()));

        final int mPosition = position;

        holder.videoSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    VideoUtils.videoSelect.put(mPosition + "", VideoUtils.MEDIA_CHECKED);
                } else {
                    VideoUtils.videoSelect.put(mPosition + "", VideoUtils.MEDIA_NO_CHECKED);
                }
            }
        });

        return convertView;
    }

}