package com.xagu.himalaya.data;

import com.xagu.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XAGU on 2020/3/2
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class HimalayaApi {

    private volatile static HimalayaApi sInstance = null;

    private HimalayaApi() {
    }

    public static HimalayaApi getInstance() {
        if (sInstance == null) {
            synchronized (HimalayaApi.class) {
                if (sInstance == null) {
                    sInstance = new HimalayaApi();
                }
            }
        }
        return sInstance;
    }


    /**
     * 获取推荐内容
     *
     * @param callBack 数据回调
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        //这个参数表示一页返回多少数据
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    /**
     * 根据专辑id获取专辑详情内容
     *
     * @param callback 获取专辑详情的回调
     * @param albumId  专辑id
     * @param page     第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callback, long albumId, int page) {
        //根据页码和Album去拿专辑详情数据
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callback);
    }

    /**
     * 根据专辑id获取专辑详情内容
     *
     * @param callback 获取专辑详情的回调
     * @param albumId  专辑id
     * @param page     第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callback, long albumId, int page, int pageSize) {
        //根据页码和Album去拿专辑详情数据
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, pageSize > 200 ? "200" : pageSize + "");
        CommonRequest.getTracks(map, callback);
    }

    /**
     * 根据关键词搜索
     * @param keyword
     */
    public static void searchByKeyword(String keyword,int page,IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map,callback);
    }


    /**
     * 获取推荐的热词
     */
    public void getHotWords(IDataCallBack<HotWordList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD+"");
        CommonRequest.getHotWords(map,callback);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword
     * @param callback
     */
    public void getSuggestWord(String keyword,IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map,callback);
    }
}
