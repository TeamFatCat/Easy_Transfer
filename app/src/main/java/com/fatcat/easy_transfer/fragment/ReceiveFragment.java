package com.fatcat.easy_transfer.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.base.BaseFragment;
import com.fatcat.easy_transfer.utils.FileUtils;
import com.fatcat.easy_transfer.utils.LogUtils;
import com.fatcat.easy_transfer.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FatCat on 2016/8/4.
 */
public class ReceiveFragment extends BaseFragment {

    private static ArrayList<File> mFileList;

    // private ProgressBar progressBar;

    private static GridView mGridView;

    private static File mParentFile;

    private static File[] mChildFiles;

    private static String receivePath;

    private File mParentCopyFile;

    private File[] mChildCopyFiles;

    private ListView mCopyList;

    private TextView tv_file_path;

    private TextView mFile;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive, container, false);


        mGridView = (GridView) view.findViewById(R.id.gv_receive);

        mFile= (TextView) view.findViewById(R.id.tv_no_file);
        //progressBar = (ProgressBar) view.findViewById(R.id.loading_receive);


        return view;
    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public void initData() {
        super.initData();

        receivePath = FileUtils.getFilePath() + "/" + "transferDownload";

        mFileList = new ArrayList<>();

        mParentFile = new File(FileUtils.getFilePath() + "/" + "transferDownload");


        //LogUtils.s("------------------------"+mParentFile.listFiles().toString());

        mChildFiles = mParentFile.listFiles();
        if (mChildFiles == null) {

            mFile.setVisibility(View.VISIBLE);
        } else {

            mFile.setVisibility(View.GONE);

            mFileList.addAll(Arrays.asList(mChildFiles));

            //mGridView.setAdapter(new ReceiveAdapter(getContext(), mFileList));
            initGridView();
        }


    }

    private static void initGridView() {


        mChildFiles = mParentFile.listFiles();

        //mChildFiles = mParentFile.listFiles();

        mFileList.removeAll(mFileList);

        mFileList.addAll(Arrays.asList(mChildFiles));

        mFileList.toArray(mChildFiles);


        //创建一个List对象，List对象的元素是Map
        List<Map<String, Object>> listItems
                = new ArrayList<Map<String, Object>>();
        Map<String, Object> listItem;
        for (int i = 0; i < mChildFiles.length; i++) {
            listItem = new HashMap<String, Object>();
            listItem.put("file_icon", FileUtils.Icons[FileUtils.getIcons(mChildFiles[i])]);
            listItem.put("file_name", mChildFiles[i].getName());
            listItems.add(listItem);
        }


        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(UIUtils.getContext()
                , listItems
                //使用/layout/cell.xml文件作为界面布局
                , R.layout.gridview_info
                , new String[]{"file_icon", "file_name"}
                , new int[]{R.id.iv_icon_name, R.id.tv_file_name});

        //为GridView设置Adapter
        mGridView.setAdapter(simpleAdapter);

    }


    @Override
    public void initListener() {
        super.initListener();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LogUtils.i("TAG_1", "parent_ID:" + parent.getId());
                LogUtils.i("TAG_1", "view_ID" + view.getId());

                File file_Clicked = mChildFiles[position];

                if (file_Clicked.exists() && file_Clicked.canRead()) {
                    if (file_Clicked.isDirectory()) {
                        LogUtils.i("TAG_1", "点击了文件夹");
                        mParentFile = file_Clicked;
                        mChildFiles = file_Clicked.listFiles();
                        initGridView();//刷新GrilView
                    } else {
                        LogUtils.i("TAG_1", "文件");
                        FileUtils.openFile(file_Clicked);
                    }
                } else {
                    Toast.makeText(UIUtils.getContext(), "没有权限", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public static boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (receivePath.equals(mParentFile.getPath())) {

                return true;
            }
            mParentFile = mParentFile.getParentFile();
            initGridView();


            return false;
        } else {
            return false;
        }


    }


}
