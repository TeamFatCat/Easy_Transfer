package com.fatcat.easy_transfer.utils;

import android.test.InstrumentationTestCase;

import java.io.File;

/**
 * Created by FatCat on 2016/8/9.
 */
public class TestFileUtils extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //Intent intent = new Intent();
        // intent.setClassName("com.fatcat.filetransfer.utils",FileUtils.class.getName());
        //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void testGetIcons(){
        File testFile = new File("/test."+ FileUtils.BITMAP[2]);
        int test = FileUtils.getIcons(testFile);
        assertEquals(3, test);
    }

    public void testGetMIMEType(){
        File file = new File("test.jpg");
        String test  = FileUtils.getMIMEType(file);
        assertEquals("image/*",test);
    }


    public void testGetFileType(){
        File file = new File("test.jpg");
        String test = FileUtils.getFileType(file);
        assertEquals("jpg格式",test);
    }

    public  void testGetFileName(){
        File file = new File("test.jpg");
        String test = FileUtils.getFileName(file);
        assertEquals("test.jpg",test);
    }


}
