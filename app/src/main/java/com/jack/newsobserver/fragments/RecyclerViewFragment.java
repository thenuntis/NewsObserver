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
import com.jack.newsobserver.adapter.NewsListAdapter;
import com.jack.newsobserver.helper.NewsListDatabaseHelper;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.models.NewsList;
import com.jack.newsobserver.parser.NewsListFromXmlParser;
import com.jack.newsobserver.util.ImageCache;

import java.util.List;


public class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "RecyclerViewFragmentTag";
    private static final String ACTION_SEARCH_HINT = "Search";
    private static final String FILTER_STRING = "filterString";
    private static final String EXPANDED_SEARCH_FIELD = "searchStatus";
    private static boolean searchStatus = true;
    private static String filterString ;
    private NewsListAdapter mRecAdapter;
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
        RecyclerView recyclerList = (RecyclerView) rootView.findViewById(R.id.newsList);
        recyclerList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(layoutManager);
        mRecAdapter = new NewsListAdapter(getActivity());
        recyclerList.setAdapter(mRecAdapter);
        if (null == savedInstanceState){
            setNewsList(null);
        }else {
            filterString = savedInstanceState.getString(FILTER_STRING);
            searchStatus = savedInstanceState.getBoolean(EXPANDED_SEARCH_FIELD,true);
            setNewsList(filterString);
        }
        return rootView;
    }

    @Override
    public void onRefresh() {
        ImageCache.clearCache();
        loadNewsList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(FILTER_STRING, filterString);
        outState.putBoolean(EXPANDED_SEARCH_FIELD,searchStatus);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconified(searchStatus);
        searchView.setQuery(filterString, false);
        searchView.setQueryHint(ACTION_SEARCH_HINT);
        searchView.setOnSearchClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStatus = searchView.isIconified();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchStatus = true;
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterString = s;
                setNewsList(filterString);
                return true;
            }
        });
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
            setNewsList(null);
        }
    }

    public void setNewsList(String searchText) {
        if (null == mNewsListDatabaseHelper){
            mNewsListDatabaseHelper = new NewsListDatabaseHelper(getActivity());
        }
        List<NewsList> newsList = mNewsListDatabaseHelper.getNewsList(mSiteId, searchText);
        if (0 == newsList.size() && filterString.length()==0){
            loadNewsList();
        }else {
            mRecAdapter.updateList(newsList,searchText);
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
            setNewsList(null);
            mSwipeLayout.setRefreshing(false);
        }
    }
    public void setListUrl(String url, long id) {
        mSiteUrl = url;
        mSiteId = id;
    }

}
