package com.fatcat.easy_transfer.net;

import android.test.InstrumentationTestCase;

/**
 * Created by hasee on 2016/5/17.
 */
public class TestServerThread extends InstrumentationTestCase{

    ServerThread serverThread = null;
    String ip = null;
    int post;
    String path = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initTestData();
        serverThread = ServerThread.createServerThread(ip,post,path);
    }

    private void initTestData(){
        ip = "192.168.100.10";
        post = 6602;
        path = "/test/a.zip";
    }

    public void testGetIp(){
        assertEquals(ip,serverThread.getIP());
    }

    public void testGetPort(){

        assertEquals(post,serverThread.getPort());
    }


    public void testSetFileSavaPath(){
        String oldpath = serverThread.getFileSavaPath();
        String newpath = "/test/b.zip";
        if ( newpath.equals(oldpath) ){
            newpath += ".test";
        }
        serverThread.setFileSavaPath(newpath);
        assertEquals(newpath,serverThread.getFileSavaPath());
        serverThread.setFileSavaPath(oldpath);
    }

    public void testGetFileSavaPath(){
        assertEquals(path,serverThread.getFileSavaPath());
    }
}
