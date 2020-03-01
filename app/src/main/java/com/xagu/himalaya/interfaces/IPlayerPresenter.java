package com.xagu.himalaya.interfaces;

import com.xagu.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * Created by XAGU on 2020/2/28
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 上一首
     */
    void playNext();


    /**
     * 下一首
     */
    void playPre();

    /**
     * 切换播放模式
     * @param Mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode Mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行播放
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否正在播放
     * @return
     */
    boolean isPlay();
}
