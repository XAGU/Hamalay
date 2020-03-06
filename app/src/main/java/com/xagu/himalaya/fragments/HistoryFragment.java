package com.xagu.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.xagu.himalaya.PlayerActivity;
import com.xagu.himalaya.R;
import com.xagu.himalaya.adapters.AlbumDetailListAdapter;
import com.xagu.himalaya.base.BaseFragment;
import com.xagu.himalaya.interfaces.IHistoryCallback;
import com.xagu.himalaya.presenters.HistoryPresenter;
import com.xagu.himalaya.presenters.PlayerPresenter;
import com.xagu.himalaya.views.ConfirmCheckBoxDialog;
import com.xagu.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class HistoryFragment extends BaseFragment implements IHistoryCallback {

    private UILoader mUiLoader = null;
    private RecyclerView mAlbumDatalList;
    private TwinklingRefreshLayout mRefreshLayout;
    private AlbumDetailListAdapter mAlbumDetailListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private PlayerPresenter mPlayerPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        if (mUiLoader == null) {
            mUiLoader = new UILoader(getActivity()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
        }
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }
        initPresenter();
        initEvent();
        return mUiLoader;
    }

    private void initEvent() {
        mAlbumDetailListAdapter.setOnItemClickListener(new AlbumDetailListAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(List<Track> detailList, int position) {
                mPlayerPresenter = PlayerPresenter.getInstance();
                mPlayerPresenter.setPlayList(detailList,position);
                startActivity(new Intent(getActivity(), PlayerActivity.class));
            }

            @Override
            public void onItemLongClickListener(List<Track> detailList, int position) {
                final ConfirmCheckBoxDialog confirmDialog = new ConfirmCheckBoxDialog(getActivity());
                confirmDialog.setOnBtnClickListener(new ConfirmCheckBoxDialog.OnBtnClickListener() {
                    @Override
                    public void onConfirmClick(boolean isChecked) {
                        if (!isChecked) {
                            //删除当前
                            if (mHistoryPresenter != null) {
                                mHistoryPresenter.deleteHistory(detailList.get(position));
                            }
                        } else {
                            //删除所有
                            if (mHistoryPresenter != null) {
                                mHistoryPresenter.clearHistory();
                            }
                        }
                    }

                    @Override
                    public void onCancelClick(boolean isChecked) {

                    }
                });
                confirmDialog.show();
            }
        });
    }

    private void initPresenter() {
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onDestroy() {
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
        super.onDestroy();
    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(getActivity()).inflate(R.layout.item_detail_list, container, false);
        mAlbumDatalList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setPureScrollModeOn();
        //RecycleView的使用步骤
        //1、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mAlbumDatalList.setLayoutManager(linearLayoutManager);
        //2、设置适配器
        mAlbumDetailListAdapter = new AlbumDetailListAdapter();
        mAlbumDatalList.setAdapter(mAlbumDetailListAdapter);
        //设置item的上下间距
        mAlbumDatalList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        return detailListView;
    }

    @Override
    public void onHistoryAdd(Track track) {

    }

    @Override
    public void onHistoryDelete(Track track) {

    }

    @Override
    public void onHistoryList(List<Track> tracks) {
        if (tracks==null||tracks.size()<=0) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else {
            mAlbumDetailListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onHistoryClear() {

    }
}
