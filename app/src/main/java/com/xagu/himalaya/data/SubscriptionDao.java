package com.xagu.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.utils.Constants;
import com.xagu.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class SubscriptionDao implements ISubscriptionDao {
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private static final String TAG = "SubscriptionDao" ;
    private final HimalayaDBHelper mDbHelper;
    private ISubDaoCallback mCallback = null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mDbHelper = new HimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            //封装数据
            contentValues.put(Constants.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID,album.getId());
            db.insert(Constants.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
            isSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onAddResult(isSuccess);
            }
        }
    }

    @Override
    public void deleteAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.d(TAG,"db delete-->"+delete);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
            isSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onDeleteResult(isSuccess);
            }
        }
    }

    @Override
    public void getAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_TB_NAME, null, null, null, null, null, Constants.SUB_ID+" desc");
            //封装数据
            while (query.moveToNext()) {
                Album album = new Album();
                album.setCoverUrlLarge(query.getString(query.getColumnIndex(Constants.SUB_COVER_URL)));
                album.setAlbumTitle(query.getString(query.getColumnIndex(Constants.SUB_TITLE)));
                album.setAlbumIntro(query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION)));
                album.setIncludeTrackCount(query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT)));
                album.setPlayCount(query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT)));
                Announcer announcer = new Announcer();
                announcer.setNickname(query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME)));
                album.setAnnouncer(announcer);
                album.setId(query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID)));
                result.add(album);
            }
            db.setTransactionSuccessful();
            query.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            //把数据返回
            if (mCallback != null) {
                mCallback.onGetResult(result);
            }
        }
    }
}
