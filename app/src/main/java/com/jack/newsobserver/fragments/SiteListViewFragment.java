package com.jack.newsobserver.fragments;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jack.newsobserver.ImageCache;
import com.jack.newsobserver.R;
import com.jack.newsobserver.SitesAdapter;
import com.jack.newsobserver.XmlNewsParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class SiteListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "SiteListViewFragmentTag";

    private SitesAdapter mAdapter;
    private ListView storiesList;
    static final String SITE_URL = "http://www.cbc.ca/cmlink/rss-topstories";
    static final String XML_FILE_NAME = "rss-topstories.xml";
    private SwipeRefreshLayout swipeLayout;

    public SiteListViewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView  = inflater.inflate(R.layout.site_listview_fragment, container, false);


            swipeLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);

            storiesList = (ListView)rootView.findViewById(R.id.storiesList);
            storiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = mAdapter.getItem(position).getStoryLink();
                    OnSelectedLinkListener listener = (OnSelectedLinkListener) getActivity();
                    listener.onListItemSelected(url);
                }
            });

            LoadNewsList();

            return rootView;
        }

    @Override
    public void onRefresh() {
        ImageCache.clearCache();
        LoadNewsList();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }

    public interface OnSelectedLinkListener {
        void onListItemSelected(String url);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
       // mAdapter.notifyDataSetChanged();
    }

    private void LoadNewsList() {
        if(isNetworkAvailable()){

            StoriesDownloadTask refreshing = new StoriesDownloadTask();
            refreshing.execute();
        }else {
            final Builder dialogMsg = new Builder(getActivity());
            dialogMsg.setTitle(R.string.dialogErrorTitle)
                    .setMessage(R.string.dialogErrorMsg);
            dialogMsg.setPositiveButton(R.string.dialogErrorPositiveBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoadNewsList();
                }
            });
            dialogMsg.setNegativeButton(R.string.dialogErrorNegativeBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogMsg.show();

            mAdapter = new SitesAdapter(getActivity(), -1, XmlNewsParser.getTopStories(getActivity()));
            storiesList.setAdapter(mAdapter);
            getActivity().setProgressBarVisibility(false);
        }
    }


    private class StoriesDownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                downloadFromUrl(SITE_URL, getActivity().openFileOutput(XML_FILE_NAME, Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void downloadFromUrl(String URL, FileOutputStream fos){
            try {
                    URL url = new URL(URL);
                    URLConnection ucon = url.openConnection();
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    byte data[] = new byte[1024];
                    int count;
                    while ((count = bis.read(data)) != -1) {
                        bos.write(data, 0, count);
                    }
                    bos.flush();
                    bos.close();
            }catch (IOException e) {
                    e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result){
            mAdapter = new SitesAdapter(getActivity(), -1, XmlNewsParser.getTopStories(getActivity()));
            storiesList.setAdapter(mAdapter);
            Log.i("StoriesDigest", "adapter size = " + mAdapter.getCount());
        }
    }



}
