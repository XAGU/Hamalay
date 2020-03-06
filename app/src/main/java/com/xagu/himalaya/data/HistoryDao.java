package com.xagu.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xagu.himalaya.base.BaseApplication;
import com.xagu.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XAGU on 2020/3/6
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class HistoryDao implements IHistoryDao {

    private final HimalayaDBHelper mDbHelper;
    private IHistoryDaoCallback mCallback = null;

    public HistoryDao() {
        mDbHelper = new HimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setHistoryCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.HIS_TRACK_ID, track.getDataId());
            contentValues.put(Constants.HIS_TITLE, track.getTrackTitle());
            contentValues.put(Constants.HIS_PLAY_COUNT, track.getPlayCount());
            contentValues.put(Constants.HIS_DURATION, track.getDuration());
            contentValues.put(Constants.HIS_UPDATE_TIME, track.getUpdatedAt());
            contentValues.put(Constants.HIS_COVER, track.getCoverUrlLarge());
            contentValues.put(Constants.HIS_AUTHOR, track.getAnnouncer().getNickname());
            db.insert(Constants.HIS_TB_NAME, null, contentValues);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryAdd(isSuccess);
            }
        }

    }

    @Override
    public void deleteHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constants.HIS_TB_NAME, Constants.HIS_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryDelete(isSuccess);
            }
        }
    }

    @Override
    public void listHistory() {
        SQLiteDatabase db = null;
        List<Track> tracks = new ArrayList<>();
        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor result = db.query(Constants.HIS_TB_NAME, null, null, null, null, null, "_id desc");
            while (result.moveToNext()) {
                Track track = new Track();
                track.setDataId(result.getInt(result.getColumnIndex(Constants.HIS_TRACK_ID)));
                track.setTrackTitle(result.getString(result.getColumnIndex(Constants.HIS_TITLE)));
                track.setPlayCount(result.getInt(result.getColumnIndex(Constants.HIS_PLAY_COUNT)));
                track.setDuration(result.getInt(result.getColumnIndex(Constants.HIS_DURATION)));
                track.setUpdatedAt(result.getLong(result.getColumnIndex(Constants.HIS_UPDATE_TIME)));
                track.setCoverUrlLarge(result.getString(result.getColumnIndex(Constants.HIS_COVER)));
                Announcer announcer = new Announcer();
                announcer.setNickname(result.getString(result.getColumnIndex(Constants.HIS_AUTHOR)));
                track.setAnnouncer(announcer);
                track.setPaid(false);
                track.setFree(true);
                track.setKind(PlayableModel.KIND_TRACK);
                tracks.add(track);
            }
            db.setTransactionSuccessful();
            result.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryList(tracks);
            }
        }
    }

    @Override
    public void clearHistory() {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
             db = mDbHelper.getWritableDatabase();
             db.beginTransaction();
             db.delete(Constants.HIS_TB_NAME,null,null);
             db.setTransactionSuccessful();
             isSuccess = true;
        } catch (Exception e){
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryClear(isSuccess);
            }
        }
    }
}
