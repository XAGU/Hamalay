package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by XAGU on 2020/2/26
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IRecommendViewCallBack {

    /**
     * 获取推荐内容的接口
     * @param result
     */
    void onReCommendListLoaded(List<Album> result);

    /**
     * 加载更多
     * @param result
     */
    void onLoaderMore(List<Album> result);

    /**
     * 下接加载更多的结果
     * @param result
     */
    void onRefreshMore(List<Album> result);

}
