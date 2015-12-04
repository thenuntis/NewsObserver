package com.jack.newsobserver.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.jack.newsobserver.helper.TopicsDatabaseHelper;
import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetDataFromHtmlManager extends AsyncTask <String, Void, Void> {
    Context mContext;
    private static final String MAIN_HTML_ELEMENT = "table.feeds";
    private static final String MAIN_HTML_TITLE = "td.title";
    private static final String MAIN_HTML_CONTENT = "td.content";
    private static final String URL_CONTENT_TEXT = "a";
    private static final String URL_CONTENT_LINK = "href";

    private OnFillFinished mCallBack ;


    public GetDataFromHtmlManager(Context context, OnFillFinished callBack) {
        this.mContext = context;
        this.mCallBack=callBack;
    }

    @Override
    protected Void doInBackground(String... params) {
        Document doc;
//        DatabaseHelper mDatabaseHelper = DatabaseHelper.getInstance(mContext);
//        ContentValues newCategoryNames = new ContentValues();
        TopicsDatabaseHelper topicsDatabaseHelper = new TopicsDatabaseHelper(mContext);
        try {
            doc  = Jsoup.connect(params[0]).get();
            Elements categories = doc.select(MAIN_HTML_ELEMENT);
            for (Element category : categories){
                Element categoryElement = doc.select(MAIN_HTML_ELEMENT).get(categories.indexOf(category));
                Document bodyOfCategory = Jsoup.parse(String.valueOf(categoryElement));
                String categoryTitle = bodyOfCategory.select(MAIN_HTML_TITLE).first().text();
//                newCategoryNames.put(DatabaseHelper.NAME_COLUMN, categoryTitle);
                NewsCategory mCategory = new NewsCategory();
                mCategory.setCategoryName(categoryTitle);
                Elements topics = bodyOfCategory.select(MAIN_HTML_CONTENT);
/*                ArrayList<ContentValues> newTopicsList = new ArrayList<>();*/
                for (Element topic:topics){
                    NewsTopic mTopic= new NewsTopic();
//                    ContentValues newTopicsValues = new ContentValues();
                    Document bodyOfTopic = Jsoup.parse(String.valueOf(topic));
                    Elements topicContent = bodyOfTopic.select(URL_CONTENT_TEXT);
                    mTopic.setTopicCategory(mCategory);
                    mTopic.setTopicName(topicContent.text());
                    mTopic.setTopicLink(topicContent.attr(URL_CONTENT_LINK));
                    topicsDatabaseHelper.addTopic(mTopic);
/*
                    newTopicsValues.put(DatabaseHelper.NAME_COLUMN, topicContent.text());
                    newTopicsValues.put(DatabaseHelper.TOPICS_LINK_COLUMN, topicContent.attr(URL_CONTENT_LINK));
                    newTopicsList.add(newTopicsValues);*/
                }
//                mDatabaseHelper.fillTablesFromHtml(newCategoryNames, newTopicsList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mCallBack.callBack();
    }

    public interface OnFillFinished {
        void callBack();
    }
}
