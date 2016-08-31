package com.fatcat.easy_transfer.net;

public class TransferSocket extends TransferBase{

    /**
     * 文件名
     */
    protected String mFileName;

    /**
     * 已发送长度
     */
    protected long mTransferLenght;

    /**
     * 文件总长度
     */
    protected long mFileLenght;

    /**
     * 发送速度
     */
    protected long mCurrRate;

    /**
     * 发送是否结束的标志，发送完成或者被接收端拒绝后都属于发送结束
     */
    protected boolean isEnd;

    /**
     * 缓冲区
     */
    protected byte[] data;
    protected int len;

    public TransferSocket() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 返回文件长度
     * @return
     */
    public long getFileLenght() {
        return mFileLenght;
    }


    /**
     * 返回但前发送是否结束
     * @return
     */
    public boolean isEnd() {
        if(isEnd&&mCurrRate==0){
            return true;
        }
        return false;
    }

    /**
     * 返回文件名
     * @return
     */
    public String getFileName() {
        return mFileName;
    }

    /**
     * 返回一个0~1之间的浮点数,表示发送进度
     * @return
     */
    public double getProgress(){
        if(mFileLenght==0){
            return 0.0;
        }
        return (double)mTransferLenght/(double)mFileLenght;
    }

    /**
     * 返回发送速度
     * @return
     */
    public long getRate() {
        return mCurrRate;
    }

}

