package com.fatcat.easy_transfer.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FatCat on 2016/7/17.
 */
public class FileFragment extends BaseFragment {

    private static GridView mGridView;

    private static File mParentFile;

    private static File[] mChildFiles;

    private static ArrayList<File> mFileList;

    private File mParentCopyFile;

    private File[] mChildCopyFiles;

    private ListView mCopyList;

    private TextView tv_file_path;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        mGridView = (GridView) view.findViewById(R.id.gv_file);
        return view;
    }

    @Override
    public void init() {
        super.init();

    }


    @Override
    public void initData() {
        super.initData();

        mFileList = new ArrayList<>();

        File path = FileUtils.getFilePath();

        //如果路径存在
        if (path.exists()) {
            mParentFile = path;
            initGridView();
        }


    }

    private static void initGridView() {


        mChildFiles = FileUtils.filtration(mParentFile.listFiles());

        //mChildFiles = mParentFile.listFiles();

        mFileList.removeAll(mFileList);

        mFileList.addAll(Arrays.asList(mChildFiles));

        Collections.sort(mFileList, new FileUtils.FileComparer());

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

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final File file_Long_Clicked = mChildFiles[position];

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == FileUtils.OPEN_FILE) {
                            if (file_Long_Clicked.exists() && file_Long_Clicked.canRead()) {
                                if (file_Long_Clicked.isDirectory()) {
                                    LogUtils.i("TAG_1", "点击了文件夹");
                                    mParentFile = file_Long_Clicked;
                                    mChildFiles = file_Long_Clicked.listFiles();
                                    initGridView();//刷新GrilView
                                } else {
                                    LogUtils.i("TAG_1", "文件");
                                    FileUtils.openFile(file_Long_Clicked);
                                }
                            } else {
                                Toast.makeText(UIUtils.getContext(), "没有权限", Toast.LENGTH_SHORT).show();
                            }
                        } else if (which == FileUtils.CHANGE_FILE_NAME) {
                            initChangeFileName(file_Long_Clicked);
                        } else if (which == FileUtils.DELETE_FILE) {
                            initDeleteFile(file_Long_Clicked);
                        } else if (which == FileUtils.COPY_FILE) {
                            initCopyFile(file_Long_Clicked);

                        } else if (which == FileUtils.SEND_FILE) {
                            if (file_Long_Clicked.isDirectory()) {
                                LogUtils.s("#-------发送的是文件夹");
                            } else {
                                ArrayList<String> file_list = new ArrayList<String>();
                                file_list.add(file_Long_Clicked.getAbsolutePath());
                                mSendFile.sendFileList(file_list);
                            }
                        } else if (which == FileUtils.FILE_INFO) {
                            initFileInfo(file_Long_Clicked);
                        }
                    }
                };
                //选择文件时，弹出操作选项对话框
                String[] menu = {"发送文件", "打开文件", "重命名", "删除文件", "复制文件", "文件属性"};
                new AlertDialog.Builder(getContext())
                        .setItems(menu, listener)
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                return true;
            }
        });

    }

    private void initChangeFileName(final File file_Long_Clicked) {
        //定义1个文本输入框
        final EditText fileName = new EditText(getContext());
        fileName.setText(file_Long_Clicked.getName());
        fileName.setTextColor(getResources().getColor(R.color.text_color));
        //创建对话框
        new AlertDialog.Builder(getContext())
                .setTitle("重命名")//设置对话框标题
                .setView(fileName)//为对话框添加要显示的组件
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//设置对话框[肯定]按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.s("fileName=" + fileName.getText().toString());
                        String modifyName = fileName.getText().toString();
                        final String fpath = file_Long_Clicked.getParentFile().getPath();
                        final File newFile = new File(fpath + "/" + modifyName);
                        if (newFile.exists()) {
                            //排除没有修改情况
                            if (!modifyName.equals(file_Long_Clicked.getName())) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("注意!")
                                        .setMessage("文件名已存在，是否覆盖？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (file_Long_Clicked.renameTo(newFile)) {
                                                    mParentFile = file_Long_Clicked.getParentFile();
                                                    mChildFiles = mParentFile.listFiles();
                                                    initGridView();//刷新GrilView
                                                    LogUtils.s("##-------重命名成功");
                                                } else {
                                                    LogUtils.s("##-------重命名失败");
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        } else {
                            if (file_Long_Clicked.renameTo(newFile)) {
                                mParentFile = file_Long_Clicked.getParentFile();
                                mChildFiles = mParentFile.listFiles();
                                initGridView();//刷新GrilView
                                LogUtils.s("##-------重命名成功");
                            } else {
                                LogUtils.s("##-------重命名失败");
                            }
                        }

                    }
                }).setNegativeButton("取消", null)//设置对话框[否定]按钮
                .show();
    }

    private void initDeleteFile(final File file_Long_Clicked) {
        new AlertDialog.Builder(getContext())
                .setTitle("注意!")
                .setMessage("确定要删除此文件吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDelete = FileUtils.deleteFile(file_Long_Clicked);

                        LogUtils.s("------------删除文件夹-------" + isDelete);
                        //更新文件列表
                        mParentFile = file_Long_Clicked.getParentFile();
                        mChildFiles = mParentFile.listFiles();
                        initGridView();//刷新GrilView
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void initCopyFile(final File file_Long_Clicked) {
        LogUtils.s("#------------------拷贝文件");


        LinearLayout copyLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_copy_file, null);

        ImageView iv_action_back = (ImageView) copyLayout.findViewById(R.id.iv_action_back);

        tv_file_path = (TextView) copyLayout.findViewById(R.id.tv_file_path);

        iv_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentCopyFile = mParentCopyFile.getParentFile();
                initListView();
            }
        });

        ImageView add_new_folder = (ImageView) copyLayout.findViewById(R.id.add_new_folder);

        add_new_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //定义1个文本输入框
                final EditText fileName = new EditText(getContext());
                fileName.setText("文件夹");
                fileName.setTextColor(getResources().getColor(R.color.text_color));
                //创建对话框
                new AlertDialog.Builder(getContext())
                        .setTitle("新建")//设置对话框标题
                        .setView(fileName)//为对话框添加要显示的组件
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String folderName = fileName.getText().toString().trim();
                                LogUtils.s("当前新建文件所在的路径-------" + mParentCopyFile.getPath());
                                File destDir = new File(mParentCopyFile.getPath() + "/" + folderName);
                                if (!destDir.exists()) {
                                    destDir.mkdirs();
                                    initListView();
                                    initGridView();
                                }

                            }//设置对话框[肯定]按钮
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });

        mCopyList = (ListView) copyLayout.findViewById(R.id.lv_file_list);

        mParentCopyFile = FileUtils.getFilePath();
        mChildCopyFiles = mParentCopyFile.listFiles();

        initListView();

        AlertDialog.Builder copyDialog = new AlertDialog.Builder(getContext());
        copyDialog.setView(copyLayout);

        copyDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        copyDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (file_Long_Clicked.isDirectory()) {

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            LogUtils.s("文件夹拷贝-----" + file_Long_Clicked.getPath() + "---------" + mParentCopyFile.getPath());
                            FileUtils.copyFolder(file_Long_Clicked.getPath(), mParentCopyFile.getPath() + "/" + file_Long_Clicked.getName());
                        }
                    }.start();

                } else {
                    LogUtils.s("文件拷贝-----" + file_Long_Clicked.getPath() + "---------" + mParentCopyFile.getPath());
                    FileUtils.copyFile(file_Long_Clicked.getPath(), mParentCopyFile.getPath() + "/" + file_Long_Clicked.getName());
                }
            }
        });

        copyDialog.create().show();

    }

    private void initFileInfo(File file_Long_Clicked) {

        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_file_info, null);


        FileUtils.setDialogFileInfo(linearLayout, file_Long_Clicked);

        ab.setView(linearLayout).setTitle("属性").setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ab.create().show();
    }

    private void initListView() {

        tv_file_path.setText(mParentCopyFile.getPath());


        mChildCopyFiles = FileUtils.filtration(mParentCopyFile.listFiles());


        Collections.sort(Arrays.asList(mChildCopyFiles), new FileUtils.FileComparer());

        final List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Map<String, Object> listItem;
        for (int i = 0; i < mChildCopyFiles.length; i++) {
            listItem = new HashMap<String, Object>();
            listItem.put("file_icon", FileUtils.Icons[FileUtils.getIcons(mChildCopyFiles[i])]);
            listItem.put("file_name", mChildCopyFiles[i].getName());
            datas.add(listItem);
        }

        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(UIUtils.getContext()
                , datas
                , R.layout.copy_file_item
                , new String[]{"file_icon", "file_name"}
                , new int[]{R.id.iv_copy_file_icon, R.id.tv_copy_file_name});

        //为ListView设置Adapter
        mCopyList.setAdapter(simpleAdapter);

        mCopyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File click_file = mChildCopyFiles[position];

                if (click_file.isDirectory()) {
                    LogUtils.i("TAG_1", "点击了文件夹");
                    mParentCopyFile = click_file;
                    mChildCopyFiles = click_file.listFiles();
                    initListView();//刷新ListView
                }

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();

//        mServerThread.interrupt();

    }

    public static boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (FileUtils.getFilePath().getPath().equals(mParentFile.getPath())) {

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
