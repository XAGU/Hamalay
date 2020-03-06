package com.xagu.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by XAGU on 2020/3/6
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface IHistoryDaoCallback {
    /**
     * 添加历史
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史
     * @param isSuccess
     */
    void onHistoryDelete(boolean isSuccess);

    /**
     * 获取历史
     * @param tracks
     */
    void onHistoryList(List<Track> tracks);

    /**
     * 清除所有历史记录
     * @param isSuccess
     */
    void onHistoryClear(boolean isSuccess);
}
