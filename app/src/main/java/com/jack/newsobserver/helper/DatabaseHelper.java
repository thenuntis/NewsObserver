package com.jack.newsobserver.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "newsobserverdb.db";
    public static final int SCHEMA = 2;
    public static final String NEWS_TOPICS_TABLE = "topics";
    public static final String TOPICS_CATEGORY_ID_COLUMN = "category_id";
    public static final String TOPICS_LINK_COLUMN = "link";
    public static final String NEWS_CATEGORY_TABLE = "category";
    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String NEWS_LIST_TABLE = "newslist";
    public static final String LIST_TOPIC_ID_COLUMN = "topic_id";
    public static final String LIST_TITLE_COLUMN = "title";
    public static final String LIST_AUTHOR_COLUMN = "author";
    public static final String LIST_PUBDATE_COLUMN = "pubdate";
    public static final String LIST_IMGURL_COLUMN = "imgurl";
    public static final String LIST_LINK_COLUMN = "link";
    public static final String LIST_WATCHED_COLUMN = "watcheddate";
    private static DatabaseHelper sDatabaseHelper;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (null == sDatabaseHelper) {
            sDatabaseHelper = new DatabaseHelper(context);
        }
        return sDatabaseHelper;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        String categorySql = "create table " + NEWS_CATEGORY_TABLE + "("
                + ID_COLUMN + " integer primary key autoincrement, "
                + NAME_COLUMN + " text not null" + ")";
        db.execSQL(categorySql);
        String topicsSql = "create table " + NEWS_TOPICS_TABLE + "("
                + ID_COLUMN + " integer primary key autoincrement, "
                + TOPICS_CATEGORY_ID_COLUMN + " integer not null "
                + "REFERENCES " + NEWS_CATEGORY_TABLE + " (" + ID_COLUMN + "), "
                + NAME_COLUMN + " text not null, "
                + TOPICS_LINK_COLUMN + " text not null" + ")";
        db.execSQL(topicsSql);
        String newslistSql = "create table " + NEWS_LIST_TABLE + "("
                + ID_COLUMN + " integer primary key autoincrement, "
                + LIST_TOPIC_ID_COLUMN + " integer not null "
                + "REFERENCES " + NEWS_TOPICS_TABLE + " ("+ ID_COLUMN + "), "
                + LIST_TITLE_COLUMN + " text not null, "
                + LIST_AUTHOR_COLUMN + " text not null, "
                + LIST_PUBDATE_COLUMN + " datetime, "
                + LIST_IMGURL_COLUMN + " text not null, "
                + LIST_LINK_COLUMN + " text not null, "
                + LIST_WATCHED_COLUMN + " datetime" + ")";
        db.execSQL(newslistSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NEWS_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NEWS_TOPICS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NEWS_CATEGORY_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public Cursor createCursor(String query) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(query, null);
    }

}
