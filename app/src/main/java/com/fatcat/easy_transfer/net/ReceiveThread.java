package com.fatcat.easy_transfer.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

/**
 * 接收文件线程
 * @author EsauLu
 *
 */
public class ReceiveThread extends TransferSocket implements Runnable{

    /**
     * 连接套接字
     */
    private Socket mSocket;

    /**
     * 网络输入流
     */
    private DataInputStream mDis;

    /**
     * 网络输出流
     */
    private DataOutputStream mDos;

    /**
     * 文件保存路径
     */
    private String mPath;

    /**
     * 路径分隔符
     */
    public final static String mSplit;

    //静态初始化块
    static {
        //获取当前环境路径分隔符
        Properties p=System.getProperties();
        mSplit=p.getProperty("file.separator");
    }

    /**
     * 构造函数，根据传入的连接套接字创建接收线程
     * @param mSocket	与发送端连接的套接字
     */
    public ReceiveThread(Socket mSocket ,String path ,String host) throws IOException{
        super();
        this.mHostName=host;
        this.mSocket = mSocket;
        this.mPath=path;
        this.mThread=new Thread(this);
        init();
    }

    /**
     * 初始化
     */
    private void init() throws IOException{

        //处理流
        mDis=new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
        mDos=new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));

        isEnd=false;	//标志接收未开始或者没有结束

        //初始化缓冲区
        data=new byte[8192];
        len=0;

        //初始化文件长度
        mTransferLenght=0;
        mFileLenght=0;

        //获取发送端地址
        mIp=mSocket.getInetAddress().getHostAddress();

        //发送主机名
        mDos.write(mHostName.getBytes("GBK"));
        mDos.flush();

        //获取文件长度
        mFileLenght=mDis.readLong();

        //获取文件名
        len=mDis.read(data);
        mFileName=new String(data, 0, len,"GBK");

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        File file=null;
        DataOutputStream fos=null;

        try{
            mDos.write(1);
            mDos.flush();

            //创建文件并打开输出流
            file=createFile();
            fos=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            //启动计算接收速度的线程
            new CountRate().start();

            //传输文件内容
            while((len=mDis.read(data))!=-1){
                fos.write(data, 0, len);
                mTransferLenght+=len;
            }
            fos.flush();

        }catch(IOException e){
            isEnd=true;
        }finally{
            try{
                if(mSocket!=null){
                    mSocket.close();
                }
                if(fos!=null){
                    fos.close();
                }
                isEnd=true;
            }catch(IOException e){

            }
        }

    }

    /**
     * 在文件保存路径下创建一个不重复文件名的文件
     * @return
     */
    private File createFile(){

        //检查路径是否存在
        File file=new File(mPath);
        if(!file.exists()){
            file.mkdir();
        }

        //创建文件
        String sb=mPath+mSplit+mFileName;
        file=new File(sb);

        int i=1;
        int index=sb.lastIndexOf(".");
        while(file.exists()){
            i++;
            if(index<0){
                file=new File(sb+"("+i+")");
            }else{
                file=new File(sb.substring(0, index)+"("+i+")"+sb.substring(index));
            }
        }
        try{
            file.createNewFile();
        }catch(Exception e){

        }
        return file;
    }

    /**
     * 取消接收文件
     */
    public void close(){
        try{
            this.mSocket.close();
        }catch(IOException e){

        }
    }

    /**
     * 返回文件路径
     * @return
     */
    public String getPath() {
        return mPath;
    }

    /**
     * 返回接收速度
     * @return
     */
    public long getReceiveRate() {
        return getRate();
    }

    /**
     * 用于计算接收速度的线程
     * @author EsauLu
     *
     */
    private class CountRate extends Thread{


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

            preLenght=mTransferLenght;
            preTime=System.currentTimeMillis();
            while(mTransferLenght<mFileLenght&&!isEnd){
                currTime=System.currentTimeMillis();
                currLenght=mTransferLenght;
                timeDiff=currTime-preTime;
                if(timeDiff!=0){
                    mCurrRate=(long)((double)(mTransferLenght-preLenght)*1000.0/(double)timeDiff);
                }else{
                    mCurrRate=0;
                }
                preLenght=currLenght;
                preTime=currTime;
                try{
                    Thread.sleep(500);
                }catch(Exception e){
                }
            }
            mCurrRate=0;

        }

    }

}































































































