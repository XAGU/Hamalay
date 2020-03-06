package com.xagu.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xagu.himalaya.adapters.IndicatorAdapter;
import com.xagu.himalaya.adapters.MainContentAdapter;
import com.xagu.himalaya.base.BaseActivity;
import com.xagu.himalaya.data.HimalayaDBHelper;
import com.xagu.himalaya.interfaces.IPlayerCallback;
import com.xagu.himalaya.presenters.PlayerPresenter;
import com.xagu.himalaya.presenters.RecommendPresenter;
import com.xagu.himalaya.utils.LogUtil;
import com.xagu.himalaya.views.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends BaseActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mMainTrackCover;
    private TextView mMainTrackTitle;
    private TextView mMainTrackAuthor;
    private ImageView mMainTrackPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private RecommendPresenter mRecommendPresenter;
    private View mMainPlayControlItem;
    private View mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);

    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"click index is -->" + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
        mMainTrackPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //如果没有播放列表。就播放第一个推荐专辑
                        playFirstRecommend();
                    } else{
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });

        //点击播放控制器跳转播放器
        mMainPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //如果没有播放列表。就播放第一个推荐专辑
                        playFirstRecommend();
                    }
                }
                Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                startActivity(intent);
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
    }

    /**
     * 播放第一个推荐的内容
     */
    private void playFirstRecommend() {
        mRecommendPresenter = RecommendPresenter.getInstance();
        List<Album> currentRecommend = mRecommendPresenter.getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0){
            Album firstAlbum = currentRecommend.get(0);
            long albumId = firstAlbum.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);

        //ViewPager
        mContentPager = findViewById(R.id.content_pager);
        mContentPager.setOffscreenPageLimit(2);
        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContentPager.setAdapter(mainContentAdapter);

        //把ViewPager与indicator绑定到一起
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator,mContentPager);

        //播放控制相关
        mMainTrackCover = findViewById(R.id.main_track_cover);
        mMainTrackTitle = findViewById(R.id.main_track_title);
        mMainTrackTitle.setSelected(true);
        mMainTrackAuthor = findViewById(R.id.main_track_author);
        mMainTrackPlayControl = findViewById(R.id.main_track_play_control);
        mMainPlayControlItem = findViewById(R.id.main_play_control_item);
        mSearchBtn = findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    private void updatePlayControl(boolean isPlaying){
        mMainTrackPlayControl.setImageResource(isPlaying?R.drawable.selector_player_stop:R.drawable.selector_player_play);
    }

    @Override
    public void onPlayStart() {
        if (mMainTrackPlayControl != null) {
           updatePlayControl(true);
        }
    }

    @Override
    public void onPlayPause() {
        if (mMainTrackPlayControl != null) {
            updatePlayControl(false);
        }
    }

    @Override
    public void onPlayStop() {
        if (mMainTrackPlayControl != null) {
            updatePlayControl(false);
        }
    }

    @Override
    public void onPlayError() {
        if (mMainTrackPlayControl != null) {
            updatePlayControl(false);
        }
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
            String trackTitle = track.getTrackTitle();
            if (trackTitle != null) {
                mMainTrackTitle.setText(trackTitle);
            }
            String nickname = track.getAnnouncer().getNickname();
            if (nickname != null) {
                mMainTrackAuthor.setText(nickname);
            }
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (coverUrlMiddle != null) {
                Glide.with(this).load(coverUrlMiddle).into(mMainTrackCover);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
