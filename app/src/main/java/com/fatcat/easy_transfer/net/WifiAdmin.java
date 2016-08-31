package com.fatcat.easy_transfer.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.fatcat.easy_transfer.conf.Constants;
import com.fatcat.easy_transfer.utils.LogUtils;
import com.fatcat.easy_transfer.utils.UIUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/1121:54
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class WifiAdmin {

    /**
     * 上下文Context
     */
    private Context mContext;

    /**
     * wifi管理
     */
    private WifiManager mWifiManager;

    /**
     * wifi锁，当传输文件时，将wifi锁住，防止网络的中断导致文件传输失败
     */
    private WifiManager.WifiLock mWifiLock;

    /**
     * mWifiConfig热点配置
     */
    private WifiConfiguration mWifiConfig;

    private List<ScanResult> mHotPot;

    /**
     * wifi锁标志Tag
     */
    private final static String WIFI_LOCK_TAG = "LOCK";

    /**
     * 热点无密码
     */
    public final static int NO_PASSWORD = 1;

    /**
     * 热点以WPA方式加密
     */
    public final static int WPA_PASSWORD = 2;



    /**
     * 构造函数
     *
     * @param context
     */
    public WifiAdmin(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiLock = mWifiManager.createWifiLock(WIFI_LOCK_TAG);
    }


    /**
     * 判断Wifi是否开启
     *
     * @return 返回true, 表示开启，否则关闭
     */
    public boolean isWifiOpen() {
        return mWifiManager.isWifiEnabled();
    }


    /**
     * 打开wifi
     * 判断wifi是否开启，若已开启，则跳过，否则，开启wifi
     */
    public void openWifi() {
        if (isWifiApOpen()) {
            closeWifiAp();
        }
        if (!isWifiOpen()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WIFI
     * 判断wifi是否开启，若已开启，则关闭，否则，跳过
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    /**
     * 获取当前wifi状态
     *
     * @return WIFI_STATE_DISABLING, WIFI_STATE_DISABLED
     * WIFI_STATE_ENABLING,WIFI_STATE_ENABLED
     * WIFI_STATE_UNKNOWN
     */
    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 获取热点网络号
     *
     * @return
     */
    public int getCurrNetworkId() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        return info.getNetworkId();
    }

    /**
     * 扫描热点
     *
     * @return
     */
    public void ScanWifiAp() {
        mWifiManager.startScan();

    }

    /**
     * 返回已配置的wifi
     */
    public List<WifiConfiguration> getWifiConfigList() {
        return mWifiManager.getConfiguredNetworks();
    }

    /**
     * 返回扫描到的热点列表
     */
    public List<ScanResult> getWifiScanList() {
        return mWifiManager.getScanResults();
    }

    /**
     * 返回当前连接的热点
     */
    public WifiInfo getCurrWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }


    /**
     * 返回当前连接wifi的SSID
     */
    public String getSSID() {
        WifiInfo info = mWifiManager.getConnectionInfo();

        if (info == null) {
            return "连接已断开";
        } else {
            return info.getSSID();
        }
    }


    /**
     *SupplicantState
     */


    /**
     * 返回当前连接信号强度
     */
    public int getLevel() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info == null) {
            return 0;
        } else {
            return info.getRssi();
        }
    }

    /**
     * 判断指定SSID的wifi是否已配置
     */
    public WifiConfiguration isExist(String SSID) {

        Iterator<WifiConfiguration> it = mWifiManager.getConfiguredNetworks().iterator();

        WifiConfiguration config;

        //查找已配置的wifi
        while (it.hasNext()) {
            config = (WifiConfiguration) it.next();
            if (config.SSID.equals("\"" + SSID + "\"")) {
                return config;
            }
        }

        return null;
    }

    /**
     * 移除一个已配置的网络
     */
    public void removeWifiConfiguration(WifiConfiguration config) {
        mWifiManager.removeNetwork(config.networkId);
    }

    /**
     * 移除所有已配置的网络
     */
    public void removeAllWifiConfiguration() {
        List<WifiConfiguration> list = getWifiConfigList();
        if (list == null) {
            return;
        }
        for (WifiConfiguration config : list) {
            mWifiManager.removeNetwork(config.networkId);
        }
    }

    /**
     * 添加热点
     */
    public boolean addWifi(WifiConfiguration config) {
        int i = mWifiManager.addNetwork(config);
        if (connectionWifi(i)) {    //连接成功则返回true
            return true;
        }
        mWifiManager.removeNetwork(i);
        return false;
    }

    /**
     * 是否连接到热点
     */
    public boolean isConnect() {
        ConnectivityManager conManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 连接热点
     */
    public boolean connectionWifi(int ID) {
        return mWifiManager.enableNetwork(ID, true);
    }


    /**
     * WifiLock锁定wifi
     */
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    /**
     * WifiLock解锁wifi
     */
    public void releaseWifiLock() {
        // 判断是否锁定
        if (isHeld()) {
            mWifiLock.release();
        }
    }

    /**
     * 判断wifi的锁是否持有
     *
     * @return true，表示wifi被wifilock锁定，否则未锁定
     */
    public boolean isHeld() {
        return mWifiLock.isHeld();
    }

    /**
     * 返回热点是否开启
     *
     * @return true，表示热点已开，否则未开启
     */
    public boolean isWifiApOpen() {
        try {
            return (Boolean) WifiManager.class.getMethod("isWifiApEnabled").invoke(mWifiManager);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 开启热点
     *
     * @return
     */
    public void openWifiAp(WifiConfiguration config) {

        //开启热点前先关闭wifi
        if (isWifiOpen()) {
            closeWifi();
        }

        //如果热点已经开启，关闭热点
        if (isWifiApOpen()) {
            closeWifiAp();
        }

        try {
            Method setApMethod = WifiManager.class.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setApMethod.invoke(mWifiManager, config, true);
        } catch (Exception e) {
        }
    }

    /**
     * 关闭热点
     *
     * @return
     */
    public void closeWifiAp() {
        if (!isWifiApOpen()) {
            return;
        }
        try {
            Method getConfigMethod = WifiManager.class.getMethod("getWifiApConfiguration");
            getConfigMethod.setAccessible(true);    //设置不检查类访问权限
            WifiConfiguration config = (WifiConfiguration) getConfigMethod.invoke(mWifiManager);
            getConfigMethod.setAccessible(false);
            Method setApMethod = WifiManager.class.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setApMethod.invoke(mWifiManager, config, false);
        } catch (Exception e) {
        }
    }


    /**
     * 配置wifi AP热点
     *
     * @param ssid   热点名
     * @param passwd 热点密码 当type为NO_PASSWORD时，可不填
     * @param type   设置是否具有密码 参数有 NO_PASSWORD， WPA_PASSWORD
     * @return
     */
    public WifiConfiguration setConfigWifiAp(String ssid, String passwd, int type) {

        //配置网络信息类
        mWifiConfig = new WifiConfiguration();

        //设置配置网络属性
        mWifiConfig.allowedAuthAlgorithms.clear();
        mWifiConfig.allowedGroupCiphers.clear();
        mWifiConfig.allowedKeyManagement.clear();
        mWifiConfig.allowedPairwiseCiphers.clear();
        mWifiConfig.allowedProtocols.clear();

        mWifiConfig.SSID = ssid;
        Log.i("======================", mWifiConfig.SSID);

        mWifiConfig.wepTxKeyIndex = 0;
        if (type == NO_PASSWORD) {  //没有密码
            //mWifiConfig.wepKeys[0] = "";
            mWifiConfig.allowedKeyManagement.set(0);
            //mWifiConfig.wepTxKeyIndex = 0;
        } else if (type == WPA_PASSWORD) {    //wpa密码
            mWifiConfig.preSharedKey = passwd;
            mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        }

        return mWifiConfig;
    }

    /**
     * 返回热点名SSID
     *
     * @return
     */
    public String getWifiApSSID() {
        String ApSSID = "";
        try {
            Method getConfigMethod = WifiManager.class.getMethod("getWifiApConfiguration");
            getConfigMethod.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) getConfigMethod.invoke(mWifiManager);
            ApSSID = config.SSID;
        } catch (Exception e) {
            return "";
        }
        return ApSSID;
    }

    /**
     * 返回热点AP密码
     *
     * @return
     */
    public String getWifiApPassword() {
        String apPassWord = "";
        try {
            Method getConfigMethod = WifiManager.class.getMethod("getWifiApConfiguration");
            getConfigMethod.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) getConfigMethod.invoke(mWifiManager);
            apPassWord = config.preSharedKey;
        } catch (Exception e) {
            return "";
        }
        return apPassWord;
    }

    /**
     * 自动连接热点
     *
     * @param wifiList
     */
    public void autoConnect(List<ScanResult> wifiList) {
        mHotPot = new ArrayList<ScanResult>();
        for (ScanResult result : wifiList) {
            //System.out.println(result.SSID);
            LogUtils.s("当前在列表内的ssid：" + result.SSID);
            if ((result.SSID).contains(Constants.WIFI_AP_NAME))
                mHotPot.add(result);
        }
        connectToHotPot();


    }

    /**
     * 配置热点 连接热点
     */
    public void connectToHotPot() {
        if (mHotPot == null || mHotPot.size() == 0)
            return;
        WifiConfiguration config = this.setConfigWifiAp("\"" + mHotPot.get(0).SSID + "\"", "\"" + Constants.WIFI_AP_PSWD + "\"", WifiAdmin.WPA_PASSWORD);
        LogUtils.s(" 热点名: " + mHotPot.get(0));
        if (this.addWifi(config)) {
            Toast.makeText(UIUtils.getContext(), "连接成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UIUtils.getContext(), "连接失败！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 指定SSID连接
     */
    public void connetToSSID(String ssid){
        WifiConfiguration config = this.setConfigWifiAp("\"" + ssid + "\"", "\"" + Constants.WIFI_AP_PSWD + "\"", WifiAdmin.WPA_PASSWORD);
        if (this.addWifi(config)) {
            Toast.makeText(UIUtils.getContext(), "连接成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UIUtils.getContext(), "连接失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }


}
