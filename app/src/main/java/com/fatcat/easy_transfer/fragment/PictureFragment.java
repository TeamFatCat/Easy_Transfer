package com.fatcat.easy_transfer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.activity.PictureActivity;
import com.fatcat.easy_transfer.adapter.PictureAdapter;
import com.fatcat.easy_transfer.base.BaseFragment;
import com.fatcat.easy_transfer.entity.ImageInfo;
import com.fatcat.easy_transfer.utils.PictureUtils;
import com.fatcat.easy_transfer.utils.UIUtils;
import java.util.ArrayList;

/**
 * Created by FatCat on 2016/7/17.
 */
public class PictureFragment extends BaseFragment {


    private ProgressBar progressBar;

    private GridView mGridView;

    private ArrayList<ImageInfo> mImgList;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x137: {
                    mGridView.setAdapter(new PictureAdapter(getContext(), mImgList));

                    progressBar.setVisibility(View.GONE);
                    break;
                }

            }

        }


    };


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        mGridView = (GridView) view.findViewById(R.id.gv_picture);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);

        return view;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void initData() {
        super.initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mImgList = (ArrayList<ImageInfo>) PictureUtils.getPictureInfos(getContext());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mImgList != null) {
                    Message msg = new Message();
                    msg.what = 0x137;
                    mHandler.sendMessage(msg);
                }

            }
        }).start();

    }

    @Override
    public void initListener() {
        super.initListener();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                //用intent.putExtra(String name, String value);来传递参数。
                intent.putExtra("PicturePath", mImgList.get(position).getPath());
                intent.setClass(getContext(), PictureActivity.class);
                startActivity(intent);
            }
        });


        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> fileList = new ArrayList<String>();
                for (int i = 0; i < PictureUtils.pictureSelect.size(); i++) {
                    if (PictureUtils.pictureSelect.get(i + "").equals(PictureUtils.MEDIA_CHECKED)) {
                        fileList.add(mImgList.get(i).getPath());
                    }
                }
                mSendFile.sendFileList(fileList);   //发送文件
                return true;
            }
        });

    }

    private static long exitTime = 0;

    public static boolean onKeyUp(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && ((System.currentTimeMillis() - exitTime) > 2000)) {

            Toast.makeText(UIUtils.getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();

            return false;
        } else {

            return true;
        }

    }
}












































