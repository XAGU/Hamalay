package com.xagu.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xagu.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/2/28
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class AlbumDetailListAdapter extends RecyclerView.Adapter<AlbumDetailListAdapter.InnerHolder> {

    private List<Track> mDetailList = new ArrayList<>();
    //格式化时间
    SimpleDateFormat mUpdateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //找到控件
        View itemView = holder.itemView;
        //顺序ID
        TextView orderText = itemView.findViewById(R.id.order_text);
        //标题
        TextView detailTitle = itemView.findViewById(R.id.detail_item_title);
        //播放次数
        TextView detailPlayCount = itemView.findViewById(R.id.detail_item_play_count);
        //时长
        TextView detailPlayDuration = itemView.findViewById(R.id.detail_item_play_duration);
        //上传时间
        TextView detailUpdateTime = itemView.findViewById(R.id.detail_item_update_time);

        //设置数据
        Track track = mDetailList.get(position);
        if (orderText != null) {
            orderText.setText(position + 1 + "");
        }
        if (detailTitle != null) {
            detailTitle.setText(track.getTrackTitle());
        }
        if (detailPlayCount != null) {
            detailPlayCount.setText(track.getPlayCount() + "");
        }
        if (detailPlayDuration != null) {
            detailPlayDuration.setText(mDurationFormat.format(track.getDuration() * 1000));
        }
        String updateTimeText = mUpdateTimeFormat.format(track.getUpdatedAt());
        if (detailUpdateTime != null) {
            detailUpdateTime.setText(updateTimeText);
        }
        //设置Item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    //参数需要列表和位置
                    mItemClickListener.onItemClickListener(mDetailList,position);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    //参数需要列表和位置
                    mItemClickListener.onItemLongClickListener(mDetailList,position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailList.size();
    }

    public void setData(List<Track> tracks) {
        //清除原有数据
        mDetailList.clear();
        //更新数据
        mDetailList.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClickListener(List<Track> detailList, int position);
        void onItemLongClickListener(List<Track> detailList, int position);
    }
}
