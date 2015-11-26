package com.jack.newsobserver.manager;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.jack.newsobserver.helper.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GetDataFromHtmlManager extends AsyncTask <String, Void, Void> {
    Context mContext;
    private static final String MAIN_HTML_ELEMENT = "table.feeds";
    private OnFillFinished mCallBack ;

    public GetDataFromHtmlManager(Context context, OnFillFinished callBack) {
        this.mContext = context;
        this.mCallBack=callBack;
    }

    @Override
    protected Void doInBackground(String... params) {
        Document doc;
        DatabaseHelper mDatabaseHelper = new DatabaseHelper(mContext);
        ContentValues newCategoryNames = new ContentValues();
        try {
            doc  = Jsoup.connect(params[0]).get();
            Elements feedElements = doc.select(MAIN_HTML_ELEMENT);
            for (Element elemA : feedElements){
                Element elementsA = doc.select(MAIN_HTML_ELEMENT).get(feedElements.indexOf(elemA));
                Document docA = Jsoup.parse(String.valueOf(elementsA));
                String elemB = docA.select("td.title").first().text();
                newCategoryNames.put(DatabaseHelper.NAME_COLUMN, elemB);
                Elements elemC = docA.select("td.content");
                ArrayList<ContentValues> newTopicsList = new ArrayList<>();
                for (Element elemD:elemC){
                    ContentValues newTopicsValues = new ContentValues();
                    Document docB = Jsoup.parse(String.valueOf(elemD));
                    Elements elemE = docB.select("a");
                    newTopicsValues.put(DatabaseHelper.NAME_COLUMN, elemE.text());
                    newTopicsValues.put(DatabaseHelper.TOPICS_LINK_COLUMN, elemE.attr("href"));
                    newTopicsList.add(newTopicsValues);
                }
                mDatabaseHelper.fillTablesFromHtml(newCategoryNames, newTopicsList);
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
