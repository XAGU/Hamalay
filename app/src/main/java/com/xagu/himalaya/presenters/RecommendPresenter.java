package com.xagu.himalaya.presenters;

import com.xagu.himalaya.data.HimalayaApi;
import com.xagu.himalaya.interfaces.IRecommendPresenter;
import com.xagu.himalaya.interfaces.IRecommendViewCallback;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/2/26
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class RecommendPresenter implements IRecommendPresenter {

    private String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallBacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter() {
    }

    ;

    private static volatile RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前的推荐专辑列表
     *
     * @return 推荐专辑列表，使用前要判空
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }

    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        updateLoading();
        HimalayaApi.getInstance().getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //数据回来了，得更新UI
                    //upRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取失败
                LogUtil.d(TAG, "error code:" + i + "message" + s);
                handleError();
            }
        });
    }

    private void handleError() {
        //通知UI更新
        if (mCallBacks != null) {
            for (IRecommendViewCallback callBack : mCallBacks) {
                callBack.onNetWorkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback callBack : mCallBacks) {
                    callBack.onEmpty();
                }
            } else {
                for (IRecommendViewCallback callBack : mCallBacks) {
                    callBack.onReCommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    private void updateLoading() {
        for (IRecommendViewCallback callBack : mCallBacks) {
            callBack.onLoading();
        }
    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callBack) {
        if (mCallBacks != null && !mCallBacks.contains(callBack)) {
            mCallBacks.add(callBack);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callBack) {
        if (mCallBacks != null) {
            mCallBacks.remove(callBack);
        }
    }
}
