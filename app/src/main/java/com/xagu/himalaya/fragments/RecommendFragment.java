package com.xagu.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xagu.himalaya.R;
import com.xagu.himalaya.adapters.RecommendListAdapter;
import com.xagu.himalaya.base.BaseFragment;
import com.xagu.himalaya.utils.Constants;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container,false);

        //RecycleView的使用
        //1.找到控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);

        //去拿数据回来
        getRecommendData();

        //返回view给界面显示
        return mRootView;
    }

    /**
     * 获取推荐内容，其实就是猜你喜欢
     */
    private void getRecommendData() {
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
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取失败
                LogUtil.d(TAG,"error code:" + i + "message" + s);
            }
        });
    }

    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器，并且更新UI
        mRecommendListAdapter.setData(albumList);
    }
}
