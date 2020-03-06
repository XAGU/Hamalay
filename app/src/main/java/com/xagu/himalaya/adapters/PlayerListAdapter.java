package com.xagu.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xagu.himalaya.R;
import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/3/1
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.InnerHolder> {

    private List<Track> mTracks = new ArrayList<>();
    private int playingIndex = 0;
    private SobPopWindow.OnListItemClickListener mItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_track_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //拿到数据
        Track track = mTracks.get(position);
        //找到控件
        TextView trackTitle = holder.itemView.findViewById(R.id.track_title_tv);
        //设置数据、
        trackTitle.setText(track.getTrackTitle());
        //设置字体颜色
        trackTitle.setTextColor(BaseApplication.getAppContext().getResources().getColor(playingIndex == position ?R.color.main_color:R.color.sub_text_title));
        //找播放状态的图标
        View playingIconView = holder.itemView.findViewById(R.id.play_icon_iv);
        playingIconView.setVisibility(playingIndex == position ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onListItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public void setData(List<Track> tracks) {
        //设置数据更新列表
        mTracks.clear();
        mTracks.addAll(tracks);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.OnListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex = position;
        notifyDataSetChanged();
    }
}
