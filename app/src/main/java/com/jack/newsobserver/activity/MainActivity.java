package com.jack.newsobserver.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.DrawerExpListAdapter;
import com.jack.newsobserver.fragments.SiteListViewFragment;
import com.jack.newsobserver.fragments.SiteWebViewFragment;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.helper.TopicsDatabaseHelper;
import com.jack.newsobserver.manager.AlertDialogManager;
import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;
import com.jack.newsobserver.parser.DataFromHtmlParser;


public class MainActivity extends ActionBarActivity implements SiteListViewFragment.OnSelectedLinkListener,
        ExpandableListView.OnChildClickListener, DataFromHtmlParser.OnFillFinished, SearchView.OnQueryTextListener {

    private DrawerLayout mDrawerLayout;
    private TopicsDatabaseHelper mTopicsDatabaseHelper;
    private DrawerExpListAdapter mDrawerExpListAdapter;
    private ExpandableListView mExpandableListView;
    private static String subTitleString;
    private static String newsListUrl;
    private static long newsLinkId;
    private static final String SUBTITLE_KEY = "SUBTITLE";
    private static final String RECENT_LINK_URL_KEY = "LINKURLKEY";
    private static final String RECENT_LINK_ID_KEY = "LINKIDKEY";
    private static final String HTML_FEED_URL = "http://www.cbc.ca/rss/";
    private static final String DEFAULT_URL = "http://www.cbc.ca/cmlink/rss-topstories";
    private static final String DEFAULT_SUBTITLE = "General News: Top Stories";


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
                newsListUrl =DEFAULT_URL;
                newsLinkId = 1;
            }
        }

        FragmentManager manager = getFragmentManager();

        if (null == manager.findFragmentByTag(SiteListViewFragment.TAG)) {
            FragmentTransaction transaction = manager.beginTransaction();
            SiteListViewFragment siteListViewFragment = new SiteListViewFragment();
            transaction.add(R.id.list_view_fragment, siteListViewFragment, SiteListViewFragment.TAG);
            siteListViewFragment.setListViewUrl(newsListUrl,newsLinkId);
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
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (null == savedInstanceState){
            if (new TestNetwork(this).isNetworkAvailable()) {
                DataFromHtmlParser mDataFromHtmlParser = new DataFromHtmlParser(this,this);
                mDataFromHtmlParser.execute(HTML_FEED_URL);
            } else{
                new AlertDialogManager().alertDialogShow(this);
            }
        }else {
            initDrawerExpandableList();
        }
    }

    private DrawerExpListAdapter initDrawerExpandableList() {
        mExpandableListView = (ExpandableListView)findViewById(R.id.drawer_ltr_expListView);
        setDrawerListGroupIndicator();
        mExpandableListView.setOnChildClickListener(this);
        if (null == mTopicsDatabaseHelper){
            mTopicsDatabaseHelper=new TopicsDatabaseHelper(this);
        }
        mDrawerExpListAdapter = new DrawerExpListAdapter(this,
                                mTopicsDatabaseHelper.getCategoriesWithRelatedTopics());
        mExpandableListView.setAdapter(mDrawerExpListAdapter);
        if (null == subTitleString){
            subTitleString = DEFAULT_SUBTITLE;
        }
        getSupportActionBar().setSubtitle(subTitleString);
        return mDrawerExpListAdapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean onQueryTextChange(String text_new) {
        Log.d("QUERY", "New text is " + text_new);
        return true;
    }

    public boolean onQueryTextSubmit(String text) {
        Log.d("QUERY", "Search text is " + text);
        return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SUBTITLE_KEY, subTitleString);
        outState.putString(RECENT_LINK_URL_KEY, newsListUrl);
        outState.putLong(RECENT_LINK_ID_KEY, newsLinkId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);

    }

    private void setDrawerListGroupIndicator() {
        float density = this.getResources().getDisplayMetrics().density;
        int widthExpList = mExpandableListView.getLayoutParams().width;
        int pxEnd = (int) (widthExpList - (10*density));
        int pxStart = (int) (widthExpList - (60*density));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mExpandableListView.setIndicatorBounds(pxStart,pxEnd);
        } else {
            mExpandableListView.setIndicatorBoundsRelative(pxStart,pxEnd);
        }
    }

    @Override
    public void onListItemSelected(String url) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        SiteWebViewFragment siteWebViewFragment = (SiteWebViewFragment) manager.findFragmentByTag(SiteWebViewFragment.TAG);

        if (siteWebViewFragment != null) {
            siteWebViewFragment.setWebViewUrl(url);
        }else {
            siteWebViewFragment = new SiteWebViewFragment();
        }

        transaction.replace(R.id.list_view_fragment, siteWebViewFragment, SiteWebViewFragment.TAG);
        transaction.addToBackStack(subTitleString);
        siteWebViewFragment.setWebViewUrl(url);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getFragmentManager();
            if (manager.getBackStackEntryCount()>0){
                getSupportActionBar().setSubtitle(manager.getBackStackEntryAt(
                        manager.getBackStackEntryCount()-1).getName());
                manager.popBackStack();
            }else {
                this.finish();

            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        NewsCategory category = (NewsCategory) mDrawerExpListAdapter.getGroup(groupPosition);
        NewsTopic topic = (NewsTopic) mDrawerExpListAdapter.getChild(groupPosition, childPosition);
        String groupTitle = category.getCategoryName();
        String childTitle = topic.getTopicName();
        subTitleString =groupTitle+": "+childTitle;
        newsListUrl = topic.getTopicLink();
        newsLinkId = topic.getTopicId();
        getSupportActionBar().setSubtitle(subTitleString);
        FragmentManager manager = getFragmentManager();
        SiteListViewFragment siteListViewFragment = (SiteListViewFragment) manager.findFragmentByTag(SiteListViewFragment.TAG);
        SiteWebViewFragment siteWebViewFragment = (SiteWebViewFragment) manager.findFragmentByTag(SiteWebViewFragment.TAG);
        if (siteWebViewFragment!=null && siteWebViewFragment.isVisible()){
            siteListViewFragment = new SiteListViewFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.list_view_fragment, siteListViewFragment, SiteListViewFragment.TAG);
            transaction.addToBackStack(subTitleString);
            siteListViewFragment.setListViewUrl(newsListUrl,newsLinkId);
            transaction.commit();
        }else {
            siteListViewFragment.setListViewUrl(newsListUrl,newsLinkId);
            siteListViewFragment.onRefresh();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void callBack() {
        initDrawerExpandableList().notifyDataSetChanged();
    }
}