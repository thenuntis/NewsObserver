package com.jack.newsobserver.fragments;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jack.newsobserver.ImageCache;
import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.NewsListAdapter;
import com.jack.newsobserver.helper.NewsListDatabaseHelper;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.models.NewsList;
import com.jack.newsobserver.parser.NewsListFromXmlParser;

import java.util.List;


public class SiteListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "SiteListViewFragmentTag";
    private static final String LOADING_NEWS_LIST_MSG = "Loading news...";
    private NewsListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeLayout;
    private String mSiteUrl;
    private long mSiteId;
    private NewsListDatabaseHelper mNewsListDatabaseHelper;


    public SiteListViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.site_listview_fragment, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        ListView storiesList = (ListView) rootView.findViewById(R.id.storiesList);
        mAdapter = new NewsListAdapter(getActivity());
        storiesList.setAdapter(mAdapter);
        storiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mAdapter.getItem(position).getStoryLink();
                OnSelectedLinkListener listener = (OnSelectedLinkListener) getActivity();
                listener.onListItemSelected(url);
            }
        });
        setNewsList();
        return rootView;
    }

    @Override
    public void onRefresh() {
        ImageCache.clearCache();
        loadNewsList();
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
            mAdapter.setSites(newsList);
        }
    }

    public interface OnSelectedLinkListener {
        void onListItemSelected(String url);
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
//            if (null == mProgressDialog && !mSwipeLayout.isRefreshing()){
//                mProgressDialog = new ProgressDialog(getActivity());
//                mProgressDialog.setMessage(LOADING_NEWS_LIST_MSG);
//                mProgressDialog.show();
//            }
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            setNewsList();
            mSwipeLayout.setRefreshing(false);
//            if(null != mProgressDialog && mProgressDialog.isShowing()){
//                mProgressDialog.dismiss();
//                mProgressDialog=null;
//            }
        }
    }
    public void setListViewUrl (String url,long id) {
        mSiteUrl = url;
        mSiteId = id;
    }

}
