package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by XAGU on 2020/2/28
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IPlayerCallback {
    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首播放
     */
    void onNextPlay(Track track);

    /**
     * 上一首播放
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完成
     * @param tracks 播放列表数据
     */
    void onListLoad(List<Track> tracks);

    /**
     * 播放模式状态改变
     * @param mode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode mode);

    /**
     * 进度条的改变
     * @param currentIndex
     * @param total
     */
    void onProgressChange(int currentIndex,int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告加载结束
     */
    void onAdFinish();

    /**
     * 切歌标题更新
     */
    void onTrackUpdate(Track track,int playIndex);

    /**
     * 通知UI更新播放列表的顺序文字和图标
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);
}
