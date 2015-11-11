package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;


public class SiteWebViewFragment extends Fragment {

    public static final String TAG = "SiteWebViewFragmentTag";
    private String mSiteUrl;
    private WebView mWebView;
    private Bundle mWebViewBundle;
    private ProgressBar mWebViewBar;

    public SiteWebViewFragment(){
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
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mWebViewBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.site_webview_fragment,container,false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        if (mWebViewBundle == null) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mSiteUrl);
        }else {
            mWebView.restoreState(mWebViewBundle);
        }
        return rootView;
    }
    public void setWebViewUrl (String url) {
        mSiteUrl = url;
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebViewBundle =new Bundle();
        mWebView.saveState(mWebViewBundle);
    }

/*    @Override
    public void onStop() {
        super.onStop();
        FragmentManager manager = getFragmentManager();
        Log.i("", manager.popBackStack());
    }*/
}
