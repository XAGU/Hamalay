package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface ISubscriptionCallback {

    /**
     * 添加订阅回调
     * @param isSuccess
     */
    void onAddSubscriptionResult(boolean isSuccess);

    /**
     * 取消订阅的回调
     * @param isSuccess
     */
    void onDeleteSubscriptionResult(boolean isSuccess);

    /**
     * 获取订阅回调
     */
    void onGetSubscriptionResult(List<Album> albums);
}
