package com.xagu.himalaya.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xagu.himalaya.R;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();
    private static final String TAG = "RecommendListAdapter";
    private OnAlbumItemClickListener mItemClickListener = null;
    private OnAlbumItemLongClickListener mItemLongClickListener = null;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(TAG,"holder.itemView clicked" + (int)v.getTag());
                int index = (int)v.getTag();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(index,mData.get(index));
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int index = (int)v.getTag();
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(index,mData.get(index));
                }
                return true;
            }
        });
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
            DecimalFormat df=new DecimalFormat("0.00");
            albumPlayCountTv.setText(df.format(album.getPlayCount()/10000f)+"万");
            albumContentSizeTv.setText(album.getIncludeTrackCount()+"集");
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Glide.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
            } else {
                albumCoverIv.setImageResource(R.mipmap.logo);
            }
        }
    }

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnAlbumItemClickListener {
        public void onItemClick(int position, Album album);
    }

    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public interface OnAlbumItemLongClickListener {
        public void onItemLongClick(int position, Album album);
    }
}
