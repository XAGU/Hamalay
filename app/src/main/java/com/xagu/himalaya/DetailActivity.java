package com.xagu.himalaya;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.xagu.himalaya.base.BaseActivity;
import com.xagu.himalaya.interfaces.IAlbumDetailPresenter;
import com.xagu.himalaya.interfaces.IAlbumDetailViewCallback;
import com.xagu.himalaya.presenters.AlbumDetailPresenter;
import com.xagu.himalaya.utils.ImageBlur;
import com.xagu.himalaya.views.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by XAGU on 2020/2/27
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private IAlbumDetailPresenter mAlbumDetailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
    }

    private void initView() {
        mLargeCover = findViewById(R.id.iv_large_cover);
        mSmallCover = findViewById(R.id.riv_small_cover);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mAlbumAuthor = findViewById(R.id.tv_album_author);
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {

    }

    @Override
    public void onAlbumLoaded(Album album) {
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做毛玻璃效果
        if (mLargeCover != null) {
            Glide.with(this).load(album.getCoverUrlLarge()).apply(RequestOptions.bitmapTransform(new BlurTransformation(14,3))).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Glide.with(this).load(album.getCoverUrlSmall()).into(mSmallCover);
        }

    }
}
