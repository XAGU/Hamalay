package com.xagu.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xagu.himalaya.adapters.AlbumDetailListAdapter;
import com.xagu.himalaya.base.BaseActivity;
import com.xagu.himalaya.interfaces.IAlbumDetailPresenter;
import com.xagu.himalaya.interfaces.IAlbumDetailViewCallback;
import com.xagu.himalaya.presenters.AlbumDetailPresenter;
import com.xagu.himalaya.presenters.PlayerPresenter;
import com.xagu.himalaya.views.RoundRectImageView;
import com.xagu.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, AlbumDetailListAdapter.ItemClickListener {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private RecyclerView mAlbumDetailList;
    private FrameLayout mDetailListContainer;
    private IAlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 1;
    private AlbumDetailListAdapter mAlbumDetailListAdapter;
    private UILoader mUiLoader;
    private long mCurrentId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);

    }

    private void initView() {
        mLargeCover = findViewById(R.id.iv_large_cover);
        mSmallCover = findViewById(R.id.riv_small_cover);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mAlbumAuthor = findViewById(R.id.tv_album_author);
        mDetailListContainer = findViewById(R.id.detail_list_container);
        //
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(this);
        }

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);
        mAlbumDetailList = detailListView.findViewById(R.id.album_detail_list);
        //RecycleView的使用步骤
        //1、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAlbumDetailList.setLayoutManager(linearLayoutManager);
        //2、设置适配器
        mAlbumDetailListAdapter = new AlbumDetailListAdapter();
        mAlbumDetailList.setAdapter(mAlbumDetailListAdapter);
        //设置item的上下间距
        mAlbumDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        mAlbumDetailListAdapter.setOnItemClickListener(this);
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        //判断数据结果,根据结果控制UI
        if (tracks == null || tracks.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新/设置UI数据
        mAlbumDetailListAdapter.setData(tracks);
    }

    @Override
    public void onNetWorkError(int errorCode, String errorMsg) {
        //发生错误，显示网络异常状态
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onAlbumLoaded(Album album) {
        mCurrentId = album.getId();
        //获取专辑详情内容
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) album.getId(),mCurrentPage);
        }
        //拿数据显示loading
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做毛玻璃效果
        if (mLargeCover != null) {
            Glide.with(this).load(album.getCoverUrlLarge()).apply(RequestOptions.bitmapTransform(new BlurTransformation(14,3))).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Glide.with(this).load(album.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null){
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //这里面表示用户网络不佳，点击了重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId,mCurrentPage);
        }
    }

    @Override
    public void onItemClickListener(List<Track> detailList, int position) {
        //设置播放器的数据
        PlayerPresenter.getInstance().setPlayList(detailList,position);
        //跳转到播放器界面
        Intent intent = new Intent(DetailActivity.this,PlayerActivity.class);
        startActivity(intent);
    }
}
