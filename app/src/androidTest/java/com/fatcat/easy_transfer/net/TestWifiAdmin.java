package com.fatcat.easy_transfer.net;

import android.test.InstrumentationTestCase;

import com.fatcat.easy_transfer.utils.UIUtils;


/**
 * Created by hasee on 2016/5/17.
 */
public class TestWifiAdmin extends InstrumentationTestCase{

    WifiAdmin wifiAdmin = null;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wifiAdmin = new WifiAdmin(UIUtils.getContext());

    }


    /**
     * WifiLock解锁wifi
     */
    public void testReleaseWifiLock() {
        // 判断是否锁定
        if (wifiAdmin.isHeld()) {
            wifiAdmin.releaseWifiLock();
            assertEquals(false,wifiAdmin.isHeld());
        }
    }

}
