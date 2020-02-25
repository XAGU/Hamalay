package com.xagu.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xagu.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里是找到view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //这里是封装数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到这个控件，设置值
            //专辑的封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //专辑的标题
            TextView albumTitleTv = itemView.findViewById(R.id.album_title);
            //专辑的描述
            TextView albumDescTv = itemView.findViewById(R.id.album_desc);
            //专辑的播放量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑的数量
            TextView albumContentSizeTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDescTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount()+"");
            albumContentSizeTv.setText(album.getIncludeTrackCount()+"");

            Glide.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);
        }
    }
}
