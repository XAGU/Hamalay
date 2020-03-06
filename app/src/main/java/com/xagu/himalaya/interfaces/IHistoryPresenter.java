package com.xagu.himalaya.interfaces;

import com.xagu.himalaya.base.IBasePresenter;
import com.xagu.himalaya.data.IHistoryDaoCallback;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by XAGU on 2020/3/6
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {

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
