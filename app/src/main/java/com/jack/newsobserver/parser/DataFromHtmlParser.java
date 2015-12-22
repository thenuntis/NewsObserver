package com.jack.newsobserver.parser;

import android.content.Context;
import android.os.AsyncTask;

import com.jack.newsobserver.helper.DatabaseHelper;
import com.jack.newsobserver.helper.TopicsDatabaseHelper;
import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class DataFromHtmlParser extends AsyncTask <String, Void, Void> {
    Context mContext;
    private static final String MAIN_HTML_ELEMENT = "table.feeds";
    private static final String MAIN_HTML_TITLE = "td.title";
    private static final String MAIN_HTML_CONTENT = "td.content";
    private static final String URL_CONTENT_TEXT = "a";
    private static final String URL_CONTENT_LINK = "href";

    private OnFillFinished mCallBack ;
    private TopicsDatabaseHelper mDatabaseHelper;


    public DataFromHtmlParser(Context context, OnFillFinished callBack) {
        this.mContext = context;
        this.mCallBack=callBack;
    }
    
/*    @Override
    protected Void doInBackground(String... params) {
        Document doc;
        DatabaseHelper.getInstance(mContext);
        TopicsDatabaseHelper topicsDatabaseHelper = new TopicsDatabaseHelper(mContext);
        try {
            doc  = Jsoup.connect(params[0]).get();
            Elements categories = doc.select(MAIN_HTML_ELEMENT);
            for (Element category : categories){
                Element categoryElement = doc.select(MAIN_HTML_ELEMENT).get(categories.indexOf(category));
                Document bodyOfCategory = Jsoup.parse(String.valueOf(categoryElement));
                String categoryTitle = bodyOfCategory.select(MAIN_HTML_TITLE).first().text();
                NewsCategory mCategory = new NewsCategory();
                mCategory.setCategoryName(categoryTitle);
                Elements topics = bodyOfCategory.select(MAIN_HTML_CONTENT);

                ArrayList<NewsTopic> newTopicsList = getTopicsArrayList(topics);
//                ArrayList<NewsTopic> newTopicsList = new ArrayList<>();
//                for (Element topic:topics){
//                    NewsTopic mTopic= new NewsTopic();
//                    Document bodyOfTopic = Jsoup.parse(String.valueOf(topic));
//                    Elements topicContent = bodyOfTopic.select(URL_CONTENT_TEXT);
//                    mTopic.setTopicName(topicContent.text());
//                    mTopic.setTopicLink(topicContent.attr(URL_CONTENT_LINK));
//                    newTopicsList.add(mTopic);
//                }
                mCategory.setCategoryTopics(newTopicsList);
//                topicsDatabaseHelper.addCategoryAndRelatedTopics(mCategory);
                addCategoryToDb(mCategory,topicsDatabaseHelper);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
    @Override
    protected Void doInBackground(String... params) {
        DatabaseHelper.getInstance(mContext);
        try {
            Document htmlDocument = Jsoup.connect(params[0]).get();
            parseDocument(htmlDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseDocument(Document htmlDocument) {
        Elements categories = htmlDocument.select(MAIN_HTML_ELEMENT);
        for (Element category : categories){
            Element categoryElement = htmlDocument.select(MAIN_HTML_ELEMENT).get(categories.indexOf(category));
            NewsCategory categoryItem = getFilledCategory(categoryElement);
            addCategoryToDb(categoryItem);
        }
    }

    private NewsCategory getFilledCategory(Element categoryElement) {
        NewsCategory category = new NewsCategory();
        Document bodyOfCategory = Jsoup.parse(String.valueOf(categoryElement));
        String categoryTitle = bodyOfCategory.select(MAIN_HTML_TITLE).first().text();
        category.setCategoryName(categoryTitle);
        Elements topics = bodyOfCategory.select(MAIN_HTML_CONTENT);
        ArrayList<NewsTopic> newTopicsList = getTopicsArrayList(topics);
        category.setCategoryTopics(newTopicsList);
        return category;
    }

    private void addCategoryToDb(NewsCategory mCategory) {
        if (null == mDatabaseHelper){
            mDatabaseHelper = new TopicsDatabaseHelper(mContext);
        }
        mDatabaseHelper.addCategoryAndRelatedTopics(mCategory);
    }

    private ArrayList<NewsTopic> getTopicsArrayList(Elements topics) {
        ArrayList<NewsTopic> newTopicsList = new ArrayList<>();
        for (Element topic:topics){
            NewsTopic mTopic= new NewsTopic();
            Document bodyOfTopic = Jsoup.parse(String.valueOf(topic));
            Elements topicContent = bodyOfTopic.select(URL_CONTENT_TEXT);
            mTopic.setTopicName(topicContent.text());
            mTopic.setTopicLink(topicContent.attr(URL_CONTENT_LINK));
            newTopicsList.add(mTopic);
        }
        return newTopicsList;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mCallBack.callBack();
    }

    public interface OnFillFinished {
        void callBack();
    }


}
