package com.fatcat.easy_transfer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fatcat.easy_transfer.net.WifiAdmin;
import com.fatcat.easy_transfer.utils.LogUtils;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/1511:58
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class WifiApReceiver extends BroadcastReceiver {

    private WifiAdmin mWifiAdmin;



    @Override
    public void onReceive(Context context, Intent intent) {
        mWifiAdmin = new WifiAdmin(context);

        String action = intent.getAction();
        if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
            if (mWifiAdmin.isWifiApOpen()) {
                //Toast.makeText(context, "热点开启", Toast.LENGTH_SHORT).show();
                LogUtils.s("热点开启");

            } else {
                //Toast.makeText(context, "热点关闭", Toast.LENGTH_SHORT).show();
                LogUtils.s("热点关闭");
            }
        }else if("android.net.wifi.WIFI_STATE_CHANGED".equals(action)){
            if (mWifiAdmin.isWifiOpen()){
                //Toast.makeText(context, "wifi开启", Toast.LENGTH_SHORT).show();
                LogUtils.s("wifi开启");

            } else {
                //Toast.makeText(context, "wifi关闭", Toast.LENGTH_SHORT).show();
                LogUtils.s("wifi关闭");
            }
        }

    }

}
