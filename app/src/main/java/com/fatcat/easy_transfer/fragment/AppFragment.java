package com.fatcat.easy_transfer.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.adapter.AppAdapter;
import com.fatcat.easy_transfer.base.BaseFragment;
import com.fatcat.easy_transfer.utils.LogUtils;
import com.fatcat.easy_transfer.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;

/**
 * Created by FatCat on 2016/7/17.
 */
public class AppFragment extends BaseFragment {

    private ListView listView;
    private PackageManager pManager;
    // 软件信息
    private ArrayList<PackageInfo> packageInfos;

    // apk文件信息，保存包名 路径
    public static HashMap<String, String> apkInfo;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app, container, false);

        listView = (ListView) view.findViewById(R.id.list_app);
        // PackageManager 获取安装信息
        this.pManager = (PackageManager) getContext().getPackageManager();
        this.packageInfos = new ArrayList<PackageInfo>();
        this.getAppInfo(this.packageInfos, this.pManager);
        // 设置 adapter
        this.listView.setAdapter(new AppAdapter(getContext(), this.packageInfos));


        return view;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initListener() {
        super.initListener();

        this.listView.setOnItemClickListener(new ListItemClick());

        this.listView.setOnItemLongClickListener(new ListItemLongClick());


    }


    @SuppressWarnings("static-access")
    private void getAppInfo(ArrayList<PackageInfo> infos,
                            PackageManager packageManager) {
        infos.clear();
        //  获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            // 只保存用户安装软件，非系统应用
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                infos.add(pak);
            }
        }
    }

    /**
     * 列表单击事件
     */
    private class ListItemClick implements OnItemClickListener {

        private static final String SCHEME = "package:";

        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Uri packageURI = Uri.parse(SCHEME + packageInfos.get(position).packageName);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(intent);
        }
    }


    /**
     * 列表长按事件
     */
    private class ListItemLongClick implements OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            PackageInfo app = packageInfos.get(position);
            try {
                String appDir = getActivity().getPackageManager().getApplicationInfo(app.packageName, 0).sourceDir;
                ArrayList<String> file_list=new ArrayList<>();
                file_list.add(appDir);
                mSendFile.sendFileList(file_list);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private  static  long exitTime = 0;
    public static boolean onKeyUp(int keyCode, KeyEvent event) {


        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 &&((System.currentTimeMillis()-exitTime) > 2000)) {

            Toast.makeText(UIUtils.getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();

            return false;
        }else {

            return true;
        }

    }

}
