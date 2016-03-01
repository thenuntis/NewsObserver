package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;
import com.jack.newsobserver.interfaces.OnFavoriteCheckChangeListener;
import com.jack.newsobserver.interfaces.OnShareButtonClickListener;
import com.jack.newsobserver.manager.HtmlPageManager;
import com.jack.newsobserver.util.Constants;

import java.io.IOException;
import java.util.ArrayList;


public class WebViewFragment extends Fragment {

    public static final String TAG = "WebViewFragmentTag";
    private String mSiteUrl;
    private String mSiteData;
    private long mSiteId;
    private boolean mFavoriteChecked;
    private ArrayList<String> mHistoryList;
    private WebView mWebView;
    private ProgressBar mWebViewBar;
    private static final String SCROLL_VALUE = "scrollingPosition";
    private static final String HISTORY_LIST = "historyList";
    private static float mScrollPositionY;

    public WebViewFragment() {
        setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHistoryList = new ArrayList<>();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_webview_fragment, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        setWebViewSettings();
        Bundle mSavedBundle = getArguments();
        if (null == mHistoryList) {
            mHistoryList = mSavedBundle.getStringArrayList(HISTORY_LIST);
        }
        mScrollPositionY = mSavedBundle.getFloat(SCROLL_VALUE);
        mWebView.loadDataWithBaseURL(mSiteUrl, mSiteData, "text/html", "UTF-8", null);
        if (mHistoryList.isEmpty()) {
            mHistoryList.add(mSiteUrl);
        }
        ImageButton shareButton = (ImageButton) rootView.findViewById(R.id.web_view_share_btn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnShareButtonClickListener listener = (OnShareButtonClickListener) getActivity();
                listener.onShareButtonClick(mSiteUrl);
            }
        });
        CheckBox favoriteCheckbox = (CheckBox) rootView.findViewById(R.id.web_view_favorite_checkBox);
        favoriteCheckbox.setChecked(mFavoriteChecked);
        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFavoriteChecked = isChecked;
                OnFavoriteCheckChangeListener listener = (OnFavoriteCheckChangeListener) getActivity();
                listener.onFavoriteCheckChanged(mSiteId, isChecked);
            }
        });
        return rootView;
    }

    public void setWebViewParams(String data, String primaryUrl, long storyId, boolean favoriteFlag) {
        mSiteUrl = primaryUrl;
        mSiteData = data;
        mSiteId = storyId;
        mFavoriteChecked = favoriteFlag;
    }

    public boolean canGoPreviousPage() {
        return null != mHistoryList && 1 < mHistoryList.size();

    }

    public void goPreviousPage() {
        mHistoryList.remove(mHistoryList.size() - 1);
        loadPage(mHistoryList.get(mHistoryList.size() - 1));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getArguments().putFloat(SCROLL_VALUE, calcScrollPosition());
        getArguments().putStringArrayList(HISTORY_LIST, mHistoryList);

    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mWebViewBar = (ProgressBar) getActivity().findViewById(R.id.webViewBar);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebViewBar.setVisibility(View.INVISIBLE);
            if (0 != mScrollPositionY) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        float webviewsize = mWebView.getContentHeight();
                        float scrollCoef = (float) 1.3;
                        float scrollPosition;
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            scrollPosition = webviewsize * (mScrollPositionY * scrollCoef);
                        } else {
                            scrollPosition = webviewsize * (mScrollPositionY / scrollCoef);
                        }
                        int positionY = Math.round(scrollPosition);
                        mWebView.scrollTo(0, positionY);
                    }
                }, 100);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().contains(Constants.MAIN_HOST_NAME)) {
                loadPage(url);
                mHistoryList.add(url);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
            }
            return true;
        }
    }

    private void loadPage(String url) {
        MinimizeHtmlPageTask minimizeHtmlPageTask = new MinimizeHtmlPageTask();
        minimizeHtmlPageTask.execute(url);
    }

    private float calcScrollPosition() {
        float contentHeight = mWebView.getContentHeight();
        float currentScrollPosition = mWebView.getScrollY();
        return (currentScrollPosition) / contentHeight;
    }

    private void setWebViewSettings() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        mWebView.setWebViewClient(new myWebViewClient());
    }

    private class MinimizeHtmlPageTask extends AsyncTask<String, Void, Void> {
        private String url;

        @Override
        protected Void doInBackground(String... params) {
            url = params[0];
            try {
                mSiteData = HtmlPageManager.getHtmlPage(getActivity(), url);
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

