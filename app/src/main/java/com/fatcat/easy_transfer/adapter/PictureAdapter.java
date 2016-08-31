package com.fatcat.easy_transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.entity.ImageInfo;
import com.fatcat.easy_transfer.utils.PictureUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by FatCat on 2016/7/30.
 */
public class PictureAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<ImageInfo> mDatas;

    private LayoutInflater inflater;

    public PictureAdapter(Context context, ArrayList<ImageInfo> Datas) {
        this.context = context;
        this.mDatas = Datas;
        this.inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {
        public ImageView img;
        public CheckBox cx;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.picture_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.iv_picture);
            holder.cx = (CheckBox) convertView.findViewById(R.id.rb_pictrue_select);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File picture = new File(mDatas.get(position).getPath());

        Glide.with(context).load(picture).into(holder.img);

        holder.img.setScaleType(ImageView.ScaleType.FIT_XY);

        if (PictureUtils.pictureSelect.get(position + "").equals(PictureUtils.MEDIA_NO_CHECKED)) {
            holder.cx.setChecked(false);
        } else {
            holder.cx.setChecked(true);
        }

        final int mPosition = position;

        holder.cx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PictureUtils.pictureSelect.put(mPosition + "", PictureUtils.MEDIA_CHECKED);
                    //Toast.makeText(context, "选中", Toast.LENGTH_LONG).show();

                } else {
                    PictureUtils.pictureSelect.put(mPosition + "", PictureUtils.MEDIA_NO_CHECKED);
                    //Toast.makeText(context, "没选中", Toast.LENGTH_LONG).show();

                }
            }
        });

        return convertView;
    }
}
