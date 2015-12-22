package com.jack.newsobserver.util;

import android.content.Context;
import android.support.v7.widget.SearchView;

import com.jack.newsobserver.helper.NewsListDatabaseHelper;

public class ActionSearchUtil implements SearchView.OnQueryTextListener {

//    private NewsListAdapter mRecAdapter;
//    private long mTopicId;
    Context mContext;
    NewsListDatabaseHelper mListHelper;

    public ActionSearchUtil (Context context){
//        this.mRecAdapter = adapter;
        this.mContext = context;

    }

    public boolean onQueryTextChange(String text_new) {
        if (null == mListHelper) {
            NewsListDatabaseHelper mListHelper = new NewsListDatabaseHelper(mContext);
        }
//        mRecAdapter.filter(text_new);
        return true;
    }

    public boolean onQueryTextSubmit(String text) {
        return true;
    }

}
