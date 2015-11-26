package com.jack.newsobserver.manager;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.jack.newsobserver.activity.MainActivity;
import com.jack.newsobserver.helper.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetDataFromHtmlManager extends AsyncTask <String, Void, Void> {
//    DatabaseHelper mDatabaseHelper;
    MainActivity mActivity;
    private static final String MAIN_HTML_ELEMENT = "table.feeds";
    private OnFillFinished mCallBack ;

    public GetDataFromHtmlManager(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        Document doc;
        DatabaseHelper mDatabaseHelper = new DatabaseHelper(mActivity);
        ContentValues newCategoryNames = new ContentValues();
        ContentValues newTopicsNames = new ContentValues();
        ContentValues newTopicsCategoryIds = new ContentValues();
        ContentValues newTopicsLinks = new ContentValues();
        try {
            doc  = Jsoup.connect(params[0]).get();
            Elements feedElements = doc.select(MAIN_HTML_ELEMENT);
            int mCategoryValue = 1;
            for (Element elemA : feedElements){
                Element elementsA = doc.select(MAIN_HTML_ELEMENT).get(feedElements.indexOf(elemA));
                Document docA = Jsoup.parse(String.valueOf(elementsA));
                String elemB = docA.select("td.title").first().text();
                String contentKey = String.valueOf(mCategoryValue);
                newCategoryNames.put(contentKey, elemB);
                Elements elemC = docA.select("td.content");
                for (Element elemD:elemC){
                    Document docB = Jsoup.parse(String.valueOf(elemD));
                    Elements elemE = docB.select("a");
                    newTopicsCategoryIds.put(contentKey, mCategoryValue);
                    newTopicsNames.put(contentKey, elemE.text());
                    newTopicsLinks.put(contentKey, elemE.attr("href"));
                }
                mCategoryValue++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDatabaseHelper.fillTablesFromHtml(newCategoryNames, newTopicsCategoryIds,
                    newTopicsNames, newTopicsLinks);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mCallBack = new OnFillFinished() {
            @Override
            public void callBack(boolean isFinish) {

            }
        };
        mCallBack.callBack(true);
//       mActivity.onGetDataFromHtmlDone();
    }

    public interface OnFillFinished {
        void callBack(boolean isFinish);
    }
}
