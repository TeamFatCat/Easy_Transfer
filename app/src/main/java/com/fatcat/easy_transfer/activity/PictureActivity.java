package com.fatcat.easy_transfer.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fatcat.easy_transfer.R;
import com.fatcat.easy_transfer.base.BaseActivity;

import java.io.File;


/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/916:34
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class PictureActivity extends BaseActivity {

    private Toolbar mToolbar;

    private ImageView mImageView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_picture);
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("查看图片");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();

        String picturePath = intent.getStringExtra("PicturePath");

        File pictureFile = new File(picturePath);

        mImageView = (ImageView) findViewById(R.id.iv_picture_watch);

        Glide.with(this).load(pictureFile).into(mImageView);


    }

    @Override
    protected void initListener() {
        super.initListener();
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
