package com.xagu.himalaya.interfaces;

import com.xagu.himalaya.base.IBasePresenter;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {
    /**
     * 获取专辑详情
     */
    void getAlbumDetail(int albumId,int page);

    /**
     * 下拉刷新更多
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();
}
