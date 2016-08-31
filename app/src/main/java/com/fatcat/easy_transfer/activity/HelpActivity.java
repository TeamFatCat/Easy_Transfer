package com.fatcat.easy_transfer.activity;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.base.BaseActivity;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/916:34
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class HelpActivity extends BaseActivity {

    private Toolbar mToolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_help);
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("帮助");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void initFragment() {
        super.initFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
