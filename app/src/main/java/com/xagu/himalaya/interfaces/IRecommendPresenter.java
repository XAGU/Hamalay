package com.xagu.himalaya.interfaces;

/**
 * Created by XAGU on 2020/2/26
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public interface IRecommendPresenter {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 这个方法用于注册ui的回调
     * @param callBack
     */
    void registerViewCallback(IRecommendViewCallBack callBack);

    /**
     * 取消UI的回调注册
     * @param callBack
     */
    void unRegisterViewCallback(IRecommendViewCallBack callBack);
}
