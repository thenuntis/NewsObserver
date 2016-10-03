package com.jack.newsobserver.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import com.jack.newsobserver.R;
import com.jack.newsobserver.fragments.DrawerExpListFragment;
import com.jack.newsobserver.fragments.RecyclerViewFragment;
import com.jack.newsobserver.fragments.WebViewFragment;
import com.jack.newsobserver.helper.NewsListDatabaseHelper;
import com.jack.newsobserver.interfaces.OnFavoriteCheckChangeListener;
import com.jack.newsobserver.interfaces.OnShareButtonClickListener;
import com.jack.newsobserver.manager.HtmlPageManager;
import com.jack.newsobserver.util.Constants;


public class MainActivity extends ActionBarActivity implements
        DrawerExpListFragment.onSelectedExpListListener, RecyclerViewFragment.onMinimizingFinishedListener,
        DrawerExpListFragment.onFavoritesClickListener, OnShareButtonClickListener, OnFavoriteCheckChangeListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mFavoriteFlag;
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
        if (null != savedInstanceState) {
            subTitleString = savedInstanceState.getString(SUBTITLE_KEY);
            newsListUrl = savedInstanceState.getString(RECENT_LINK_URL_KEY);
            newsLinkId = savedInstanceState.getLong(RECENT_LINK_ID_KEY);
        } else {
            if (null == newsListUrl) {
                newsListUrl = Constants.DEFAULT_URL;
                newsLinkId = 1;
            }
        }
        initNewsListFragment();
        initDrawerFragment();
        initActionBar();

    }

    private void initNewsListFragment() {
        FragmentManager manager = getFragmentManager();
        if (null == manager.findFragmentByTag(RecyclerViewFragment.TAG)) {
            FragmentTransaction transaction = manager.beginTransaction();
            RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
            transaction.add(R.id.list_view_fragment, recyclerViewFragment, RecyclerViewFragment.TAG);
            recyclerViewFragment.setListUrl(newsListUrl, newsLinkId);
            transaction.commit();
        }
    }

    private void initActionBar() {
        if (null == subTitleString) {
            subTitleString = Constants.DEFAULT_SUBTITLE;
        }
        getSupportActionBar().setSubtitle(subTitleString);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawerFragment() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        WebViewFragment webViewFragment = (WebViewFragment) manager.findFragmentByTag(WebViewFragment.TAG);
        if (null != webViewFragment) {
            webViewFragmentMenuActivate();
        }
        if (null != mDrawerToggle) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            if (manager.getBackStackEntryCount() > 0) {
                WebViewFragment webViewFragment = (WebViewFragment) manager.findFragmentByTag(WebViewFragment.TAG);
                if (null != webViewFragment) {
                    if (webViewFragment.canGoPreviousPage()) {
                        webViewFragment.goPreviousPage();
                    } else {
                        getSupportActionBar().setSubtitle(manager.getBackStackEntryAt(
                                manager.getBackStackEntryCount() - 1).getName());
                        HtmlPageManager.clearStoredHtmlPages(this);
                        manager.popBackStack();
                        enableDrawer();
                    }
                }
            } else {
                this.finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
                FragmentManager manager = getFragmentManager();
                WebViewFragment webViewFragment = (WebViewFragment) manager.findFragmentByTag(WebViewFragment.TAG);
                if (null == webViewFragment) {
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                } else {
                    onBackPressed();
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
        recyclerViewFragment.setListUrl(newsListUrl, newsLinkId);
        recyclerViewFragment.onRefresh();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void minimizingHtmlPageCallback(String htmlPageString, String primaryUrl, long storyId) {
        webViewFragmentMenuActivate();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter, R.animator.exit);
        WebViewFragment webViewFragment = (WebViewFragment) manager.findFragmentByTag(WebViewFragment.TAG);
        if (null == webViewFragment) {
            webViewFragment = new WebViewFragment();
        }
        transaction.replace(R.id.list_view_fragment, webViewFragment, WebViewFragment.TAG);
        transaction.addToBackStack(subTitleString);
        webViewFragment.setWebViewParams(htmlPageString, primaryUrl, storyId, mFavoriteFlag);
        transaction.commit();
    }

    private void enableDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.setHomeAsUpIndicator(getApplicationContext().getResources().getDrawable(R.drawable.ic_drawer));
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    private void webViewFragmentMenuActivate() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_left));
    }

    @Override
    public void onDrawerFavoritesItemClick(String title, long newsId) {
        subTitleString = title;
        newsLinkId = newsId;
        getSupportActionBar().setSubtitle(subTitleString);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        FragmentManager manager = getFragmentManager();
        RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) manager
                .findFragmentByTag(RecyclerViewFragment.TAG);
        recyclerViewFragment.showFavorites();
    }

    @Override
    public void onShareButtonClick(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent, "Share link"));
    }

    @Override
    public void onFavoriteCheckChanged(long newsItemId, boolean flag) {
        mFavoriteFlag = flag;
        MarkAsFavoriteTask markAsFavoriteTask = new MarkAsFavoriteTask(flag);
        markAsFavoriteTask.execute(newsItemId);
    }

    private class MarkAsFavoriteTask extends AsyncTask<Long, Void, Void> {
        boolean favoritesFlag;

        private MarkAsFavoriteTask(boolean flag) {
            favoritesFlag = flag;
        }

        @Override
        protected Void doInBackground(Long... params) {
            NewsListDatabaseHelper newsListDatabaseHelper = new NewsListDatabaseHelper(getBaseContext());
            newsListDatabaseHelper.setFavorite(params[0], favoritesFlag);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (-1 == newsLinkId) {
                FragmentManager manager = getFragmentManager();
                RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) manager
                        .findFragmentByTag(RecyclerViewFragment.TAG);
                recyclerViewFragment.onRefresh();
            }
        }
    }
}