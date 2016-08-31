package com.fatcat.easy_transfer.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * @author FatCat
 * @version $Rev$
 * @time 2016/4/721:58
 * @des 定义一个全局的盒子.里面放置的对象, 属性, 方法都是全局可以调用
 * @updateAuthor $FatCat$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class BaseApplication extends Application {
    private static Context mContext;
    private static Thread mMainThread;
    private static long mMainTreadId;
    private static Looper mMainLooper;
    private static Handler mHandler;

    /**
     * 获取Handler
     * @return
     */
    public static Handler getHandler() {
        return mHandler;
    }

    /**
     * 获取上下文
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 获取主线程
     * @return
     */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /**
     * 获取主线程id
     * @return
     */
    public static long getMainTreadId() {
        return mMainTreadId;
    }

    /**
     * 获取主线程轮询器
     * @return
     */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    @Override
    public void onCreate() {// 程序的入口
        // 初始化化一些.常用属性.然后放到盒子里面来
        // 上下文
        mContext = getApplicationContext();

        // 主线程
        mMainThread = Thread.currentThread();

        // 主线程Id
        mMainTreadId = android.os.Process.myTid();

        // tid thread
        // uid user
        // pid process
        // 主线程Looper对象
        mMainLooper = getMainLooper();

        // 定义一个handler
        mHandler = new Handler();

        super.onCreate();
    }
}
