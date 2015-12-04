package com.jack.newsobserver.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;

import java.util.ArrayList;
import java.util.List;

public class TopicsDatabaseHelper {
    Context ctx;
    DatabaseHelper db = DatabaseHelper.getInstance(ctx);

    public TopicsDatabaseHelper(Context context) {
        this.ctx = context;
    }

    public void addTopic (NewsTopic topic){
        SQLiteDatabase helper = db.getReadableDatabase();
        helper.beginTransaction();
        try {

            long categoryId = addOrUpdateCategory(topic.getTopicCategory());

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.TOPICS_CATEGORY_ID_COLUMN, categoryId);
            values.put(DatabaseHelper.NAME_COLUMN, topic.getTopicName());
            values.put(DatabaseHelper.TOPICS_LINK_COLUMN, topic.getTopicLink());
            int rows = helper.update(DatabaseHelper.NEWS_TOPICS_TABLE,values,
                    DatabaseHelper.TOPICS_LINK_COLUMN + "= ?",new String[]{topic.getTopicLink()});
            if (rows != 1){
                helper.insertOrThrow(DatabaseHelper.NEWS_TOPICS_TABLE, null, values);
            }
            helper.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TopicDatabaseHelper", "Error while trying to add topic to database");
        } finally {
            helper.endTransaction();
        }
    }

    private long addOrUpdateCategory(NewsCategory category) {
        SQLiteDatabase helper = db.getReadableDatabase();
        long categoryId = -1;

        helper.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.NAME_COLUMN, category.getCategoryName());

            int rows = helper.update(DatabaseHelper.NEWS_CATEGORY_TABLE, values,
                        DatabaseHelper.NAME_COLUMN + "= ?", new String[]{category.getCategoryName()});
            if (rows == 1) {
                String categorySelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                                                            DatabaseHelper.ID_COLUMN,
                                                            DatabaseHelper.NEWS_CATEGORY_TABLE,
                                                            DatabaseHelper.NAME_COLUMN);
                Cursor cursor = helper.rawQuery(categorySelectQuery,
                                        new String[]{category.getCategoryName()});
                try {
                    if (cursor.moveToFirst()) {
                        categoryId = cursor.getInt(0);
                        helper.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                categoryId = helper.insertOrThrow(DatabaseHelper.NEWS_CATEGORY_TABLE, null, values);
                helper.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("TopicDatabaseHelper", "Error while trying to add or update category");
        } finally {
            helper.endTransaction();
        }
        return categoryId;
    }

    public List<NewsCategory> getCategories(){
        List<NewsCategory> categoriesList = new ArrayList<>();
        String query = "SELECT * FROM category";
        Cursor cursor = db.createCursor(query);
        while (cursor.moveToNext()){
            NewsCategory category = new NewsCategory();
            category.setCategoryId(cursor.getLong(0));
            category.setCategoryName(cursor.getString(1));
            categoriesList.add(category);
        }
        cursor.close();
        return categoriesList;
    }

    public List<NewsTopic> getTopicsByCategory(NewsCategory category){
        List<NewsTopic> topicsList = new ArrayList<>();
        String query = "SELECT * FROM topics WHERE category_id = " + category.getCategoryId();
        Cursor cursor = db.createCursor(query);
        while (cursor.moveToNext()){
            NewsTopic topic = new NewsTopic();
            topic.setTopicId(cursor.getInt(0));
            topic.setTopicCategory(category);
            topic.setTopicName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_COLUMN)));
            topic.setTopicLink(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOPICS_LINK_COLUMN)));
            topicsList.add(topic);
        }
        cursor.close();
        return topicsList;
    }
    public ArrayList<List<NewsTopic>> getAllTopics (){
        ArrayList<List<NewsTopic>> topicsList = new ArrayList<>();
        List<NewsCategory> categoryList = getCategories();
        for(NewsCategory category:categoryList){
            topicsList.add(getTopicsByCategory(category));
        }
        return topicsList;
    }
}
