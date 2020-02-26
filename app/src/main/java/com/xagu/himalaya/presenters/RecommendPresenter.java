package com.xagu.himalaya.presenters;

import android.util.Log;

import com.xagu.himalaya.interfaces.IRecommendPresenter;
import com.xagu.himalaya.interfaces.IRecommendViewCallBack;
import com.xagu.himalaya.utils.Constants;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XAGU on 2020/2/26
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class RecommendPresenter implements IRecommendPresenter {

    private String TAG = "RecommendPresenter";

    private List<IRecommendViewCallBack> mCallBacks = new ArrayList<>();

    private RecommendPresenter(){};

    private static volatile RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (sInstance == null) {
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        Map<String,String> map = new HashMap<>();
        //这个参数表示一页返回多少数据
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
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
                LogUtil.d(TAG,"error code:" + i + "message" + s);
            }
        });
    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (mCallBacks != null) {
            for (IRecommendViewCallBack callBack : mCallBacks) {
                callBack.onReCommendListLoaded(albumList);
            }
        }
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
    }

    @Override
    public void registerViewCallback(IRecommendViewCallBack callBack) {
        if (mCallBacks != null&&!mCallBacks.contains(callBack)) {
            mCallBacks.add(callBack);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallBack callBack) {
        if (mCallBacks != null){
            mCallBacks.remove(callBack);
        }
    }
}
