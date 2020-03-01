package com.xagu.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IAlbumDetailViewCallback {
    /**
     * 专辑详情内容加载出来了
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 如果网络错误，就通知UI
     * @param errorCode
     * @param errorMsg
     */
    void onNetWorkError(int errorCode, String errorMsg);

    /**
     * 把Album传给UI
     * @param album
     */
    void onAlbumLoaded(Album album);
}
