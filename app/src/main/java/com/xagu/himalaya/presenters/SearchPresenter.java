package com.xagu.himalaya.presenters;

import com.xagu.himalaya.data.HimalayaApi;
import com.xagu.himalaya.interfaces.ISearchCallback;
import com.xagu.himalaya.interfaces.ISearchPresenter;
import com.xagu.himalaya.utils.Constants;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/3/3
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    private volatile static SearchPresenter sInstance = null;
    private List<ISearchCallback> mCallbacks = new ArrayList<>();
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    //当前的搜索关键词
    private String mCurrentKeyword = null;
    private final HimalayaApi mHimalayaApi;

    private List<Album> mAlbums = new ArrayList<>();

    private SearchPresenter() {
        mHimalayaApi = HimalayaApi.getInstance();
    }

    public static SearchPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SearchPresenter.class) {
                if (sInstance == null) {
                    sInstance = new SearchPresenter();
                }
            }
        }
        return sInstance;
    }


    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        mAlbums.clear();
        //用于重新搜索
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    /**
     * 搜索关键字
     *
     * @param keyword
     */
    private void search(String keyword) {
        HimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {

            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mAlbums.addAll(albums);
                if (mIsLoadMore) {
                    for (ISearchCallback callback : mCallbacks) {
                            callback.onLoadMoreResult(mAlbums, albums.size() > 0);
                    }
                    mIsLoadMore = false;
                } else {
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onSearchResultLoaded(mAlbums);
                    }
                }
                LogUtil.d(TAG, "albums size--->" + albums.size());
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code:" + i + "---msg:" + s);
                for (ISearchCallback callback : mCallbacks) {
                    if (mIsLoadMore) {
                        callback.onLoadMoreResult(mAlbums, false);
                        mCurrentPage--;
                        mIsLoadMore = false;
                    } else {
                        callback.onSearchError(i, s);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        if (mCurrentKeyword != null) {
            search(mCurrentKeyword);
        }
    }


    private boolean mIsLoadMore = false;

    @Override
    public void loadMore() {
        //判断有没有必要加载更多
        if (mAlbums.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallback callback : mCallbacks) {
                callback.onLoadMoreResult(mAlbums, false);
            }
        } else {
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWord() {
        mHimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    if (hotWords != null) {
                        //通知UI回调
                        for (ISearchCallback callback : mCallbacks) {
                            callback.onHotWordLoaded(hotWords);
                        }
                        LogUtil.d(TAG, "hotWords size--->" + hotWords.size());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code:" + i + "---msg:" + s);
            }
        });
    }

    @Override
    public void getRecommendWord(final String keyword) {
        mHimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWords = suggestWords.getKeyWordList();
                    if (keyWords != null) {
                        LogUtil.d(TAG, "keyWords size--->" + keyWords.size());
                        for (ISearchCallback callback : mCallbacks) {
                            callback.onRecommendWordLoaded(keyWords);
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code:" + i + "---msg:" + s);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
