package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;
import com.jack.newsobserver.manager.HtmlPageManager;
import com.jack.newsobserver.util.Constants;

import java.io.IOException;
import java.util.ArrayList;


public class WebViewFragment extends Fragment {

    public static final String TAG = "WebViewFragmentTag";
    private String mSiteUrl;
    private String mSiteData;
    private ArrayList<String> mHistoryList;
    private WebView mWebView;
    private ProgressBar mWebViewBar;

    public WebViewFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_webview_fragment,container,false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new myWebViewClient());
        mHistoryList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        mWebView.loadDataWithBaseURL(mSiteUrl, mSiteData, "text/html", "UTF-8", null);
        mHistoryList.add(mSiteUrl);
        return rootView;
    }

    public void setWebViewUrl (String data,String primaryUrl) {
        mSiteUrl = primaryUrl;
        mSiteData= data;
    }

    public boolean canGoPreviousPage(){
        return 1 < mHistoryList.size();
    }
    public void goPreviousPage (){
        mHistoryList.remove(mHistoryList.size() - 1);
        loadPage(mHistoryList.get(mHistoryList.size() - 1));
    }
    private class myWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mWebViewBar = (ProgressBar) getActivity().findViewById(R.id.webViewBar);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            mWebViewBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Uri.parse(url).getHost().contains(Constants.MAIN_HOST_NAME)) {
                loadPage(url);
                mHistoryList.add(url);
            }else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
            }
            return true;
        }
    }

    private void loadPage (String url) {
        MinimizeHtmlPageTask minimizeHtmlPageTask = new MinimizeHtmlPageTask();
        minimizeHtmlPageTask.execute(url);
    }

    private class MinimizeHtmlPageTask extends AsyncTask<String, Void, Void> {
        private String url;
        @Override
        protected Void doInBackground(String... params) {
            url = params[0];
            try {
                mSiteData=HtmlPageManager.getHtmlPage(getActivity(), url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mWebView.loadDataWithBaseURL(url, mSiteData, "text/html", "UTF-8", null);
        }
    }

}

