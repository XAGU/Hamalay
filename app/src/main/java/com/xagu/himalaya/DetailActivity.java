package com.xagu.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.xagu.himalaya.adapters.AlbumDetailListAdapter;
import com.xagu.himalaya.base.BaseActivity;
import com.xagu.himalaya.interfaces.IAlbumDetailPresenter;
import com.xagu.himalaya.interfaces.IAlbumDetailViewCallback;
import com.xagu.himalaya.interfaces.IPlayerCallback;
import com.xagu.himalaya.interfaces.ISubscriptionCallback;
import com.xagu.himalaya.presenters.AlbumDetailPresenter;
import com.xagu.himalaya.presenters.PlayerPresenter;
import com.xagu.himalaya.presenters.SubscriptionPresenter;
import com.xagu.himalaya.views.RoundRectImageView;
import com.xagu.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, AlbumDetailListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private RecyclerView mAlbumDatalList;
    private FrameLayout mDetailListContainer;
    private IAlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 1;
    private AlbumDetailListAdapter mAlbumDetailListAdapter;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle;
    private TextView mDetailSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrenAlbum = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        initPresenter();
        //设置订阅的状态
        updatePlayState(mPlayerPresenter.isPlaying());
        initEvent();
    }

    private void updateSubState(boolean isSub) {
        mDetailSubBtn.setText(isSub?R.string.cancel_sub_tips_text:R.string.sub_tips_text);
    }

    private void initPresenter() {
        //这个是专辑详情的presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器的presenter
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //订阅相关
        mSubscriptionPresenter = SubscriptionPresenter.getsInstance();
        mSubscriptionPresenter.listSubscriptions();
        mSubscriptionPresenter.registerViewCallback(this);
    }


    private void initEvent() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //判断播放器是否有播放列表
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (hasPlayList) {
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }
                }


            }
        });

        mDetailSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrenAlbum != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentId);
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrenAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrenAlbum);
                    }
                }
            }
        });
    }

    /**
     * 当播放器里面没有播放列表
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        //控制播放器的状态
        if (mPlayerPresenter.isPlaying()) {
            //正在播放就暂停
            mPlayerPresenter.pause();
        } else {
            //暂停就开始播放
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        mLargeCover = findViewById(R.id.iv_large_cover);
        mSmallCover = findViewById(R.id.riv_small_cover);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mAlbumAuthor = findViewById(R.id.tv_album_author);
        mDetailSubBtn = findViewById(R.id.detail_sub_btn);
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
        mPlayControlBtn = findViewById(R.id.detail_play_control);
        mPlayControlTv = findViewById(R.id.detail_play_control_tv);
        mPlayControlTv.setSelected(true);

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mAlbumDatalList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //RecycleView的使用步骤
        //1、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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

        mAlbumDetailListAdapter.setOnItemClickListener(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                if (mAlbumDetailPresenter != null){
                    mAlbumDetailPresenter.pull2RefreshMore();
                    mIsRefresh = true;
                }
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoadMore = true;
                }
            }
        });
        return detailListView;
    }

    private boolean mIsLoadMore = false;
    private boolean mIsRefresh = false;

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoadMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoadMore = false;
        }
        if (mIsRefresh && mRefreshLayout != null) {
            mRefreshLayout.finishRefreshing();
            mIsRefresh = false;
        }
        this.mCurrentTracks = tracks;
        //判断数据结果,根据结果控制UI
        if (tracks == null || tracks.size() == 0) {
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
        mCurrenAlbum = album;
        mCurrentId = album.getId();
        //获取专辑详情内容
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) album.getId(), mCurrentPage);
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
            Glide.with(this).load(album.getCoverUrlLarge()).apply(RequestOptions.bitmapTransform(new BlurTransformation(14, 3))).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Glide.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onLoadMoreFinish(int size) {
        if (size > 0){
            Toast.makeText(this, "成功加载"+size+"条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多了~~", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinish(int size) {
        if (size > 0){
            Toast.makeText(this, "成功刷新"+size+"条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有节目了~~", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //这里面表示用户网络不佳，点击了重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClickListener(List<Track> detailList, int position) {
        //设置播放器的数据
        PlayerPresenter.getInstance().setPlayList(detailList, position);
        //跳转到播放器界面
        Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(List<Track> detailList, int position) {

    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停状态，文字修改为正在播放
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为播放状态，文字修改为已经暂停
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayState(false);
    }

    /**
     * 根据播放状态修改图标和文字
     *
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        //修改图标为播放状态，文字修改为已经暂停
        if (mPlayControlBtn != null && mPlayControlTv != null) {
            mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTv.setText(R.string.click_play_tips_text);
            } else {
                if (mCurrentTrackTitle != null) {
                    mPlayControlTv.setText(mCurrentTrackTitle);
                }
            }
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoad(List<Track> tracks) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChange(int currentIndex, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {

            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTv != null) {
                mPlayControlTv.setText(mCurrentTrackTitle);
            }

        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddSubscriptionResult(boolean isSuccess) {
        if (isSuccess) {
            updateSubState(mSubscriptionPresenter.isSub(mCurrentId));
        }
    }

    @Override
    public void onDeleteSubscriptionResult(boolean isSuccess) {
        if (isSuccess) {
            updateSubState(mSubscriptionPresenter.isSub(mCurrentId));
        }
    }

    @Override
    public void onGetSubscriptionResult(List<Album> albums) {
        updateSubState(mSubscriptionPresenter.isSub(mCurrentId));
    }
}
