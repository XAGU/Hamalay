package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by XAGU on 2020/3/6
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface IHistoryCallback {
    /**
     * 添加历史
     * @param track
     */
    void onHistoryAdd(Track track);

    /**
     * 删除历史
     * @param track
     */
    void onHistoryDelete(Track track);

    /**
     * 获取历史
     * @param tracks
     */
    void onHistoryList(List<Track> tracks);

    /**
     * 清除所有历史记录
     */
    void onHistoryClear();
}
