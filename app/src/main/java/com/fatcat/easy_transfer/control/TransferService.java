package com.fatcat.easy_transfer.control;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fatcat.easy_transfer.conf.Constants;
import com.fatcat.easy_transfer.net.ReceiveThread;
import com.fatcat.easy_transfer.net.SendThread;
import com.fatcat.easy_transfer.net.ServerThread;
import com.fatcat.easy_transfer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by EsauLu on 2016/8/9.
 */
public class TransferService extends Service {

    private Binder mBinder;
    private ServerThread mServerThread;
    private SendThread mSendThread;
    private File mFileSavePath;

    //是否正在传输
    private boolean isTransfer;
    private MyReceiveOper mReceiveOper;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mServerThread.close();
        super.onDestroy();
    }

    private void init() {

        isTransfer = false;

        mBinder = new ServerBinder();

        mFileSavePath = new File(FileUtils.getFilePath() + "/" + "transferDownload");

        mServerThread = ServerThread.createServerThread(Constants.PORT, mFileSavePath.getAbsolutePath(), getLastHostName());

        mReceiveOper = new MyReceiveOper();

        mServerThread.registerReceiveOper(mReceiveOper);

        mServerThread.start();

        Intent intent = new Intent();
        intent.setAction(Constants.CHANGE_HOST_NAME);
        sendBroadcast(intent);

    }

    //获取上次设置的主机名
    private String getLastHostName() {
        String hostName = android.os.Build.MODEL + "-" + android.os.Build.VERSION.RELEASE;
        try {
            SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.PROPERTIES_FILE_NAME, Context.MODE_PRIVATE);
            hostName = sp.getString("host_name", android.os.Build.MODEL + "-" + android.os.Build.VERSION.RELEASE);
        } catch (Exception e) {
        }
        return hostName;
    }

    public class MyReceiveOper implements ServerThread.ReceiveOper {
        @Override
        public void operate(final ReceiveThread receive) {
            //若正在接收文件，则等待
            while (isTransfer) {
                continue;
            }
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    //设置正在文件接收
                    isTransfer = true;

                    Intent intent = new Intent();
                    intent.setAction(Constants.FILE_RECEIVE);
                    intent.putExtra("file_name", receive.getFileName());
                    sendBroadcast(intent);

                    intent.setAction(Constants.FILE_RECEIVE_PEOGRESS);
                    receive.start();
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
                        intent.putExtra("progress", (int) (receive.getProgress() * 100.0));
                        intent.putExtra("rate", receive.getRate());
                        sendBroadcast(intent);
                        if (receive.isEnd()) {
                            break;
                        }
                    }

                    intent.setAction(Constants.FILE_RECEIVE_END);
                    intent.putExtra("rate", 0);
                    if ((int) (receive.getProgress() * 100.0) == 100) {
                        intent.putExtra("progress", 100);
                    }
                    sendBroadcast(intent);

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                    //设置不在接收文件
                    isTransfer = false;
                }
            }.start();
        }
    }

    public class ServerBinder extends Binder {

        public void sendFileList(final String ip, final int port, final ArrayList<String> file_paths) {
            if (isTransfer) {
                return;
            }

            try {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        isTransfer = true;

                        Intent intent = new Intent();
                        intent.setAction(Constants.FILE_SEND);
                        sendBroadcast(intent);

                        for (String path : file_paths) {
                            File file = new File(path);
                            if (!file.exists()) {
                                continue;
                            }
                            try {
                                mSendThread = new SendThread(ip, port, file);
                                intent.setAction(Constants.FILE_SEND_NAME);
                                intent.putExtra("file_name", file.getName());
                                sendBroadcast(intent);
                                mSendThread.start();
                            } catch (Exception e) {
                                continue;
                            }
                            intent.setAction(Constants.FILE_SEND_PEOGRESS);
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("progress", (int) (mSendThread.getProgress() * 100.0));
                                long r = mSendThread.getRate();
                                intent.putExtra("rate", r);
                                sendBroadcast(intent);
                                if (mSendThread.isEnd()) {
                                    break;
                                }
                            }

                            if ((mSendThread.isEnd() && (int) (mSendThread.getProgress() * 100.0) != 100)) {
                                break;
                            }

                        }
                        intent.setAction(Constants.FILE_SEND_END);
                        sendBroadcast(intent);
                        mSendThread = null;

                        isTransfer = false;

                    }
                }.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getHostName() {
            return mServerThread.getHostName();
        }

        public void setHostName(String hostName) {
            mServerThread.setHostName(hostName);

            try {
                SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.PROPERTIES_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = sp.edit();
                edt.putString("host_name", hostName);
                edt.commit();
            } catch (Exception e) {
            }

            Intent intent = new Intent();
            intent.setAction(Constants.CHANGE_HOST_NAME);
            sendBroadcast(intent);
        }

        public void stopSend() {
            if (mSendThread != null) {
                mSendThread.close();
            }
        }
    }

}
