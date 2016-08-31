package com.fatcat.easy_transfer.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/722:06
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initActionBar();
        initData();
        initListener();
        initFragment();
    }

    /**
     * 初始化视图，交给子类实现
     */
    protected abstract void initView();

    /**
     * 如有需要，重写此方法，初始化ActionBar
     */
    protected void initActionBar() {
    }

    /**
     * 如有需要，重写此方法，初始化数据
     */
    protected void initData() {
    }

    /**
     * 如有需要，重写此方法，初始化监听器
     */
    protected void initListener() {
    }

    /**
     * 如有需要，重写此方法，初始化Fragment
     */
    protected void initFragment() {
    }


}
