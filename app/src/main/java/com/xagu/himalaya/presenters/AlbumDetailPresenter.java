package com.xagu.himalaya.presenters;

import com.xagu.himalaya.interfaces.IAlbumDetailPresenter;
import com.xagu.himalaya.interfaces.IAlbumDetailViewCallback;
import com.xagu.himalaya.interfaces.IRecommendViewCallback;
import com.xagu.himalaya.utils.Constants;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private volatile static AlbumDetailPresenter sInstance = null;
    private Album mTargetAlbum = null;
    private static final String TAG = "AlbumDetailPresenter";

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
        //根据页码和Album去拿专辑详情数据
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                LogUtil.d(TAG,"Current Thread ---->"+Thread.currentThread().getName());
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "trackList size:"+trackList.getTracks().size());
                    handlerAlbumDetailResult(tracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code ->" + i + "reason:" + s);
                handlerError(i,s);
            }
        });
    }

    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetWorkError(errorCode,errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onDetailListLoaded(tracks);
        }

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

    public void setTargetAlbum(Album album) {
        this.mTargetAlbum = album;
    }
}
