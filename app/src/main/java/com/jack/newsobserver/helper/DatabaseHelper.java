package com.jack.newsobserver.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "newsobserverdb.db";
    private static final int SCHEMA = 4;
    static final String NEWS_TOPICS_TABLE = "topics";
    static final String TOPICS_CATEGORY_ID_COLUMN = "category_id";
    static final String TOPICS_LINK_COLUMN = "link";
    static final String NEWS_CATEGORY_TABLE = "category";
    static final String ID_COLUMN = "_id";
    static final String NAME_COLUMN = "name";
    static final String NEWS_LIST_TABLE = "newslist";
    static final String LIST_TOPIC_ID_COLUMN = "topic_id";
    static final String LIST_TITLE_COLUMN = "title";
    static final String LIST_AUTHOR_COLUMN = "author";
    static final String LIST_PUBDATE_COLUMN = "pubdate";
    static final String LIST_IMGURL_COLUMN = "imgurl";
    static final String LIST_LINK_COLUMN = "link";
    static final String LIST_WATCHED_COLUMN = "watcheddate";
    static final String LIST_FAVORITE_COLUMN = "favorite";
    private static final String TABLE_TEMP_PEFIX = "_temp";
    private static DatabaseHelper sDatabaseHelper;
    private String categoryCreateSql = "create table " + NEWS_CATEGORY_TABLE + "("
            + ID_COLUMN + " integer primary key autoincrement, "
            + NAME_COLUMN + " text not null" + ")";
    private String topicsCreateSql = "create table " + NEWS_TOPICS_TABLE + "("
            + ID_COLUMN + " integer primary key autoincrement, "
            + TOPICS_CATEGORY_ID_COLUMN + " integer not null "
            + "REFERENCES " + NEWS_CATEGORY_TABLE + " (" + ID_COLUMN + "), "
            + NAME_COLUMN + " text not null, "
            + TOPICS_LINK_COLUMN + " text not null" + ")";
    private String newslistCreateSql = "create table " + NEWS_LIST_TABLE + "("
            + ID_COLUMN + " integer primary key autoincrement, "
            + LIST_TOPIC_ID_COLUMN + " integer not null "
            + "REFERENCES " + NEWS_TOPICS_TABLE + " (" + ID_COLUMN + "), "
            + LIST_TITLE_COLUMN + " text not null, "
            + LIST_AUTHOR_COLUMN + " text not null, "
            + LIST_PUBDATE_COLUMN + " datetime, "
            + LIST_IMGURL_COLUMN + " text not null, "
            + LIST_LINK_COLUMN + " text not null, "
            + LIST_WATCHED_COLUMN + " datetime, "
            + LIST_FAVORITE_COLUMN + " boolean " + ")";

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
        db.execSQL(categoryCreateSql);
        db.execSQL(topicsCreateSql);
        db.execSQL(newslistCreateSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE " + NEWS_CATEGORY_TABLE + " RENAME TO " + NEWS_CATEGORY_TABLE + TABLE_TEMP_PEFIX);
        db.execSQL("ALTER TABLE " + NEWS_TOPICS_TABLE + " RENAME TO " + NEWS_TOPICS_TABLE + TABLE_TEMP_PEFIX);
        db.execSQL("ALTER TABLE " + NEWS_LIST_TABLE + " RENAME TO " + NEWS_LIST_TABLE + TABLE_TEMP_PEFIX);

        db.execSQL(categoryCreateSql);
        String categoryInsertSql = " insert into " + NEWS_CATEGORY_TABLE + "("
                + ID_COLUMN + ", "
                + NAME_COLUMN + ")" + " select "
                + ID_COLUMN + ", "
                + NAME_COLUMN + " from " + NEWS_CATEGORY_TABLE + TABLE_TEMP_PEFIX;
        db.execSQL(categoryInsertSql);

        db.execSQL(topicsCreateSql);
        String topicsInsertSql = " insert into " + NEWS_TOPICS_TABLE + "("
                + ID_COLUMN + ", "
                + TOPICS_CATEGORY_ID_COLUMN + ",  "
                + NAME_COLUMN + ", "
                + TOPICS_LINK_COLUMN + ")" + " select "
                + ID_COLUMN + ", "
                + TOPICS_CATEGORY_ID_COLUMN + ",  "
                + NAME_COLUMN + ", "
                + TOPICS_LINK_COLUMN + " from " + NEWS_TOPICS_TABLE + TABLE_TEMP_PEFIX;
        db.execSQL(topicsInsertSql);

        db.execSQL(newslistCreateSql);
        String newslistInsertSql = " insert into " + NEWS_LIST_TABLE + "("
                + ID_COLUMN + ", "
                + LIST_TOPIC_ID_COLUMN + ", "
                + LIST_TITLE_COLUMN + ", "
                + LIST_AUTHOR_COLUMN + ", "
                + LIST_PUBDATE_COLUMN + ", "
                + LIST_IMGURL_COLUMN + ", "
                + LIST_LINK_COLUMN + ", "
                + LIST_WATCHED_COLUMN + ")" + " select "
                + ID_COLUMN + ", "
                + LIST_TOPIC_ID_COLUMN + ", "
                + LIST_TITLE_COLUMN + ", "
                + LIST_AUTHOR_COLUMN + ", "
                + LIST_PUBDATE_COLUMN + ", "
                + LIST_IMGURL_COLUMN + ", "
                + LIST_LINK_COLUMN + ", "
                + LIST_WATCHED_COLUMN + " from " + NEWS_LIST_TABLE + TABLE_TEMP_PEFIX;
        db.execSQL(newslistInsertSql);

        db.execSQL("DROP TABLE IF EXISTS " + NEWS_LIST_TABLE + TABLE_TEMP_PEFIX);
        db.execSQL("DROP TABLE IF EXISTS " + NEWS_TOPICS_TABLE + TABLE_TEMP_PEFIX);
        db.execSQL("DROP TABLE IF EXISTS " + NEWS_CATEGORY_TABLE + TABLE_TEMP_PEFIX);
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
