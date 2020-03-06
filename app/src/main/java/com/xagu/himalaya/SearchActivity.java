package com.xagu.himalaya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.xagu.himalaya.adapters.AlbumListAdapter;
import com.xagu.himalaya.adapters.SearchRecommendAdapter;
import com.xagu.himalaya.base.BaseActivity;
import com.xagu.himalaya.interfaces.ISearchCallback;
import com.xagu.himalaya.presenters.AlbumDetailPresenter;
import com.xagu.himalaya.presenters.SearchPresenter;
import com.xagu.himalaya.utils.LogUtil;
import com.xagu.himalaya.views.FlowTextLayout;
import com.xagu.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

    private static final String TAG = "SearchActivity";
    private View mSearchBackBtn;
    private EditText mSearchInput;
    private TextView mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private InputMethodManager mInputMethodManager;
    private ImageView mSearchInputDelete;
    private RecyclerView mRecommendListSearch;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mSearchResultRefreshLayout;

    private boolean needSuggestion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //注册UI更新的接口
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initView() {
        mSearchBackBtn = findViewById(R.id.search_back);
        mSearchInput = findViewById(R.id.search_input);
        mSearchInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchInput.requestFocus();
                //显示键盘
                mInputMethodManager.showSoftInput(mSearchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
        mSearchBtn = findViewById(R.id.search_btn);
        mResultContainer = findViewById(R.id.search_container);
        mSearchInputDelete = findViewById(R.id.search_input_delete);
        mSearchInputDelete.setVisibility(View.GONE);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);
        }
    }

    /**
     * 创建数据请求成功的View
     *
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        //刷新控件
        mSearchResultRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        mSearchResultRefreshLayout.setEnableRefresh(false);
        //显示热词的
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word);
        //显示搜索结果的
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        mResultListView.setLayoutManager(new LinearLayoutManager(this));
        //设置适配器
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
                //super.getItemOffsets(outRect, view, parent, state);
            }
        });
        mResultListView.setAdapter(mAlbumListAdapter);

        //显示关键字推荐的
        mRecommendListSearch = resultView.findViewById(R.id.recommend_list_search);
        //设置布局管理器
        mRecommendListSearch.setLayoutManager(new LinearLayoutManager(this));
        //设置适配器
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mRecommendListSearch.setAdapter(mSearchRecommendAdapter);


        return resultView;
    }

    private void initEvent() {
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });

        mSearchBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mSearchInput.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    Toast.makeText(SearchActivity.this, "搜索词不能为空~~", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSearchPresenter.doSearch(keyword);
                mUILoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    hideSuccessView();
                    mFlowTextLayout.setVisibility(View.VISIBLE);
                    mSearchInputDelete.setVisibility(View.GONE);
                } else {
                    mSearchInputDelete.setVisibility(View.VISIBLE);
                    if (needSuggestion) {
                        //触发联想查询
                        getSuggestWord(s.toString());
                    } else {
                        needSuggestion = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //不需要相关的联想词
                needSuggestion = false;
                //推荐热词的点击
                switch2Search(text);
            }
        });

        mSearchInputDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInput.setText("");
            }
        });

        mSearchRecommendAdapter.setOnItemClickListener(new SearchRecommendAdapter.OnItemClickListener() {
            @Override
            public void onClick(String keyword) {
                //不需要相关的联想词
                needSuggestion = false;
                switch2Search(keyword);
            }
        });

        mSearchResultRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //加载更多的内容
                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }
            }
        });

        mAlbumListAdapter.setOnAlbumItemClickListener(new AlbumListAdapter.OnAlbumItemClickListener() {
            @Override
            public void onItemClick(int position, Album album) {
                AlbumDetailPresenter.getInstance().setTargetAlbum(album);
                //item被点击跳转到详情页面
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void switch2Search(String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "搜索词不能为空~~", Toast.LENGTH_SHORT).show();
            return;
        }
        //第一步，把热词扔到输入框里
        mSearchInput.setText(text, TextView.BufferType.EDITABLE);
        mSearchInput.setSelection(text.length());
        //第二步，搜索热词
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(text);
        }
        //第三步，改变UI状态
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    /**
     * 获取联想的关键词
     *
     * @param keyword
     */
    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyword);
        }
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        hideSuccessView();
        //隐藏热词
        if (mSearchResultRefreshLayout != null) {
            mSearchResultRefreshLayout.setVisibility(View.VISIBLE);
        }
        //隐藏键盘
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(mSearchInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        handleSearchResult(result);
    }

    private void handleSearchResult(List<Album> result) {
        if (result != null) {
            if (result.size() == 0) {
                //数据为空
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            } else {
                //数据不为空，就设置数据
                mAlbumListAdapter.setData(result);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> result) {
        hideSuccessView();
        //显示搜索结果
        if (mFlowTextLayout != null) {
            mFlowTextLayout.setVisibility(View.VISIBLE);
        }
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        LogUtil.d(TAG, "onHotWordLoaded result size-->" + result.size());
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : result) {
            hotWords.add(hotWord.getSearchword());
        }
        Collections.sort(hotWords);
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        //处理加载更多的结果
        if (mSearchResultRefreshLayout != null) {
            mSearchResultRefreshLayout.finishLoadmore();
        }
        if (isOkay) {
            handleSearchResult(result);
        } else {
            Toast.makeText(this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> result) {
        //联想相关的关键字
        LogUtil.d(TAG, result.size() + "");
        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setData(result);
        }
        //控制UI的状态隐藏和显示
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //控制显示和隐藏
        hideSuccessView();
        mRecommendListSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView() {
        mRecommendListSearch.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
        mSearchResultRefreshLayout.setVisibility(View.GONE);
    }
}
