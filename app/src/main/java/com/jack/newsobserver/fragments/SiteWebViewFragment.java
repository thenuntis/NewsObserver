package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jack.newsobserver.R;


public class SiteWebViewFragment extends Fragment {

    public static final String TAG = "SiteWebViewFragmentTag";
    private String siteUrl;
    private WebView webView;
    private Bundle webViewBundle;

    public SiteWebViewFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.site_webview_fragment,container,false);
        webView = (WebView) rootView.findViewById(R.id.webView);
        if (webViewBundle == null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(siteUrl);
        }else {
            webView.restoreState(webViewBundle);
        }
        return rootView;
    }

    public void setWebViewUrl (String url) {
        siteUrl = url;
    }

    @Override
    public void onPause() {
        super.onPause();
        webViewBundle=new Bundle();
        webView.saveState(webViewBundle);
    }
}
