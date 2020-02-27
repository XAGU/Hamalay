package com.xagu.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.xagu.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] mTitles;
    private OnIndicatorTapClickListener mOnTapClickListener;

    public IndicatorAdapter(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_name);
    }

    @Override
    public int getCount() {
        if (mTitles != null) {
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建View
        ColorTransitionPagerTitleView  simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置颜色位灰色
        simplePagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //选中情况下为黑色
        simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //设置字体大小18sp
        simplePagerTitleView.setTextSize(18);
        //设置显示的内容
        simplePagerTitleView.setText(mTitles[index]);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击indicator的点击事件
                if (mOnTapClickListener != null) {
                    mOnTapClickListener.onTabClick(index);
                }
            }
        });
        return simplePagerTitleView;
    }


    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.parseColor("#ffffff"));
        return linePagerIndicator;
    }

    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnTapClickListener = listener;
    }

    public interface OnIndicatorTapClickListener {
        void onTabClick(int index);
    }
}
