package com.xagu.himalaya.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

import androidx.annotation.Nullable;

import com.xagu.himalaya.utils.Constants;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class HimalayaDBHelper extends SQLiteOpenHelper {
    public HimalayaDBHelper(@Nullable Context context) {
        //name:数据库名字   factory:游标工厂  version：版本号
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        //订阅相关的字段
        String subTbSql = "CREATE TABLE " + Constants.SUB_TB_NAME + "(" +
                Constants.SUB_ID + " integer primary key autoincrement," +
                Constants.SUB_COVER_URL + " varchar," +
                Constants.SUB_TITLE + " varchar," +
                Constants.SUB_DESCRIPTION + " varchar," +
                Constants.SUB_PLAY_COUNT + " integer," +
                Constants.SUB_TRACKS_COUNT + " integer," +
                Constants.SUB_AUTHOR_NAME + " varchar," +
                Constants.SUB_ALBUM_ID + " integer" +
                ")";
        db.execSQL(subTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
