package com.xagu.himalaya.utils;

/**
 * Created by XAGU on 2020/2/25
 * Email:xagu_qc@foxmail.com
 * Describe:
 */
public class Constants {

    //获取推荐列表的专辑数量
    public final static int COUNT_RECOMMEND = 50;

    //默认列表的请求数量
    public final static int COUNT_DEFAULT = 50;

    //默认获取热词的数量
    public final static int COUNT_HOT_WORD = 10;

    //数据库相关的常量
    //数据库名
    public final static String DB_NAME = "himalaya.db";
    //数据的版本
    public final static int DB_VERSION = 1;
    //订阅表相关
    public final static String SUB_TB_NAME = "subTb";
    public final static String SUB_ID = "_id";
    public final static String SUB_COVER_URL = "coverUrl";
    public final static String SUB_TITLE = "title";
    public final static String SUB_DESCRIPTION = "description";
    public final static String SUB_PLAY_COUNT = "playCount";
    public final static String SUB_TRACKS_COUNT = "tracksCount";
    public final static String SUB_AUTHOR_NAME = "authorName";
    public final static String SUB_ALBUM_ID = "albumId";

    //历史表相关
    public final static String HIS_TB_NAME = "hisTb";
    public final static String HIS_ID = "_id";
    public final static String HIS_TRACK_ID = "trackId";
    public final static String HIS_TITLE = "title";
    public final static String HIS_PLAY_COUNT = "playCount";
    public final static String HIS_DURATION = "duration";
    public final static String HIS_UPDATE_TIME = "updateTime";
    public final static String HIS_COVER = "cover";
    public final static String HIS_AUTHOR = "author";

    //历史记录最大条数
    public final static int MAX_HISTORY_COUNT = 100;

}
