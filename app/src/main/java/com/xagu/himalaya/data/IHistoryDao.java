package com.xagu.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by XAGU on 2020/3/6
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface IHistoryDao {

    /**
     * 设置回调接口
     * @param callback
     */
    void setHistoryCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
    void deleteHistory(Track track);

    /**
     * 获取历史
     */
    void listHistory();

    /**
     * 清除所有历史记录
     */
    void clearHistory();
}
