package com.fatcat.easy_transfer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.adapter.VideoAdapter;
import com.fatcat.easy_transfer.base.BaseFragment;
import com.fatcat.easy_transfer.entity.VideoInfo;
import com.fatcat.easy_transfer.utils.FileUtils;
import com.fatcat.easy_transfer.utils.LogUtils;
import com.fatcat.easy_transfer.utils.UIUtils;
import com.fatcat.easy_transfer.utils.VideoUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by FatCat on 2016/7/17.
 */
public class VideoFragment extends BaseFragment {

    private ListView listView;

    private ProgressBar progressBar;

    private ArrayList<VideoInfo> mVideoInfoList;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x177: {
                    listView.setAdapter(new VideoAdapter(getContext(), mVideoInfoList));
                    progressBar.setVisibility(View.GONE);
                    break;
                }
            }

        }


    };

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        listView = (ListView) view.findViewById(R.id.list_video);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_video);
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
                mVideoInfoList = (ArrayList<VideoInfo>) VideoUtils.getVideoInfos(getContext());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mVideoInfoList != null) {
                    Message msg = new Message();
                    msg.what = 0x177;
                    mHandler.sendMessage(msg);
                }

            }
        }).start();
    }

    @Override
    public void initListener() {
        super.initListener();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File file = new File(mVideoInfoList.get(position).getPath());
                FileUtils.openFile(file);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> fileList = new ArrayList<String>();
                for (int i = 0; i < VideoUtils.videoSelect.size(); i++) {
                    if (VideoUtils.videoSelect.get(i + "").equals(VideoUtils.MEDIA_CHECKED)) {
                        fileList.add(mVideoInfoList.get(i).getPath());
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
