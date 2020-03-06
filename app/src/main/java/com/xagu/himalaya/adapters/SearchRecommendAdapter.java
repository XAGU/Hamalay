package com.xagu.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xagu.himalaya.R;
import com.xagu.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/3/4
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {

    private List<QueryResult> mData = new ArrayList<>();
    private OnItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_search_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        TextView text = holder.itemView.findViewById(R.id.search_recommend_item);
        final QueryResult result = mData.get(position);
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(result.getKeyword());
                }
            }
        });
        text.setText(result.getKeyword());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置数据
     * @param result
     */
    public void setData(List<QueryResult> result) {
        mData.clear();
        mData.addAll(result);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onClick(String keyword);
    }
}
