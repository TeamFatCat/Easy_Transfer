package com.fatcat.easy_transfer.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * 发送文件线程
 *
 * @author EsauLu
 */
public class SendThread extends TransferSocket implements Runnable {

    /**
     * 连接套接字
     */
    private Socket mSocket;

    /**
     * 处理流
     */
    private DataInputStream mDis;    //从网络接收
    private DataOutputStream mDos;    //发送到网络
    private DataInputStream mFis;    //读取文件

    /**
     * 要发送的文件
     */
    private File mFile;

    /**
     * @param iP
     * @param port
     */
    public SendThread(String iP, int port, File file) throws NoSuchFieldException {
        super();
        this.mIp = iP;
        this.mPort = port;
        this.mFile = file;
        if (!file.exists()) {
            throw new NoSuchFieldException("文件不存在");
        }
        init();
    }

    private void init() {
        mCurrRate = 0;
        mTransferLenght = 0;
        mFileLenght = mFile.length();
        mFileName = mFile.getName();
        isEnd = false;
        mThread = new Thread(this);

        //初始化缓冲区
        data = new byte[8192];
        len = 0;

    }

    private void initRun() throws IOException {

        //建立连接，超时10秒连接失败
        mSocket = new Socket();
        mSocket.connect(new InetSocketAddress(mIp, mPort), 5000);

        //获取处理流
        mDis = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
        mDos = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
        mFis = new DataInputStream(new BufferedInputStream(new FileInputStream(mFile)));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        try {

            initRun();

            //获取主机名
            len = mDis.read(data);
            mHostName = new String(data, 0, len, "GBK");

            mDos.writeLong(mFileLenght);
            mDos.flush();
            mDos.write(mFileName.getBytes("GBK"));
            mDos.flush();
            mDis.read();

            //计算发送速度
            new CountRate().start();

            //发送文件内容
            len = 0;
            while ((len = mFis.read(data)) != -1) {
                mDos.write(data, 0, len);
                mTransferLenght += len;//记录已发送的长度
            }
            mDos.flush();

        } catch (IOException e) {
            isEnd = true;
            mCurrRate = 0;
        } finally {
            try {
                if (mSocket != null) {
                    mSocket.close();
                }
                if (mFis != null) {
                    mFis.close();
                }
                isEnd = true;
            } catch (IOException e) {

            }
        }

    }

    /**
     * 取消接收文件
     */
    public void close() {
        try {
            this.mSocket.close();
        } catch (IOException e) {
        }
    }

    /**
     * 返回发送速度
     *
     * @return
     */
    public long getSendRate() {
        return getRate();
    }

    /**
     * 用于计算发送速度的线程
     *
     * @author EsauLu
     */
    private class CountRate extends Thread {

        public CountRate() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            long preTime;
            long currTime;
            long timeDiff;
            long preLenght;
            long currLenght;

            preLenght = mTransferLenght;
            preTime = System.currentTimeMillis();
            while (mTransferLenght < mFileLenght && !isEnd) {
                currTime = System.currentTimeMillis();
                currLenght = mTransferLenght;
                timeDiff = currTime - preTime;
                if (timeDiff != 0) {
                    mCurrRate = (long) ((double) (mTransferLenght - preLenght) * 1000.0 / (double) timeDiff);
                } else {
                    mCurrRate = 0;
                }
                preLenght = currLenght;
                preTime = currTime;
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
            mCurrRate = 0;

        }

    }

}













































