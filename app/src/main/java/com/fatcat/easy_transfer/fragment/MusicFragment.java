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
import com.fatcat.easy_transfer.adapter.MusicAdapter;
import com.fatcat.easy_transfer.base.BaseFragment;
import com.fatcat.easy_transfer.entity.Mp3Info;
import com.fatcat.easy_transfer.utils.FileUtils;
import com.fatcat.easy_transfer.utils.MusicUtils;
import com.fatcat.easy_transfer.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FatCat on 2016/7/17.
 */
public class MusicFragment extends BaseFragment {

    private ListView listView;

    private ProgressBar progressBar;

    List<Mp3Info> mp3List;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x147: {
                    listView.setAdapter(new MusicAdapter(getContext(), mp3List));
                    progressBar.setVisibility(View.GONE);

                    break;
                }

            }

        }


    };

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        listView = (ListView) view.findViewById(R.id.list_music);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_music);
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
                mp3List = MusicUtils.getMp3Infos(getContext());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mp3List != null) {
                    Message msg = new Message();
                    msg.what = 0x147;
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
                File file = new File(mp3List.get(position).getUrl());
                FileUtils.openFile(file);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> fileList = new ArrayList<String>();
                for (int i = 0; i < MusicUtils.mp3Select.size(); i++) {
                    if (MusicUtils.mp3Select.get(i + "").equals(MusicUtils.MEDIA_CHECKED)) {
                        fileList.add(mp3List.get(i).getUrl());
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
