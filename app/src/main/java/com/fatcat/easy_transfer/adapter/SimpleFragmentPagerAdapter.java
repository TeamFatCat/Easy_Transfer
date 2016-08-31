package com.fatcat.easy_transfer.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fatcat.easy_transfer.fragment.FragmentFactory;
import com.fatcat.easy_transfer.utils.LogUtils;

/**
 * @author fatcat
 * @version $Rev$
 * @time 2016/4/723:10
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SimpleFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    final int PAGE_COUNT = 6;
    private String tabTitles[] = new String[]{"文件","应用","图库","音乐","视频","接收"};
    private Context context;



    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        LogUtils.s("当前position------："+position);
        return FragmentFactory.getFragment(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
