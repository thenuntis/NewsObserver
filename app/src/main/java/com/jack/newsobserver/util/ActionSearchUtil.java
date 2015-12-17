package com.jack.newsobserver.util;

import android.support.v7.widget.SearchView;

import com.jack.newsobserver.adapter.NewsListRecyclerAdapter;

public class ActionSearchUtil implements SearchView.OnQueryTextListener {

    private NewsListRecyclerAdapter mRecAdapter;

    public ActionSearchUtil (NewsListRecyclerAdapter adapter){
        this.mRecAdapter = adapter;
    }

    public boolean onQueryTextChange(String text_new) {
        if (text_new.length()!=0) {
            mRecAdapter.filter(text_new);
            return true;
        }else return false;
    }

    public boolean onQueryTextSubmit(String text) {
        return true;
    }

}
