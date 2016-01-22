package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;
import com.jack.newsobserver.parser.NewsHtmlPageMinimizer;
import com.jack.newsobserver.util.Constants;

import java.io.IOException;


public class WebViewFragment extends Fragment {

    public static final String TAG = "WebViewFragmentTag";
    private String mSiteUrl;
    private String mSiteData;
    private WebView mWebView;
    private Bundle mWebViewBundle;
    private ProgressBar mWebViewBar;
    private boolean mNewUrlFlag = true;

    public WebViewFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebViewBar = (ProgressBar) getActivity().findViewById(R.id.webViewBar);
        mWebView.setWebViewClient(new myWebViewClient());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_webview_fragment,container,false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        if (mNewUrlFlag){
            if (mWebViewBundle == null  ) {
                Log.w("WEBVIEW", "FIRST_BLOOD");
                if(null != savedInstanceState){
                    Log.w("WEBVIEW", "INSTANCE");
                    mWebView.restoreState(mWebViewBundle);
//                    mWebView.restoreState(savedInstanceState);
                }else {
                    Log.w("WEBVIEW","ZERO INST");
                    mWebView.loadDataWithBaseURL("", mSiteData, "text/html", "UTF-8", "");
                }
            }else {
                Log.w("WEBVIEW1","ROTATE"+mSiteData);
                Log.w("WEBVIEW2","ROTATE"+savedInstanceState);
                Log.w("WEBBUNDLE>>", "ROTATE" + String.valueOf(mWebViewBundle));
                mWebView.restoreState(mWebViewBundle);
//                mWebView.restoreState(savedInstanceState);
            }
        }else {
            mWebView.loadDataWithBaseURL(null, mSiteData, null, null, null);
        }

        return rootView;
    }
    public void setWebViewUrl (String data,String primaryUrl) {
        if (mSiteUrl !=null){
            mNewUrlFlag = mSiteUrl != primaryUrl;
        }
        mSiteUrl = primaryUrl;
        mSiteData= data;

    }

    @Override
    public void onPause() {

        super.onPause();
        mNewUrlFlag = true;
        mWebViewBundle =new Bundle();
        Log.w("WEB_PAUSE", String.valueOf(mWebViewBundle));
        mWebView.saveState(mWebViewBundle);

    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            mWebViewBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Uri.parse(url).getHost().contains(Constants.MAIN_HOST_NAME)) {
                mSiteUrl=url;
                MinimizeHtmlPageTask minimizeHtmlPageTask = new MinimizeHtmlPageTask();
                minimizeHtmlPageTask.execute(url);
                return true;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
    private class MinimizeHtmlPageTask extends AsyncTask<String, Void, Void> {
        private String url;

        @Override
        protected Void doInBackground(String... params) {
            url = params[0];
            try {
                mSiteData=NewsHtmlPageMinimizer.getMinimizedHtml(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mWebView.loadDataWithBaseURL(null, mSiteData, null, null, null);
        }
    }

}

