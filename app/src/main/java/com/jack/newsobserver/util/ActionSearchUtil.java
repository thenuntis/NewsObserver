package com.jack.newsobserver.util;

import android.support.v7.widget.SearchView;

import com.jack.newsobserver.adapter.NewsListAdapter;

public class ActionSearchUtil implements SearchView.OnQueryTextListener {

    private NewsListAdapter mAdapter;

    public ActionSearchUtil(NewsListAdapter adapter) {
        this.mAdapter = adapter;

    }

    public boolean onQueryTextChange(String text_new) {
        if (text_new.length()!=0) {
            mAdapter.filter(text_new);
            return true;
        }else return false;
    }

    public boolean onQueryTextSubmit(String text) {
        return true;
    }

}
