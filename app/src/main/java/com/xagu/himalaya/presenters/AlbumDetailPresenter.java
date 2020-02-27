package com.xagu.himalaya.presenters;

import com.xagu.himalaya.interfaces.IAlbumDetailPresenter;
import com.xagu.himalaya.interfaces.IAlbumDetailViewCallback;
import com.xagu.himalaya.interfaces.IRecommendViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private volatile static AlbumDetailPresenter sInstance = null;
    private Album mTargetAlbum = null;

    private AlbumDetailPresenter() {
    }

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null){
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {

    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback callBack) {
        if (!mCallbacks.contains(callBack)) {
            mCallbacks.add(callBack);
            if (mTargetAlbum != null) {
                callBack.onAlbumLoaded(mTargetAlbum);
            }
    }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback callBack) {
        mCallbacks.remove(callBack);
    }

    public void setTargetAlbum(Album album){
        this.mTargetAlbum = album;
    }
}
