package com.jack.newsobserver.activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.fragments.DrawerExpListFragment;
import com.jack.newsobserver.fragments.RecyclerViewFragment;
import com.jack.newsobserver.fragments.WebViewFragment;
import com.jack.newsobserver.util.Constants;


public class MainActivity extends ActionBarActivity implements
        DrawerExpListFragment.onSelectedExpListListener,RecyclerViewFragment.onMinimizingFinishedListener {

    private DrawerLayout mDrawerLayout;
    private static String subTitleString;
    private static String newsListUrl;
    private static long newsLinkId;
    private static final String SUBTITLE_KEY = "SUBTITLE";
    private static final String RECENT_LINK_URL_KEY = "LINKURLKEY";
    private static final String RECENT_LINK_ID_KEY = "LINKIDKEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (null != savedInstanceState){
            subTitleString =savedInstanceState.getString(SUBTITLE_KEY);
            newsListUrl =savedInstanceState.getString(RECENT_LINK_URL_KEY);
            newsLinkId = savedInstanceState.getLong(RECENT_LINK_ID_KEY);
        }else {
            if (null == newsListUrl){
                newsListUrl = Constants.DEFAULT_URL;
                newsLinkId = 1;
            }
        }
        FragmentManager manager = getFragmentManager();
        if (null == manager.findFragmentByTag(RecyclerViewFragment.TAG)){
            FragmentTransaction transaction = manager.beginTransaction();
            RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
            transaction.add(R.id.list_view_fragment, recyclerViewFragment, RecyclerViewFragment.TAG);
            recyclerViewFragment.setListUrl(newsListUrl, newsLinkId);
            transaction.commit();
        }
        mDrawerLayout=(DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
            }
            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (null == subTitleString){
            subTitleString = Constants.DEFAULT_SUBTITLE;
        }
        getSupportActionBar().setSubtitle(subTitleString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SUBTITLE_KEY, subTitleString);
        outState.putString(RECENT_LINK_URL_KEY, newsListUrl);
        outState.putLong(RECENT_LINK_ID_KEY, newsLinkId);
    }



    @Override
    public void onBackPressed() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getFragmentManager();
            if (manager.getBackStackEntryCount()>0){
                Fragment webViewFragment = manager.findFragmentByTag(WebViewFragment.TAG);
                if (null != webViewFragment){
                    WebView webView = (WebView) webViewFragment.getActivity().findViewById(R.id.webView);
                    if(webView.canGoBack()){
                        webView.goBack();
                    }else{
                        getSupportActionBar().setSubtitle(manager.getBackStackEntryAt(
                                manager.getBackStackEntryCount() - 1).getName());
                        manager.popBackStack();
                        enableDrawerButton();
                    }
                }
            }else {
                this.finish();
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
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
            case R.id.action_clear_history:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onExpandableChildItemClick(String title, String url, long id) {
        subTitleString = title;
        newsListUrl = url;
        newsLinkId = id;
        getSupportActionBar().setSubtitle(subTitleString);
        FragmentManager manager = getFragmentManager();
        RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) manager
                .findFragmentByTag(RecyclerViewFragment.TAG);
        WebViewFragment webViewFragment = (WebViewFragment) manager
                .findFragmentByTag(WebViewFragment.TAG);
        if (webViewFragment !=null && webViewFragment.isVisible()){
            recyclerViewFragment = new RecyclerViewFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.list_view_fragment, recyclerViewFragment, RecyclerViewFragment.TAG);
            transaction.addToBackStack(subTitleString);
            recyclerViewFragment.setListUrl(newsListUrl, newsLinkId);
            transaction.commit();
        }else {
            recyclerViewFragment.setListUrl(newsListUrl, newsLinkId);
            recyclerViewFragment.onRefresh();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void minimizingHtmlPageCallback(String htmlPageString, String primaryUrl) {

        disableDrawerButton();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        WebViewFragment webViewFragment = (WebViewFragment) manager.findFragmentByTag(WebViewFragment.TAG);
        if (webViewFragment != null) {
            webViewFragment.setWebViewUrl(htmlPageString,primaryUrl);
        }else {
            webViewFragment = new WebViewFragment();
        }
        transaction.replace(R.id.list_view_fragment, webViewFragment, WebViewFragment.TAG);
        transaction.addToBackStack(subTitleString);
        webViewFragment.setWebViewUrl(htmlPageString,primaryUrl);
        transaction.commit();
    }
    private void enableDrawerButton() {
        getSupportActionBar().setLogo(R.drawable.ic_actionbar_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void disableDrawerButton() {
        getSupportActionBar().setLogo(R.drawable.ic_actionbar_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

}