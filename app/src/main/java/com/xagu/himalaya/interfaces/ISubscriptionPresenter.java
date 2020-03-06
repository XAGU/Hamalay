package com.xagu.himalaya.interfaces;

import com.xagu.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: 订阅功能的presenter层，订阅一般是有上限的，上限100
 */

public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 取消订阅
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅
     */
    void getSubscription();

    /**
     * 是否订阅
     * @return
     */
    boolean isSub(long albumId);
}
