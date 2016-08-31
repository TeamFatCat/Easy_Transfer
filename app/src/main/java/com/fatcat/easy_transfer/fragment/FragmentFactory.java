package com.fatcat.easy_transfer.fragment;


import android.support.v4.util.SparseArrayCompat;

import com.fatcat.easy_transfer.base.BaseFragment;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/4/237:54
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class FragmentFactory {


    public static final int FRAGMENT_FILE = 0;
    public static final int FRAGMENT_APP = 1;
    public static final int FRAGMENT_PICTRUE = 2;
    public static final int FRAGMENT_MUSIC = 3;
    public static final int FRAGMENT_VIDEO = 4;
    public static final int RECEIVE_FILE = 5;
    static SparseArrayCompat<BaseFragment> cachesFragment = new SparseArrayCompat<BaseFragment>();


    public static BaseFragment getFragment(int position) {
        BaseFragment fragment = null;
        // 如果缓存里面有对应的fragment,就直接取出返回

        BaseFragment tmpFragment = cachesFragment.get(position);

        if (tmpFragment != null) {
            fragment = tmpFragment;
            return fragment;
        }

        switch (position) {
            case RECEIVE_FILE:
                fragment = new ReceiveFragment();
                break;
            case FRAGMENT_FILE:
                fragment = new FileFragment();
                break;
            case FRAGMENT_APP:
                fragment = new AppFragment();
                break;
            case FRAGMENT_PICTRUE:
                fragment = new PictureFragment();
                break;
            case FRAGMENT_MUSIC:
                fragment = new MusicFragment();
                break;
            case FRAGMENT_VIDEO:
                fragment = new VideoFragment();
                break;
            default:
                break;
        }
        // 保存对应的fragment
        cachesFragment.put(position, fragment);
        return fragment;


    }
}
