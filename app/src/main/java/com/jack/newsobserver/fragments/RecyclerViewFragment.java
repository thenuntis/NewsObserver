package com.jack.newsobserver.fragments;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
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
import android.widget.Toast;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.NewsListAdapter;
import com.jack.newsobserver.helper.NewsListDatabaseHelper;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.models.NewsList;
import com.jack.newsobserver.parser.NewsHtmlPageMinimizer;
import com.jack.newsobserver.parser.NewsListFromXmlParser;
import com.jack.newsobserver.util.ImageCache;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        NewsListAdapter.OnSelectedListItemListener {

    public static final String TAG = "RecyclerViewFragmentTag";
    private static final String ACTION_SEARCH_HINT = "Search";
    private static final String FILTER_STRING = "filterString";
    private static final String EXPANDED_SEARCH_FIELD = "searchStatus";
    private static final String UNAVAILABLE_TOPIC_MSG = "This News Topic Unavailable";
    private static final String HISTORY_CLEAR_MSG = "History Was Cleared";
    private static boolean searchStatus = true;
    private static String filterString;
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
        mRecAdapter = new NewsListAdapter(getActivity(), this);
        recyclerList.setAdapter(mRecAdapter);
        mNewsListDatabaseHelper = new NewsListDatabaseHelper(getActivity());
        if (null == savedInstanceState) {
            setNewsList(null);
        } else {
            filterString = savedInstanceState.getString(FILTER_STRING);
            searchStatus = savedInstanceState.getBoolean(EXPANDED_SEARCH_FIELD, true);
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
        outState.putBoolean(EXPANDED_SEARCH_FIELD, searchStatus);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem resetHistoryItem = menu.findItem(R.id.action_clear_history);
        resetHistoryItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ClearVisitsHistory resetHistory = new ClearVisitsHistory();
                resetHistory.execute();
                return false;
            }
        });
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

    public void setNewsList(String searchText) {
        if (null == mNewsListDatabaseHelper) {
            mNewsListDatabaseHelper = new NewsListDatabaseHelper(getActivity());
        }
        List<NewsList> newsList = mNewsListDatabaseHelper.getNewsList(mSiteId, searchText);
        if (0 == newsList.size() && null == searchText) {
            loadNewsList();
        } else {
            mRecAdapter.updateList(newsList, searchText);
        }
    }

    public void setListUrl(String url, long id) {
        mSiteUrl = url;
        mSiteId = id;
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


    @Override
    public void onListItemSelected(NewsList newsList) {
        SetNewsAsWatchedTask setWatchedTask = new SetNewsAsWatchedTask();
        setWatchedTask.execute(newsList.getStoryId());
        MinimizeHtmlPageTask minimizeHtmlPageTask = new MinimizeHtmlPageTask();
        minimizeHtmlPageTask.execute(newsList.getStoryLink());
    }

    private class NewsListDownloadTask extends AsyncTask<Void, Void, List<NewsList>> {

        @Override
        protected List<NewsList> doInBackground(Void... arg0) {
            List<NewsList> newsList = null;

            try {
                newsList = new NewsListFromXmlParser().getNewsList(mSiteUrl, mSiteId);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            }
            if (null != newsList) {
                mNewsListDatabaseHelper.addNews(newsList);
            }
            return newsList;
        }

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<NewsList> newsList) {
            if (null != newsList) {
                setNewsList(null);
            } else {
                newsList = new ArrayList<>();
                mRecAdapter.updateList(newsList, null);
                Toast.makeText(getActivity(), UNAVAILABLE_TOPIC_MSG, Toast.LENGTH_SHORT).show();
            }
            mSwipeLayout.setRefreshing(false);
        }
    }


    private class MinimizeHtmlPageTask extends AsyncTask<String, Void, String> {
        private String primaryUrl;

        @Override
        protected String doInBackground(String... params) {
            primaryUrl = params[0];
            try {
                return NewsHtmlPageMinimizer.getMinimizedHtml(primaryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String htmlPageString) {
            if (null == htmlPageString) {
                htmlPageString = primaryUrl;
            }
            onMinimizingFinishedListener listener = (onMinimizingFinishedListener) getActivity();
            listener.minimizingHtmlPageCallback(htmlPageString, primaryUrl);
        }
    }

    private class SetNewsAsWatchedTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            try {
                mNewsListDatabaseHelper.setWatched(params[0]);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface onMinimizingFinishedListener {
        void minimizingHtmlPageCallback(String htmlPageString, String primaryUrl);
    }

    private class ClearVisitsHistory extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mNewsListDatabaseHelper.clearWatchedDate();
            mRecAdapter.updateList(mNewsListDatabaseHelper.getNewsList(mSiteId, filterString), filterString);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getActivity(), HISTORY_CLEAR_MSG, Toast.LENGTH_SHORT).show();
        }
    }
}
