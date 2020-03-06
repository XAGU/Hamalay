package com.xagu.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.xagu.himalaya.DetailActivity;
import com.xagu.himalaya.R;
import com.xagu.himalaya.adapters.AlbumListAdapter;
import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.base.BaseFragment;
import com.xagu.himalaya.interfaces.ISubscriptionCallback;
import com.xagu.himalaya.presenters.AlbumDetailPresenter;
import com.xagu.himalaya.presenters.SubscriptionPresenter;
import com.xagu.himalaya.views.ConfirmDialog;
import com.xagu.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnAlbumItemClickListener, AlbumListAdapter.OnAlbumItemLongClickListener {
    private View mRootView;
    private RecyclerView mRecommendRv;
    private AlbumListAdapter mRecommendListAdapter;
    private TwinklingRefreshLayout mOverScrollView;
    private SubscriptionPresenter mSubscriptionPresenter;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        mUiLoader = new UILoader(getActivity()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }

            @Override
            protected View getEmptyView() {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                TextView emptyText = view.findViewById(R.id.empty_text);
                emptyText.setText("没有订阅内容哦！去订阅一个再来吧~~");
                return view;
            }
        };
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }
        initPresenter();
        return mUiLoader;
    }

    private void initPresenter() {
        mSubscriptionPresenter = SubscriptionPresenter.getsInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mSubscriptionPresenter.getSubscription();
    }


    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //RecycleView的使用
        //1.找到控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        mOverScrollView = mRootView.findViewById(R.id.over_scroll);
        mOverScrollView.setPureScrollModeOn();
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
                //super.getItemOffsets(outRect, view, parent, state);
            }
        });
        //3.设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnAlbumItemClickListener(this);
        mRecommendListAdapter.setOnAlbumItemLongClickListener(this);
        return mRootView;
    }

    @Override
    public void onAddSubscriptionResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteSubscriptionResult(boolean isSuccess) {
        Toast.makeText(BaseApplication.getAppContext(), "取消订阅" + (isSuccess ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetSubscriptionResult(List<Album> albums) {
        //设置数据
        //当我们获取到推荐内容的时候，这个方法就会被调用（成功了）
        //数据回来以后，就是更新UI
        //把数据设置给适配器，并且更新UI
        if (albums.size() > 0) {
            mRecommendListAdapter.setData(albums);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        } else {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击跳转到详情页面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position, final Album album) {
        final ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setOnBtnClickListener(new ConfirmDialog.OnBtnClickListener() {
            @Override
            public void onConfirmClick() {
                mSubscriptionPresenter.deleteSubscription(album);
            }

            @Override
            public void onCancelClick() {
            }
        });
        confirmDialog.show();
    }
}
