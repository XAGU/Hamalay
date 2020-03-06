package com.xagu.himalaya.presenters;

import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.data.ISubDaoCallback;
import com.xagu.himalaya.data.SubscriptionDao;
import com.xagu.himalaya.interfaces.ISubscriptionCallback;
import com.xagu.himalaya.interfaces.ISubscriptionPresenter;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private static final String TAG = "SubscriptionPresenter";
    private volatile static SubscriptionPresenter sInstance = null;
    private final SubscriptionDao mSubscriptionDao;
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();
    private Map<Long, Album> mData = new HashMap<>();


    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
    }

    public static SubscriptionPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                if (sInstance == null) {
                    sInstance = new SubscriptionPresenter();
                }
            }
        }
        return sInstance;
    }

    public void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                //只调用，不处理
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.getAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    @Override
    public void addSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    if (!mData.containsKey(album.getId())) {
                        mSubscriptionDao.addAlbum(album);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                mSubscriptionDao.deleteAlbum(album);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscription() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(long albumId) {
        return mData.containsKey(albumId);
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void onAddResult(boolean isSuccess) {
        mSubscriptionDao.getAlbums();
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddSubscriptionResult(true);
                }
            }
        });
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        mSubscriptionDao.getAlbums();
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteSubscriptionResult(true);
                }
            }
        });
    }

    @Override
    public void onGetResult(final List<Album> albums) {
        mData.clear();
        for (Album album : albums) {
            mData.put(album.getId(), album);
            LogUtil.d(TAG,"订阅："+album.getAlbumTitle());
        }
        //通知UI更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onGetSubscriptionResult(albums);
                }
            }
        });
    }
}
