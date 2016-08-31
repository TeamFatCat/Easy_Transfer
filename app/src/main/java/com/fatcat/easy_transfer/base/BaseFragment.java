package com.fatcat.easy_transfer.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatcat.easy_transfer.control.ISendFiles;

/**
 * @author fatcat
 * @version $Rev$
 * @time 2016/1/2011:59
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public abstract class BaseFragment extends Fragment {

    //共有的属性
    //共有的方法

    public ISendFiles mSendFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * @return
     * @des 初始化view, 而且是必须实现, 但是不知道具体实现, 定义成抽象方法
     */
    public abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    public void init() {
        // TODO
        mSendFile=(ISendFiles)getActivity();
    }

    public void initData() {
        // TODO

    }

    public void initListener() {
        // TODO

    }
}
