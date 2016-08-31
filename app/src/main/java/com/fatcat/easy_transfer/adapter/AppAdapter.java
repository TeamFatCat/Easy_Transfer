package com.fatcat.easy_transfer.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatcat.easy_transfer.R;

import java.util.ArrayList;

/**
 * Created by FatCat on 2016/7/26.
 */
public class AppAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<PackageInfo> infoList;

    @SuppressWarnings("static-access")
    public AppAdapter(Context context, ArrayList<PackageInfo> infoList) {
        this.context = context;
        this.infoList = infoList;
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
        public ImageView appImg;
        public TextView appTitle;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.app_item, null);
            holder.appImg = (ImageView) convertView.findViewById(R.id.img_app_icon);
            holder.appTitle = (TextView) convertView.findViewById(R.id.txt_app_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.appImg.setImageDrawable(
                this.infoList.get(position).applicationInfo.loadIcon(context.getPackageManager()));

        holder.appTitle.setText(this.infoList.get(position).applicationInfo.loadLabel(context.getPackageManager()));


        return convertView;
    }

}
