package com.jack.newsobserver.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "newsobserverdb.db";
    public static final int SCHEMA = 1;
    public static final String NEWS_TOPICS_TABLE = "topics";
    public static final String TOPICS_CATEGORY_ID_COLUMN = "category_id";
    public static final String TOPICS_LINK_COLUMN = "link";
    public static final String NEWS_CATEGORY_TABLE = "category";
    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String NEWS_LIST_TABLE = "newslist";
    public static final String LIST_TITLE_COLUMN = "title";
    public static final String LIST_AUTHOR_COLUMN = "author";
    public static final String LIST_PUBDATE_COLUMN = "pubdate";
    public static final String LIST_IMGURL_COLUMN = "imgurl";
    public static final String LIST_LINK_COLUMN = "link";
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
                + TOPICS_LINK_COLUMN + " text not null" + ")";;
        db.execSQL(topicsSql);
        String newslistSql = "create table " + NEWS_LIST_TABLE + "("
                + ID_COLUMN + " integer primary key autoincrement, "
                + LIST_TITLE_COLUMN + " text not null, "
                + LIST_AUTHOR_COLUMN + " text not null, "
                + LIST_PUBDATE_COLUMN + " text not null, "
                + LIST_IMGURL_COLUMN + " text not null, "
                + LIST_LINK_COLUMN + " text not null" + ")"; ;
        db.execSQL(newslistSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public Cursor createCursor(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public boolean initDataBase() {
        String query = "SELECT count(*) FROM topics";
        Cursor cursor = this.createCursor(query);
        int countRecord = 0;
        if (cursor.moveToFirst()) {
            countRecord = cursor.getInt(0);
        }
        cursor.close();
        return (countRecord == 0);
    }

    public void fillDataBaseFromUrl(String tableName, ContentValues newValues) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            db.insert(tableName, null, newValues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public void fillTablesFromHtml(ContentValues category, ArrayList<ContentValues> topics) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.beginTransaction();
            long categoryId = db.insert(NEWS_CATEGORY_TABLE, null, category);
            for (ContentValues topic : topics) {
                topic.put(TOPICS_CATEGORY_ID_COLUMN, categoryId);
                db.insert(NEWS_TOPICS_TABLE, null, topic);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public String getStringFromCursor(Cursor cursor, int row, String col) {
        String value;
        if (cursor != null) {
            cursor.moveToPosition(row);
            value = cursor.getString(cursor.getColumnIndex(col));
        } else {
            value = "empty cursor";
        }
        return value;
    }

    public Cursor getGroupCursor() {
        String query = "SELECT _id, name FROM category";
        return this.createCursor(query);
    }

    public Cursor getTopicDataByCategory(int id) {
        String query = "SELECT _id, name,link FROM topics WHERE category_id=" + String.valueOf(id);
        return this.createCursor(query);
    }

}
