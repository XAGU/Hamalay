package com.xagu.himalaya.interfaces;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IAlbumDetailPresenter {
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

    /**
     * 这个方法用于注册ui的回调
     * @param callBack
     */
    void registerViewCallback(IAlbumDetailViewCallback callBack);

    /**
     * 取消UI的回调注册
     * @param callBack
     */
    void unRegisterViewCallback(IAlbumDetailViewCallback callBack);
}
