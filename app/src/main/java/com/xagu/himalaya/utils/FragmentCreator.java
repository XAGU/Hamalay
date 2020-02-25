package com.xagu.himalaya.utils;

import com.xagu.himalaya.base.BaseFragment;
import com.xagu.himalaya.fragments.HistoryFragment;
import com.xagu.himalaya.fragments.RecommendFragment;
import com.xagu.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class FragmentCreator {

    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIPTION = 1;
    public final static int INDEX_HISTORY = 2;

    public final static int PAGE_COUNT = 3;

    private static Map<Integer, BaseFragment> sCache = new HashMap<>();

    public static BaseFragment getFragment(int index){
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }

        switch (index){
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
        }
        sCache.put(index,baseFragment);
        return baseFragment;
    }

}
