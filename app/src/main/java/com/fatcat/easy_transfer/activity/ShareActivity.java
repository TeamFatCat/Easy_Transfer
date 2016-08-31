package com.fatcat.easy_transfer.activity;


import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.base.BaseActivity;
import com.fatcat.easy_transfer.utils.UIUtils;
import com.guo.duoduo.httpserver.service.WebService;
import com.guo.duoduo.httpserver.utils.Constant;
import com.guo.duoduo.httpserver.utils.Network;


public class ShareActivity extends BaseActivity {

    private TextView hint;

    @Override
    public void initView() {
        setContentView(R.layout.activity_share);

        hint = (TextView) findViewById(R.id.hint);


    }

    @Override
    protected void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_send2pc_toolbar);
        toolbar.setTitle("分享此应用");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void initData() {
        super.initData();

        String ip = Network.getLocalIp(UIUtils.getContext());

        if (ip != null) {
            hint.setText("http://" + ip + ":" + Constant.Config.PORT);
        } else {
            hint.setText("请连接至同一网络，再打开此页面！");
        }

        startService(new Intent(getApplicationContext(), WebService.class));


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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getApplicationContext(), WebService.class));
    }


}
