package com.xagu.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface ISubscriptionDao {

    void setCallback(ISubDaoCallback callback);

    /**
     * 添加订阅
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteAlbum(Album album);

    /**
     * 获取订阅内容
     * @return
     */
    void getAlbums();
}
