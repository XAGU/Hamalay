package com.xagu.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.xagu.himalaya.data.HimalayaApi;
import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.interfaces.IPlayerCallback;
import com.xagu.himalaya.interfaces.IPlayerPresenter;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by XAGU on 2020/2/28
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private static final int DEFAULT_PLAY_INDEX = 0;
    private volatile static PlayerPresenter sInstance = null;
    private final XmPlayerManager mPlayerManager;
    List<IPlayerCallback> mCallbacks = new ArrayList<>();
    private Track mCurrentTrack;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayModeSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private boolean mIsReverse = false;

    private static final int PLAY_MODEL_LIST_INT = 0;
    private static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    private static final int PLAY_MODEL_RANDOM_INT = 2;
    private static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //sp`s key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);
    }

    ;

    public static PlayerPresenter getInstance() {
        if (sInstance == null) {
            synchronized (PlayerPresenter.class) {
                if (sInstance == null) {
                    sInstance = new PlayerPresenter();
                }
            }
        }
        return sInstance;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> tracks, int playIndex) {
        if (mPlayerManager != null) {
            isPlayListSet = true;
            mPlayerManager.setPlayList(tracks, playIndex);
            mCurrentTrack = tracks.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtil.d(TAG, "setPlayList is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (isPlayListSet) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    /**
     * 判断是否有播放的播放节目列表
     *
     * @return
     */
    public boolean hasPlayList() {
        return isPlayListSet;
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            mPlayModeSp.edit().putInt(PLAY_MODE_SP_KEY, getIntByPlayMode(mode)).apply();
            //通知UI
            for (IPlayerCallback callback : mCallbacks) {
                callback.onPlayModeChange(mode);
            }
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getPlayModeByInt(int index) {
        switch (index) {
            case PLAY_MODEL_LIST_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
            case PLAY_MODEL_RANDOM_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;
        }
        return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback callback : mCallbacks) {
                callback.onListLoad(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到index的位置播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //把播放器的播放列表反转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);
        //更新UI
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback callback : mCallbacks) {
            callback.onListLoad(playList);
            callback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            callback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long albumId) {
        //1、要获取到专辑的列表内容
        HimalayaApi.getInstance().getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //把专辑内容给播放器
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, DEFAULT_PLAY_INDEX);
                    isPlayListSet = true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(), "请求数据错误Error Code:" + i + "--Msg:" + s, Toast.LENGTH_SHORT).show();
            }
        }, albumId, 1);
        //2、
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        if (!mCallbacks.contains(iPlayerCallback)) {
            mCallbacks.add(iPlayerCallback);
        }
        //更新之前，先让UI的Pager有数据
        getPlayList();
        //通知当前的节目
        iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgressPosition,mProgressDuration);
        //更新状态
        handlePlayState(iPlayerCallback);
        //从sp里头拿播放模式
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getPlayModeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        //根据状态调用
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallback.onPlayStart();
        } else {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mCallbacks.remove(iPlayerCallback);
    }

    //---------------广告相关的回调方法START----------------------------------------
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo..");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo..");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering..");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering..");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds..");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds..");
    }

    @Override
    public void onError(int i, int i1) {
        LogUtil.d(TAG, "onError code -->" + i + "extra -->" + i1);
    }
    //---------------广告相关的回调方法END----------------------------------------

    //---------------播放器状态相关的回调方法Start---------------------------------
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "onPlayStart");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, "onPlayPause");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "onPlayStop");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, "onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG, "onSoundPrepared");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完了，可以播放
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel currentModel) {
        LogUtil.d(TAG, "onSoundSwitch...");
        //currentModel代表的是当前的内容
        //通过kind（）获取他是什么类型的
        //track表示是track类型
        //第一种写法  不推荐
        //if ("track".equals(currentModel.getKind())){
        //    Track currentTrack = (Track) currentModel;
        //    LogUtil.d(TAG,"title---->" + currentTrack.getTrackTitle());
        //    System.out.println("title---->" + currentTrack.getTrackTitle());
        //}
        //第二种写法
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (currentModel instanceof Track) {
            mCurrentTrack = (Track) currentModel;
            LogUtil.d(TAG, "title---->" + mCurrentTrack.getTrackTitle());
            for (IPlayerCallback callback : mCallbacks) {
                callback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            }
        }

    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, "onBufferingStop");
    }

    @Override
    public void onBufferProgress(int i) {
        LogUtil.d(TAG, "缓存进度--》" + i);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        this.mCurrentProgressPosition = currPos;
        this.mProgressDuration = duration;
        //单位是毫秒
        for (IPlayerCallback callback : mCallbacks) {
            callback.onProgressChange(currPos, duration);
        }
        LogUtil.d(TAG, "onPlayProgress");
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, "onError---》" + e.toString());
        return false;
    }
    //---------------播放器状态相关的回调方法End---------------------------------
}
