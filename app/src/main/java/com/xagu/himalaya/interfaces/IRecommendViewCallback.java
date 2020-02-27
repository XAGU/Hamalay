package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by XAGU on 2020/2/26
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IRecommendViewCallback {

    /**
     * 获取推荐内容的接口
     * @param result
     */
    void onReCommendListLoaded(List<Album> result);

    /**
     * 网络错误
     */
    void onNetWorkError();

    /**
     * 数据为空
     */
    void onEmpty();

    /**
     * 加载中
     */
    void onLoading();

}
