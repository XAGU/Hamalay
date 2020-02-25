package com.xagu.himalaya.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.xagu.himalaya.utils.FragmentCreator;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class MainContentAdapter extends FragmentPagerAdapter {

    public MainContentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentCreator.getFragment(position);
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}
