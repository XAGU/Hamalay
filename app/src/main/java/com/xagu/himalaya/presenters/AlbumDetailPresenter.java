package com.xagu.himalaya.presenters;

import com.xagu.himalaya.data.HimalayaApi;
import com.xagu.himalaya.interfaces.IAlbumDetailPresenter;
import com.xagu.himalaya.interfaces.IAlbumDetailViewCallback;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    private volatile static AlbumDetailPresenter sInstance = null;
    private Album mTargetAlbum = null;
    private static final String TAG = "AlbumDetailPresenter";
    //当前专辑ID
    private int mCurrentAlbumId = -1;
    //当前页
    private int mCurrentPage = 0;

    private AlbumDetailPresenter() {
    }

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPage = page;
        doLoaded(false);

    }

    @Override
    public void loadMore() {
        //去加载更多内容
        mCurrentPage++;
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoadMore) {
        HimalayaApi.getInstance().getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "trackList size:" + trackList.getTracks().size());
                    if (isLoadMore) {
                        //上拉加载，list放在后面
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handleLoaderMoreResult(size);
                    } else {
                        //下拉刷新，list放在前面
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (isLoadMore) {
                    mCurrentPage--;
                }
                LogUtil.d(TAG, "error code ->" + i + "reason:" + s);
                handlerError(i, s);
            }
        }, mCurrentAlbumId, mCurrentPage);
    }

    /**
     * 处理加载更多的结果
     *
     * @param size
     */
    private void handleLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoadMoreFinish(size);
        }
    }


    private void handlePull2RefreshResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onRefreshFinish(size);
        }
    }

    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetWorkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onDetailListLoaded(tracks);
        }

    }

    @Override
    public void pull2RefreshMore() {
        HimalayaApi.getInstance().getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.e(TAG, "trackList size:" + trackList.getTracks().size());
                    //上拉加载，list放在后面
                    mTracks.clear();
                    mTracks.addAll(tracks);
                    handlePull2RefreshResult(tracks.size());
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code ->" + i + "reason:" + s);
                handlerError(i, s);
            }
        }, mCurrentAlbumId, 1, mTracks.size());
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

    public void setTargetAlbum(Album album) {
        this.mTargetAlbum = album;
    }
}
