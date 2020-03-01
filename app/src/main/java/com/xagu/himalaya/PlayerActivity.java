package com.xagu.himalaya;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.xagu.himalaya.adapters.PlayerTrackPagerAdapter;
import com.xagu.himalaya.base.BaseActivity;
import com.xagu.himalaya.interfaces.IPlayerCallback;
import com.xagu.himalaya.presenters.PlayerPresenter;
import com.xagu.himalaya.views.MarqueeTextView;
import com.xagu.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mTrackSeekBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouch = false;
    private ImageView mPlayPreBtn;
    private ImageView mPlayNextBtn;
    private MarqueeTextView mTrackTitle;
    private String mTrackTitleStr;
    private ViewPager mTrackPagerView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePage = false;
    private ImageView mPlayModeSwitch;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;

    static {
        //1、PLAY_MODEL_LIST
        //2、PLAY_MODEL_LIST_LOOP
        //3、PLAY_MODEL_RANDOM
        //4、PLAY_MODEL_SINGLE_LOOP
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST);
    }

    private ImageView mPlayListBtn;
    private SobPopWindow mSobPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //界面初始化以后再初始化数据
        mPlayerPresenter.getPlayList();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    /**
     * 给控件设置相关的事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变播放状态     暂停----》播放  播放----》暂停
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                }
            }
        });
        mTrackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouch = false;
                //手离开拖动进度条离开
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //T播放前一个节目
                mPlayerPresenter.playPre();
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个节目
                mPlayerPresenter.playNext();
            }
        });
        mTrackPagerView.addOnPageChangeListener(this);

        mTrackPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePage = true;
                        break;
                }
                return false;
            }
        });

        //播放模式切换
        mPlayModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放模式的切换
                //1、默认的是：PLAY_MODE_LIST
                //2、PLAY_MODE_LIST_LOOP
                //3、PLAY_MODE_RANDOM
                //4、PLAY_MODE_SINGLE_LOOP
                XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.switchPlayMode(playMode);
                }
            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //处理下背景，有点透明度
                updateBgAlpha(0.8f);
            }
        });

        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //POp消失以后恢复
                updateBgAlpha(1.0f);
            }
        });
    }

    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    private void updatePlayModeBtnImage(XmPlayListControl.PlayMode mode) {
        //根据当前状态更改模式图标
        switch (mode){
            case PLAY_MODEL_LIST:
                mPlayModeSwitch.setImageDrawable(getDrawable(R.drawable.selector_player_mode_list_order));
                break;
            case PLAY_MODEL_RANDOM:
                mPlayModeSwitch.setImageDrawable(getDrawable(R.drawable.selector_player_mode_random));
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayModeSwitch.setImageDrawable(getDrawable(R.drawable.selector_player_mode_list_loop));
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayModeSwitch.setImageDrawable(getDrawable(R.drawable.selector_player_mode_single_loop));
                break;
        }
    }

    /**
     * 找到各个控件
     */
    private void initView() {
        mTrackTitle = findViewById(R.id.track_title);
        mControlBtn = findViewById(R.id.play_or_pause_btn);
        mTotalDuration = findViewById(R.id.track_duration);
        //切换播放模式的按钮
        mPlayModeSwitch = findViewById(R.id.player_mode_switch);
        mCurrentPosition = findViewById(R.id.current_position);
        mTrackSeekBar = findViewById(R.id.track_seek_bar);
        mPlayPreBtn = findViewById(R.id.play_pre);
        mPlayNextBtn = findViewById(R.id.play_next);
        if (!TextUtils.isEmpty(mTrackTitleStr)) {
            mTrackTitle.setText(mTrackTitleStr);
        }
        mTrackPagerView = findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPagerView.setAdapter(mTrackPagerAdapter);
        //播放列表
        mPlayListBtn = findViewById(R.id.play_list);

        mSobPopWindow = new SobPopWindow();

    }

    //==============音乐控制相关Start========================================
    @Override
    public void onPlayStart() {
        //开始播放，修改播放按钮的状态
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        //暂停播放，修改播放按钮的状态
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        //停止播放，修改播放按钮的状态
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
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
        //把数据设置到适配器
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(tracks);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {
        updatePlayModeBtnImage(mode);
        mCurrentMode = mode;
    }

    @Override
    public void onProgressChange(int currentIndex, int total) {
        mTrackSeekBar.setMax(total);
        //更新播放进度，更新进度条
        //更新总时间
        String totalDuration;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
        } else {
            totalDuration = mMinFormat.format(total);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        //更新当前时间
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            currentPosition = mHourFormat.format(currentIndex);
        } else {
            currentPosition = mMinFormat.format(currentIndex);
        }
        if (currentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新进度条
        if (!mIsUserTouch) {
            mTrackSeekBar.setProgress(currentIndex);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackTitleStr = track.getTrackTitle();
        if (mTrackTitle != null) {
            mTrackTitle.setText(mTrackTitleStr);
        }
        //当前节目改变的时候，我们就获取当前播放器播放内容的位置
        //当前节目改变后修改页面图片
        if (mTrackPagerView != null) {
            mTrackPagerView.setCurrentItem(playIndex, true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //当页面选中的时候就去切换播放的内容
        if (mPlayerPresenter != null && mIsUserSlidePage) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePage = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //==============音乐控制相关Start========================================
}
