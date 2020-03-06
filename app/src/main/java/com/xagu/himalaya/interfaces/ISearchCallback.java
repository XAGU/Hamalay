package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * Created by XAGU on 2020/3/3
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface ISearchCallback {
    /**
     * 搜索关键词回调
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取热词回调
     * @param result
     */
    void onHotWordLoaded(List<HotWord> result);

    /**
     * 加载更多搜索
     * @param result
     * @param isOkay
     */
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    /**
     * 根据关键词推荐搜索词
     * @param result
     */
    void onRecommendWordLoaded(List<QueryResult> result);

    /**
     * 错误通知回调
     * @param errorCode
     * @param errorMsg
     */
    void onSearchError(int errorCode,String errorMsg);
}
