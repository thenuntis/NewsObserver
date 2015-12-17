package com.jack.newsobserver.fragments;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.NewsListRecyclerAdapter;
import com.jack.newsobserver.helper.NewsListDatabaseHelper;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.models.NewsList;
import com.jack.newsobserver.parser.NewsListFromXmlParser;
import com.jack.newsobserver.util.ActionSearchUtil;
import com.jack.newsobserver.util.ImageCache;

import java.util.List;


public class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "RecyclerViewFragmentTag";
    public static final String ACTION_SEARCH_HINT = "Search";
    private NewsListRecyclerAdapter mRecAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private String mSiteUrl;
    private long mSiteId;
    private NewsListDatabaseHelper mNewsListDatabaseHelper;


    public RecyclerViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_recyclerview_fragment, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.newsList);
        recList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(layoutManager);
        mRecAdapter = new NewsListRecyclerAdapter(getActivity());
        recList.setAdapter(mRecAdapter);
        setNewsList();
        return rootView;
    }

    @Override
    public void onRefresh() {
        ImageCache.clearCache();
        loadNewsList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(ACTION_SEARCH_HINT);
        searchView.setOnQueryTextListener(new ActionSearchUtil(mRecAdapter));
        super.onCreateOptionsMenu(menu, inflater);

    }
    private void loadNewsList() {
        if (new TestNetwork(getActivity()).isNetworkAvailable()) {
            NewsListDownloadTask refreshing = new NewsListDownloadTask();
            refreshing.execute();
        } else {
            final Builder dialogMsg = new Builder(getActivity());
            dialogMsg.setTitle(R.string.dialogErrorTitle)
                    .setMessage(R.string.dialogErrorMsg);
            dialogMsg.setPositiveButton(R.string.dialogErrorPositiveRetryBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadNewsList();
                }
            });
            dialogMsg.setNegativeButton(R.string.dialogErrorNegativeBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogMsg.show();
            setNewsList();
        }
    }

    public void setNewsList() {
        if (null == mNewsListDatabaseHelper){
            mNewsListDatabaseHelper = new NewsListDatabaseHelper(getActivity());
        }
        List<NewsList> newsList = mNewsListDatabaseHelper.getNewsByTopic(mSiteId);
        if (0 == newsList.size()){
            loadNewsList();
        }else {
            mRecAdapter.updateList(newsList);
        }
    }



    private class NewsListDownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NewsList> newsList = new NewsListFromXmlParser().getNewsList(mSiteUrl,mSiteId);
            mNewsListDatabaseHelper.addNews(newsList);
            return null;
        }

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            setNewsList();
            mSwipeLayout.setRefreshing(false);
        }
    }
    public void setListUrl(String url, long id) {
        mSiteUrl = url;
        mSiteId = id;
    }

}
