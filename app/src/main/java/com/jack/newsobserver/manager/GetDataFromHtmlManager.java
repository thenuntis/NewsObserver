package com.jack.newsobserver.manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.jack.newsobserver.helper.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetDataFromHtmlManager extends AsyncTask <String, Void, Void> {
    SQLiteDatabase mBase;
    private static final String MAIN_HTML_ELEMENT = "table.feeds";

    public GetDataFromHtmlManager(SQLiteDatabase base) {
      this.mBase  = base;
    }

    @Override
    protected Void doInBackground(String... params) {
        Document doc;
        try {
            doc  = Jsoup.connect(params[0]).get();
            Elements feedElements = doc.select(MAIN_HTML_ELEMENT);
            ContentValues newCategoryValues = new ContentValues();
            for (Element elemA : feedElements){
                Element elementsA = doc.select(MAIN_HTML_ELEMENT).get(feedElements.indexOf(elemA));
                Document docA = Jsoup.parse(String.valueOf(elementsA));
                String elemB = docA.select("td.title").first().text();
                newCategoryValues.put(DatabaseHelper.NAME_COLUMN, elemB);
                mBase.beginTransaction();
                try {
                    long mLongValue = mBase.insert(DatabaseHelper.NEWS_CATEGORY_TABLE, null, newCategoryValues);
                    Elements elemC = docA.select("td.content");
                    ContentValues newTopicsValues = new ContentValues();
                    for (Element elemD:elemC){
                        Document docB = Jsoup.parse(String.valueOf(elemD));
                        Elements elemE = docB.select("a");
                        String mElemValue = elemE.text();
                        String linkElem = elemE.attr("href");
                        newTopicsValues.put(DatabaseHelper.TOPICS_CATEGORY_ID_COLUMN, mLongValue);
                        newTopicsValues.put(DatabaseHelper.NAME_COLUMN, mElemValue);
                        newTopicsValues.put(DatabaseHelper.TOPICS_LINK_COLUMN, linkElem);
                        mBase.insert(DatabaseHelper.NEWS_TOPICS_TABLE, null, newTopicsValues);
                    }
                mBase.setTransactionSuccessful();
                }finally {
                    mBase.endTransaction();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
