package com.xagu.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xagu.himalaya.R;
import com.xagu.himalaya.adapters.PlayerListAdapter;
import com.xagu.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by XAGU on 2020/2/29
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayerListAdapter mPlayerListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mMPlayModeContainer;
    private OnPlayListActionClickListener mPlayModeClickListener;
    private View mPlayListDescContainer;
    private ImageView mPlayListDescIv;
    private TextView mPlayListDescTv;

    public SobPopWindow() {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //这里要注意，设置setOutsideTouchable(true);这个属性前，先要设置setBackgroundDrawable();否则点击外部无法关闭
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //载进来View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置进入离开弹窗的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initEvent() {
        //点击关闭关闭popWindow
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
        mMPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayModeClickListener.OnPlayModeClick();
            }
        });
        mPlayListDescContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表顺序（顺序或逆序）
                mPlayModeClickListener.OnOrderClick();
            }
        });
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close);
        //找到控件
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        mTrackList.setLayoutManager(new LinearLayoutManager(BaseApplication.getAppContext()));
        //设置适配器
        mPlayerListAdapter = new PlayerListAdapter();
        mTrackList.setAdapter(mPlayerListAdapter);
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.player_list_play_mode_iv);
        mMPlayModeContainer = mPopView.findViewById(R.id.play_list_play_container);
        mPlayListDescContainer = mPopView.findViewById(R.id.play_list_desc_container);
        mPlayListDescIv = mPopView.findViewById(R.id.play_list_desc_iv);
        mPlayListDescTv = mPopView.findViewById(R.id.play_list_desc_tv);
    }

    /**
     * 给适配器设置数据
     *
     * @param tracks
     */
    public void setListData(List<Track> tracks) {
        if (mPlayerListAdapter != null) {
            mPlayerListAdapter.setData(tracks);
        }
    }

    public void setCurrentPlayPosition(int position) {
        if (mPlayerListAdapter != null) {
            mPlayerListAdapter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mPlayerListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放列表的播放模式
     *
     * @param mode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode mode) {
        updatePlayModeBtnImageAndText(mode);
    }

    //更新切换列表顺序和逆序的UI
    public void updateOrderIcon(boolean isDesc) {
        mPlayListDescIv.setImageResource(isDesc ? R.drawable.selector_player_sort_desc : R.drawable.selector_player_sort_asc);
        mPlayListDescTv.setText(BaseApplication.getAppContext().getString(isDesc ? R.string.desc_text : R.string.asc_text));
    }

    private void updatePlayModeBtnImageAndText(XmPlayListControl.PlayMode mode) {
        int resId = R.drawable.selector_player_mode_list_order;
        int textId = R.string.play_mode_order_text;
        //根据当前状态更改模式图标
        switch (mode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_player_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_mode_list_loop;
                textId = R.string.play_mode_order_loop_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_mode_single_loop;
                textId = R.string.play_mode_single_loop_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    public interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    public void setOnPlayListActionClickListener(OnPlayListActionClickListener listener) {
        this.mPlayModeClickListener = listener;
    }

    public interface OnPlayListActionClickListener {
        //播放模式
        void OnPlayModeClick();

        //播放逆序或顺序按钮被点击
        void OnOrderClick();
    }

}
