package com.fatcat.easy_transfer.net;

public class TransferBase {


    /**
     * 指定ip
     */
    protected String mIp;

    /**
     * 端口号
     */
    protected int mPort;

    /**
     * 主机名
     */
    protected String mHostName;

    /**
     * 线程对象
     */
    protected Thread mThread;

    public TransferBase() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 启动线程
     */
    public void start() {
        mThread.start();
    }

    /**
     * 线程是否活动态
     * @return
     */
    public boolean isAlive(){
        return mThread.isAlive();
    }

    /**
     * 返回接收端IP
     * @return
     */
    public String getIP() {
        return mIp;
    }

    /**
     * 返回端口号
     * @return
     */
    public int getPort() {
        return mPort;
    }

    /**
     * 设置主机名
     * @param hostName 主机名
     */
    public void setHostName(String hostName) {
        this.mHostName = hostName;
    }


    /**
     * 返回主机名
     * @return 主机名
     */
    public String getHostName() {
        return mHostName;
    }

}




































