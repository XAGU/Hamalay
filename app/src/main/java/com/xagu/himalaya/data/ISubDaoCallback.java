package com.xagu.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public interface ISubDaoCallback {
    /**
     * 添加的结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除的结果
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 获取的结果
     * @param albums
     */
    void onGetResult(List<Album> albums);
}
