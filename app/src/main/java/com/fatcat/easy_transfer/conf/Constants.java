package com.fatcat.easy_transfer.conf;


import com.fatcat.easy_transfer.utils.LogUtils;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/722:14
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class Constants {
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//LEVEL_ALL,显示所有的日子,OFF:关闭日志的显示

    public static final int PORT = 6602;//应用测试传输端口

    public static final String TEST_IP = "192.168.56.1";//应用测试传输IP

    public static final String WIFI_AP_NAME = "MyWifiApDemo";
    public static final String WIFI_AP_PSWD = "12345678";
    public static final String PROPERTIES_FILE_NAME = "properties";
    public static final String CHANGE_HOST_NAME = "com.fatcat.easy_transfer.MainActivity.Broadcast.CHANGE_NAME";

    public static final String FILE_RECEIVE="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_RECEIVE";
    public static final String FILE_RECEIVE_PEOGRESS="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_RECEIVE_PEOGRESS";
    public static final String FILE_RECEIVE_END="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_RECEIVE_END";

    public static final String FILE_SEND="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_SEND";
    public static final String FILE_SEND_NAME="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_SEND_NAME";
    public static final String FILE_SEND_PEOGRESS="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_SEND_PEOGRESS";
    public static final String FILE_SEND_END="com.fatcat.easy_transfer.MainActivity.Broadcast.FILE_SEND_END";
}
