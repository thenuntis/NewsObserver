package com.jack.newsobserver.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jack.newsobserver.models.NewsList;
import com.jack.newsobserver.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class NewsListDatabaseHelper {
    private DatabaseHelper db ;
    private DateTimeUtil mDateTimeUtil;

    public NewsListDatabaseHelper(Context context) {
        this.db = DatabaseHelper.getInstance(context);
        if (null == mDateTimeUtil){
            mDateTimeUtil = new DateTimeUtil(context);
        }
    }

    public void addNews (List<NewsList> list){
        SQLiteDatabase helper = db.getReadableDatabase();
        helper.beginTransaction();
        try {
            for (NewsList newsItem:list) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.LIST_TOPIC_ID_COLUMN,newsItem.getParentTopicId());
                values.put(DatabaseHelper.LIST_TITLE_COLUMN,newsItem.getStoryTitle());
                values.put(DatabaseHelper.LIST_AUTHOR_COLUMN,newsItem.getStoryAuthor());
                values.put(DatabaseHelper.LIST_PUBDATE_COLUMN,mDateTimeUtil.convertStringToMsec(newsItem.getStoryPubdate()));
                values.put(DatabaseHelper.LIST_LINK_COLUMN,newsItem.getStoryLink());
                values.put(DatabaseHelper.LIST_IMGURL_COLUMN,newsItem.getImgUrl());
                int rows = helper.update(DatabaseHelper.NEWS_LIST_TABLE,values,
                        DatabaseHelper.LIST_LINK_COLUMN + "= ?",new String[]{newsItem.getStoryLink()});
                if (rows != 1){
                    helper.insertOrThrow(DatabaseHelper.NEWS_LIST_TABLE, null, values);
                }
            }
            helper.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("NewsListDatabaseHelper", "Error while trying to add news to NewsList table");
        }finally {
                helper.endTransaction();
        }
    }
    public List<NewsList> getNewsList(long topicId, String searchText){
        String filtredText;
        if (null == searchText) {
            filtredText = "%";
        }else {
            filtredText = searchText;
        }
        List<NewsList> newsList = new ArrayList<>();
        String query = "SELECT * FROM "+DatabaseHelper.NEWS_LIST_TABLE
                + " WHERE " + DatabaseHelper.LIST_TOPIC_ID_COLUMN + " = " + topicId
                + " AND " + DatabaseHelper.LIST_TITLE_COLUMN + " LIKE '%" + filtredText + "%' "
                + " ORDER BY " + DatabaseHelper.LIST_PUBDATE_COLUMN + " DESC";
        Cursor cursor = db.createCursor(query);
        while (cursor.moveToNext()){
            NewsList newsItem = new NewsList();
            newsItem.setStoryId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID_COLUMN)));
            newsItem.setParentTopicId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.LIST_TOPIC_ID_COLUMN)));
            newsItem.setStoryTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LIST_TITLE_COLUMN)));
            newsItem.setStoryAuthor(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LIST_AUTHOR_COLUMN)));
            newsItem.setStoryPubdate(mDateTimeUtil.getStringFromMsec(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.LIST_PUBDATE_COLUMN))));
            newsItem.setStoryLink(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LIST_LINK_COLUMN)));
            newsItem.setImgUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LIST_IMGURL_COLUMN)));
            newsList.add(newsItem);
        }
        cursor.close();
        return newsList;
    }
}
