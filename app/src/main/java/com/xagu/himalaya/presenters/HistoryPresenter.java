package com.xagu.himalaya.presenters;

import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.data.HistoryDao;
import com.xagu.himalaya.data.IHistoryDao;
import com.xagu.himalaya.data.IHistoryDaoCallback;
import com.xagu.himalaya.interfaces.IHistoryCallback;
import com.xagu.himalaya.interfaces.IHistoryPresenter;
import com.xagu.himalaya.utils.Constants;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by XAGU on 2020/3/6
 * Email:xagu_qc@foxmail.com
 * Describe: 历史数量最多100条
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private static volatile HistoryPresenter sInstance = null;
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private final IHistoryDao mHistoryDao;
    private List<Track> mCurrentHistories = new ArrayList<>();


    private HistoryPresenter() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setHistoryCallback(this);
        listHistory();
    }

    public static HistoryPresenter getInstance() {
        if (sInstance == null) {
            synchronized (HistoryPresenter.class) {
                if (sInstance == null) {
                    sInstance = new HistoryPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void addHistory(Track track) {
        if (mCurrentHistories.contains(track)) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    //判断是否已经大于100条
                    if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
                        //先不能添加，先删除最老的一条历史
                        mHistoryDao.deleteHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
                    } else {
                        mHistoryDao.addHistory(track);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteHistory(Track track) {
        if (!mCurrentHistories.contains(track)) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.deleteHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void listHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        mHistoryDao.listHistory();
    }

    @Override
    public void onHistoryDelete(boolean isSuccess) {
        mHistoryDao.listHistory();
    }

    @Override
    public void onHistoryList(List<Track> tracks) {
        this.mCurrentHistories = tracks;
        for (Track track : tracks) {
            LogUtil.d(TAG,"历史记录："+track.getTrackTitle());
        }
        //通知UI更新数据
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoryList(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoryClear(boolean isSuccess) {
        mHistoryDao.listHistory();
    }
}
