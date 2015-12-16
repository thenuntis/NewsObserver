package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;


public class WebViewFragment extends Fragment {

    public static final String TAG = "WebViewFragmentTag";
    private String mSiteUrl;
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
        setHasOptionsMenu(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.main_drawer_layout);
        switch (item.getItemId()){
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_webview_fragment,container,false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (mNewUrlFlag){
            if (mWebViewBundle == null  ) {
                mWebView.loadUrl(mSiteUrl);
            }else {
                mWebView.restoreState(mWebViewBundle);
            }
        }else {
            mWebView.loadUrl(mSiteUrl);
        }

        return rootView;
    }
    public void setWebViewUrl (String url) {
        if (mSiteUrl !=null){
            mNewUrlFlag = mSiteUrl != url;
        }
        mSiteUrl = url;
    }

    @Override
    public void onPause() {
        super.onPause();
        mNewUrlFlag = true;
        mWebViewBundle =new Bundle();
        mWebView.saveState(mWebViewBundle);

    }
}
