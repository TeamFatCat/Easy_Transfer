package com.fatcat.easy_transfer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fatcat.easy_transfer.activity.AboutActivity;
import com.fatcat.easy_transfer.activity.HelpActivity;
import com.fatcat.easy_transfer.activity.ShareActivity;
import com.fatcat.easy_transfer.adapter.SimpleFragmentPagerAdapter;
import com.fatcat.easy_transfer.base.BaseActivity;
import com.fatcat.easy_transfer.base.BaseFragment;
import com.fatcat.easy_transfer.conf.Constants;
import com.fatcat.easy_transfer.control.ISendFiles;
import com.fatcat.easy_transfer.control.TransferService;
import com.fatcat.easy_transfer.fragment.AppFragment;
import com.fatcat.easy_transfer.fragment.FileFragment;
import com.fatcat.easy_transfer.fragment.MusicFragment;
import com.fatcat.easy_transfer.fragment.PictureFragment;
import com.fatcat.easy_transfer.fragment.ReceiveFragment;
import com.fatcat.easy_transfer.net.WifiAdmin;
import com.fatcat.easy_transfer.receiver.WifiApReceiver;
import com.fatcat.easy_transfer.utils.LogUtils;
import com.fatcat.easy_transfer.utils.TransferUtils;
import com.fatcat.easy_transfer.utils.UIUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ISendFiles {

    private Toolbar mToolbar;

    private SimpleFragmentPagerAdapter mPagerAdapter;

    private ViewPager mViewPager;

    private TabLayout mTabLayout;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationView mNavigationView;

    private TextView mUserName;

    private BaseFragment mFragmentSelected;

    private FloatingActionButton mReceiveButton;

    private FloatingActionButton mSendButton;

    private WifiAdmin mWifiAdmin;

    private WifiApReceiver mWifiApReceiver;

    private List<ScanResult> mWifiApList;

    private WifiConfiguration mWifiConfiguration;

    private TransferService.ServerBinder mBinder;

    private ServiceConnection mServiceConnection;

    private boolean isBound;

    private TransferReceiver mReceiver;

    private ProgressDialog mProgressDialog;

    private AlertDialog.Builder mSendDialog;
    private LinearLayout layoutSend;
    private ProgressBar pb;
    private TextView tx_rate;

    private ArrayAdapter adapter;
    private AlertDialog alertDialog = null;

    private ArrayList SpeedList;


    private FloatingActionMenu floatingActionMenu;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bl = msg.getData();
            switch (msg.what) {

                case 0x100: {
                    adapter.notifyDataSetChanged();
                    break;
                }

                case 0x102: {
                    mProgressDialog = new ProgressDialog(MainActivity.this);
                    mProgressDialog.setTitle(bl.getString("file_name", "正在接收："));
                    mProgressDialog.setMax(100);
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.show();
                    break;
                }

                case 0x103: {
                    mProgressDialog.setProgress(bl.getInt("progress"));
                    break;
                }

                case 0x104: {

                    mProgressDialog.dismiss();


                    break;
                }

                case 0x105: {
                    layoutSend = (LinearLayout) MainActivity.this.getLayoutInflater().inflate(R.layout.send_file_progress_layout, null);
                    pb = (ProgressBar) layoutSend.findViewById(R.id.send_progress);
                    tx_rate = (TextView) layoutSend.findViewById(R.id.tv_rate);
                    pb.setProgress(0);
                    tx_rate.setText(getRateString(0) + "/s");
                    mSendDialog = new AlertDialog.Builder(MainActivity.this);
                    mSendDialog.setTitle("发送进度");
                    mSendDialog.setView(layoutSend);
                    mSendDialog.setNegativeButton("取消发送", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBinder.stopSend();
                        }
                    });
                    mSendDialog.setPositiveButton("后台运行", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog = mSendDialog.create();
                    alertDialog.show();
                    break;
                }

                case 0x106: {
                    TextView txv = (TextView) layoutSend.findViewById(R.id.tx_send_message);
                    txv.setText(bl.getString("file_name"));
                    break;
                }

                case 0x107: {
                    int p = bl.getInt("progress");
                    pb.setProgress(p);
                    tx_rate.setText(getRateString(bl.getLong("rate")) + "/s");

                    if (bl.getLong("rate") != 0) {
                        SpeedList.add((bl.getLong("rate") / 1024));
                    }

                    break;
                }

                case 0x108: {
                    if (SpeedList.size() != 0) {
                        long Speed = 0;
                        long SpeedMax = (long) SpeedList.get(0);
                        long SpeedMin = (long) SpeedList.get(0);
                        for (int x = 0; x < SpeedList.size(); x++) {
                            Speed += (long) SpeedList.get(x);
                            if ((long) SpeedList.get(x) > SpeedMax) {
                                SpeedMax = (long) SpeedList.get(x);
                            }
                            if ((long) SpeedList.get(x) < SpeedMin) {
                                SpeedMin = (long) SpeedList.get(x);
                            }

                        }
                        Speed = Speed / SpeedList.size();
                        DecimalFormat df = new DecimalFormat("0.0");
                        LogUtils.s("###-------实际吞吐率为：" + Speed + "KB/S" + "------最快传输速度" + SpeedMax + "KB/S" + "------最慢传输速度" + SpeedMin + "KB/S");
                        LogUtils.s("###-------实际吞吐率为：" + df.format((Speed / 1024.0)) + "MB/S" + "------最快传输速度" + df.format((SpeedMax / 1024.0)) + "MB/S" + "------最慢传输速度" + df.format((SpeedMin / 1024.0)) + "MB/S");
                    } else {
                        LogUtils.s("#------并未统计数据");
                    }
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                        alertDialog = null;
                    }
                    break;
                }

                case 0x109: {
                    mUserName.setText(mBinder.getHostName());
                    break;
                }


            }

        }
    };


    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);


        mPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mReceiveButton = (FloatingActionButton) findViewById(R.id.fab_receive);
        mSendButton = (FloatingActionButton) findViewById(R.id.fab_send);

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.menu_red);

        //设置NavigationView点击事件
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerLayout = mNavigationView.getHeaderView(0);

        mUserName = (TextView) headerLayout.findViewById(R.id.user_name);

    }

    @Override
    protected void initActionBar() {
        super.initActionBar();

        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        //设置标题
        mToolbar.setTitle(R.string.app_name);
        //mToolbar.setTitleTextColor(Color.WHITE);
        //设置actionbar
        setSupportActionBar(mToolbar);

        //设置DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }


    @Override
    protected void initData() {

        super.initData();

        mWifiApReceiver = new WifiApReceiver();

        mWifiAdmin = new WifiAdmin(UIUtils.getContext());

        SpeedList = new ArrayList<Long>();

        isBound = false;

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (TransferService.ServerBinder) service;
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

        mReceiver = new TransferReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.FILE_RECEIVE);
        intentFilter.addAction(Constants.FILE_RECEIVE_PEOGRESS);
        intentFilter.addAction(Constants.FILE_RECEIVE_END);
        intentFilter.addAction(Constants.FILE_SEND);
        intentFilter.addAction(Constants.FILE_SEND_NAME);
        intentFilter.addAction(Constants.FILE_SEND_PEOGRESS);
        intentFilter.addAction(Constants.FILE_SEND_END);
        intentFilter.addAction(Constants.CHANGE_HOST_NAME);
        registerReceiver(mReceiver, intentFilter);

        Intent intent = new Intent(this, TransferService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;

    }

    @Override
    public void finish() {
        if (isBound) {
            unbindService(mServiceConnection);
        }
        unregisterReceiver(mReceiver);
        super.finish();
    }

    @Override
    protected void initListener() {
        super.initListener();

        mReceiveButton.setOnClickListener(new MainClickEvent());
        mSendButton.setOnClickListener(new MainClickEvent());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_help:
                        intent = new Intent(UIUtils.getContext(), HelpActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_info:
                        intent = new Intent(UIUtils.getContext(), AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_share:
                        intent = new Intent(UIUtils.getContext(), ShareActivity.class);
                        startActivity(intent);
                        break;
                }

                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //默认选中tab项   -----文件
        mFragmentSelected = (BaseFragment) mPagerAdapter.getItem(0);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mFragmentSelected = (BaseFragment) mPagerAdapter.getItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edt = new EditText(MainActivity.this);
                edt.setText(mBinder.getHostName());
                edt.setTextColor(Color.BLACK);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("修改名称");
                builder.setView(edt);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edt.getText().toString();
                        if (!name.equals("")) {
                            mBinder.setHostName(name);
                        }
                    }
                });
                builder.create().show();
            }
        });

    }

    @Override
    protected void initFragment() {
        super.initFragment();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (mFragmentSelected instanceof FileFragment) {
            LogUtils.s("FileFragment onKeyUp事件");
            if (FileFragment.onKeyUp(keyCode, event)) {
                return super.onKeyUp(keyCode, event);
            } else {
                return false;
            }
        } else if (mFragmentSelected instanceof AppFragment) {
            if (AppFragment.onKeyUp(keyCode, event)) {
                return super.onKeyUp(keyCode, event);
            } else {
                return false;
            }
        } else if (mFragmentSelected instanceof MusicFragment) {
            if (MusicFragment.onKeyUp(keyCode, event)) {
                return super.onKeyUp(keyCode, event);
            } else {
                return false;
            }
        } else if (mFragmentSelected instanceof PictureFragment) {
            if (PictureFragment.onKeyUp(keyCode, event)) {
                return super.onKeyUp(keyCode, event);
            } else {
                return false;
            }
        } else if (mFragmentSelected instanceof ReceiveFragment) {
            if (ReceiveFragment.onKeyUp(keyCode, event)) {
                return super.onKeyUp(keyCode, event);
            } else {
                return false;
            }
        } else {
            return super.onKeyUp(keyCode, event);
        }

    }

    @Override
    public void sendFileList(final ArrayList<String> fileList) {

        LinearLayout layout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.send_choose_dialog_layout, null);
        ListView listView = (ListView) layout.findViewById(R.id.list_host_choose);
        final ArrayList<String> ip_s = new ArrayList<String>();
        final HashMap<String, String> map = new HashMap<>();

        map.clear();
        ip_s.clear();
        ip_s.add("其他设备");

        adapter = new ArrayAdapter<String>(this, R.layout.choose_host_item_layout, ip_s);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String name = textView.getText().toString();
                String ip = map.get(name);
                if (ip == null && name.equals("其他设备")) {
                    sendFileByIp(fileList);
                } else {
                    int port = Constants.PORT;
                    if (mBinder == null) {
                    }
                    mBinder.sendFileList(ip, port, fileList);
                }
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            }
        });

        scanIP(map, ip_s);
        showChooseDialog(layout, "选择设备");
    }

    private void scanIP(final HashMap<String, String> map, final ArrayList<String> ip_s) {

        final String address = getCurrIP();
        final String ip_top = address.substring(0, address.lastIndexOf('.'));
        for (int i = 1; i < 255; i += 16) {
            final int w = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        int len = 0;
                        byte[] data = new byte[8192];
                        for (int i = w; i < w + 16; i++) {

                            try {
                                String s = ip_top + "." + i;
                                if (s.equals(address)) continue;

                                Socket sc = new Socket();
                                sc.connect(new InetSocketAddress(s, Constants.PORT), 250);
                                DataInputStream dis = new DataInputStream(new BufferedInputStream(sc.getInputStream()));
                                String hostName = s;
                                if ((len = dis.read(data)) != -1) {
                                    hostName = new String(data, 0, len, "GBK");
                                }
                                sc.close();
                                sendMsg(map, ip_s, hostName, s);

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }.start();
        }
    }

    private void showChooseDialog(LinearLayout layout, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setView(layout);
        alertDialog = dialog.create();
        alertDialog.show();
    }

    private String getCurrIP() {
        String ip = "0.0.0.0";

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            ip = TransferUtils.initToIp(wifiManager.getConnectionInfo().getIpAddress());
        } else {
            try {
                Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
                if ((Boolean) method.invoke(wifiManager)) {

                    BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line = br.readLine();
                    while ((line = br.readLine()) != null) {
                        String[] spilt = line.split(" +");
                        if (spilt != null) {
                            ip = spilt[0].substring(0, spilt[0].lastIndexOf('.')) + "." + 1;
                            break;
                        }
                    }
                    br.close();
                }
            } catch (Exception e) {
            }
        }

        return ip;
    }

    private synchronized void sendMsg(HashMap<String, String> map, ArrayList<String> list, String hostName, String s) throws Exception {

        Message msg = new Message();
        msg.what = 0x100;
        map.put(hostName, s);
        list.add(0, hostName);
        mHandler.sendMessage(msg);
        Thread.sleep(1000);

    }

    private void sendFileByIp(final ArrayList<String> file_paths) {

        RelativeLayout layout = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.send_file_dialog_layout, null);
        final EditText ed_ip = (EditText) layout.findViewById(R.id.edt_ip);
        ed_ip.setTextColor(getResources().getColor(R.color.text_color));
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            String address = TransferUtils.initToIp(wifiManager.getConnectionInfo().getIpAddress());
            ed_ip.setText(address);
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("输入地址");
        dialog.setView(layout);

        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ip = ed_ip.getText().toString();
                mBinder.sendFileList(ip, Constants.PORT, file_paths);
            }
        });
        dialog.create().show();
    }

    protected String getRateString(long length) {
        // TODO Auto-generated method stub
        DecimalFormat df = new DecimalFormat("0.0");
        double tem = length;
        tem /= 1024.0;
        if (1024.0 - tem > 0.0000000001) {
            return df.format(tem) + "KB";
        }
        tem /= 1024.0;
        if (1024.0 - tem > 0.0000000001) {
            return df.format(tem) + "MB";
        }
        tem /= 1024.0;
        return df.format(tem) + "GB";

    }

    private void scanWifi(final HashMap<String, String> wifiMap, final ArrayList<String> wifiList) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (!mWifiAdmin.isWifiOpen()) {
                    mWifiAdmin.openWifi();
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
                mWifiAdmin.ScanWifiAp();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                }
                List<ScanResult> list = mWifiAdmin.getWifiScanList();
                wifiList.clear();
                wifiMap.clear();
                for (ScanResult sr : list) {
                    String ssid = sr.SSID;
                    String origin = TransferUtils.restore(ssid);
                    if (origin.startsWith(Constants.WIFI_AP_NAME)) {
                        String name = origin.substring(Constants.WIFI_AP_NAME.length());
                        wifiList.add(name);
                        wifiMap.put(name, ssid);
                    } else {
                    }
                }
                if (wifiList.size() == 0) {
                    wifiList.clear();
                    wifiList.add("重新搜索");
                }
                Message msg = new Message();
                msg.what = 0x100;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    class TransferReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Message msg = new Message();
            msg.setData(intent.getExtras());
            switch (s) {
                case Constants.FILE_RECEIVE: {
                    msg.what = 0x102;
                    break;
                }
                case Constants.FILE_RECEIVE_PEOGRESS: {
                    msg.what = 0x103;
                    break;
                }
                case Constants.FILE_RECEIVE_END: {
                    msg.what = 0x104;
                    break;
                }
                case Constants.FILE_SEND: {
                    msg.what = 0x105;
                    break;
                }
                case Constants.FILE_SEND_NAME: {
                    msg.what = 0x106;
                    break;
                }
                case Constants.FILE_SEND_PEOGRESS: {
                    msg.what = 0x107;
                    break;
                }
                case Constants.FILE_SEND_END: {
                    msg.what = 0x108;
                    break;
                }
                case Constants.CHANGE_HOST_NAME: {
                    msg.what = 0x109;
                    break;
                }
            }
            mHandler.sendMessage(msg);
        }
    }

    class MainClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_receive:

                    floatingActionMenu.close(true);

                    LinearLayout layout = (LinearLayout) MainActivity.this.getLayoutInflater().inflate(R.layout.send_choose_dialog_layout, null);
                    ListView listView = (ListView) layout.findViewById(R.id.list_host_choose);
                    final ArrayList<String> wifiList = new ArrayList<>();
                    final HashMap<String, String> wifiMap = new HashMap<>();
                    wifiList.add("搜索中...");
                    adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.choose_host_item_layout, wifiList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView textView = (TextView) view;
                            String wifiName = textView.getText().toString();

                            if (wifiName.equals("搜索中...")) {
                                return;
                            } else if (wifiName.equals("重新搜索")) {
                                wifiList.clear();
                                wifiList.add("搜索中...");
                                Message msg = new Message();
                                msg.what = 0x100;
                                mHandler.sendMessage(msg);
                                scanWifi(wifiMap, wifiList);
                                return;
                            }

                            String ap = wifiMap.get(wifiName);
                            if (ap != null) {
                                mWifiAdmin.connetToSSID(ap);
                            }
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                                alertDialog = null;
                            }
                        }
                    });
                    scanWifi(wifiMap, wifiList);
                    showChooseDialog(layout, "选择连接");
                    break;
                case R.id.fab_send:

                    floatingActionMenu.close(true);

                    LogUtils.s("热点开启中......");
                    String ap_name = TransferUtils.convert(Constants.WIFI_AP_NAME + android.os.Build.MODEL);
                    mWifiConfiguration = mWifiAdmin.setConfigWifiAp(ap_name, Constants.WIFI_AP_PSWD, WifiAdmin.WPA_PASSWORD);
                    mWifiAdmin.openWifiAp(mWifiConfiguration);

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("已建立连接")
                            .setMessage("将设备连接至：" + android.os.Build.MODEL)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                    break;
                default:
                    break;

            }
        }
    }
}
