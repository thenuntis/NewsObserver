package com.jack.newsobserver.manager;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.jack.newsobserver.activity.MainActivity;
import com.jack.newsobserver.helper.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetDataFromHtmlManager extends AsyncTask <String, Void, Void> {
//    SQLiteDatabase mBase;
    DatabaseHelper mDatabaseHelper;
    MainActivity mActivity;
    private static final String MAIN_HTML_ELEMENT = "table.feeds";

    public GetDataFromHtmlManager(DatabaseHelper mDbHelper, MainActivity activity) {
//      this.mBase  = base;
        this.mDatabaseHelper=mDbHelper;
        this.mActivity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        Document doc;
        try {
            doc  = Jsoup.connect(params[0]).get();
            Elements feedElements = doc.select(MAIN_HTML_ELEMENT);
            ContentValues newCategoryValues = new ContentValues();
            int mCategoryValue = 1;
            for (Element elemA : feedElements){
                Element elementsA = doc.select(MAIN_HTML_ELEMENT).get(feedElements.indexOf(elemA));
                Document docA = Jsoup.parse(String.valueOf(elementsA));
                String elemB = docA.select("td.title").first().text();
                newCategoryValues.put(DatabaseHelper.NAME_COLUMN, elemB);
                mDatabaseHelper.fillDataBaseFromUrl(DatabaseHelper.NEWS_CATEGORY_TABLE, newCategoryValues);
                Elements elemC = docA.select("td.content");
                ContentValues newTopicsValues = new ContentValues();
                for (Element elemD:elemC){
                    Document docB = Jsoup.parse(String.valueOf(elemD));
                    Elements elemE = docB.select("a");
                    String mElemValue = elemE.text();
                    String linkElem = elemE.attr("href");
                    newTopicsValues.put(DatabaseHelper.TOPICS_CATEGORY_ID_COLUMN, mCategoryValue);
                    newTopicsValues.put(DatabaseHelper.NAME_COLUMN, mElemValue);
                    newTopicsValues.put(DatabaseHelper.TOPICS_LINK_COLUMN, linkElem);
                    mDatabaseHelper.fillDataBaseFromUrl(DatabaseHelper.NEWS_TOPICS_TABLE, newTopicsValues);

                }
                mCategoryValue++;
//                mDatabaseHelper.fillDataBaseFromUrl(newCategoryValues,newTopicsValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("GetDataFromHtmlManager","------");
       mActivity.onGetDataFromHtmlDone();
    }
}
