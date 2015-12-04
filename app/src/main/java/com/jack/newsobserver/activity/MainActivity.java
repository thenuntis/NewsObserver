package com.jack.newsobserver.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.DrawerCursorAdapter;
import com.jack.newsobserver.adapter.DrawerExpListAdapter;
import com.jack.newsobserver.fragments.SiteListViewFragment;
import com.jack.newsobserver.fragments.SiteWebViewFragment;
import com.jack.newsobserver.helper.DatabaseHelper;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.helper.TopicsDatabaseHelper;
import com.jack.newsobserver.manager.AlertDialogManager;
import com.jack.newsobserver.manager.GetDataFromHtmlManager;
import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;


public class MainActivity extends ActionBarActivity implements SiteListViewFragment.OnSelectedLinkListener,
        ExpandableListView.OnChildClickListener, GetDataFromHtmlManager.OnFillFinished {

    private DrawerLayout mDrawerLayout;
    private Cursor mCursor;
    private DrawerCursorAdapter mCursorAdapter;
    private DatabaseHelper mDatabaseHelper;
    private TopicsDatabaseHelper mTopicsDatabaseHelper;
    DrawerExpListAdapter mDrawerExpListAdapter;
    private static String subTitleString;
    private static String newsListUrl;
    private static final String SUBTITLE_KEY = "SUBTITLE";
    private static final String RECENT_URL_KEY = "URLKEY";
    private static final String HTML_FEED_URL = "http://www.cbc.ca/rss/";
    private static final String DEFAULT_URL = "http://www.cbc.ca/cmlink/rss-topstories";
    private static final String DEFAULT_SUBTITLE = "General News: Top Stories";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState){
            subTitleString =savedInstanceState.getString(SUBTITLE_KEY);
            newsListUrl =savedInstanceState.getString(RECENT_URL_KEY);
        }else {
            if (null == newsListUrl){
                newsListUrl =DEFAULT_URL;
            }
        }

        FragmentManager manager = getFragmentManager();

        if (manager.findFragmentByTag(SiteListViewFragment.TAG)== null ) {
            FragmentTransaction transaction = manager.beginTransaction();
            SiteListViewFragment siteListViewFragment = new SiteListViewFragment();
            transaction.add(R.id.list_view_fragment, siteListViewFragment,SiteListViewFragment.TAG);
            siteListViewFragment.setListViewUrl(newsListUrl);
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


        mDatabaseHelper = DatabaseHelper.getInstance(this);
        if (new TestNetwork(this).isNetworkAvailable()) {
            GetDataFromHtmlManager mGetDataFromHtmlManager = new GetDataFromHtmlManager(this,this);
            mGetDataFromHtmlManager.execute(HTML_FEED_URL);
        } else{
            new AlertDialogManager().alertDialogShow(this);
        }
        initDrawerExpandableList();
    }

    private DrawerExpListAdapter initDrawerExpandableList() {
        ExpandableListView mExpandableListView = (ExpandableListView)findViewById(R.id.drawer_ltr_expListView);
        if (null == mTopicsDatabaseHelper){
            mTopicsDatabaseHelper=new TopicsDatabaseHelper(this);
        }
        mDrawerExpListAdapter = new DrawerExpListAdapter(this,
                                mTopicsDatabaseHelper.getCategories(),
                                mTopicsDatabaseHelper.getAllTopics());

        mExpandableListView.setAdapter(mDrawerExpListAdapter);
        if (null == subTitleString){
            subTitleString = DEFAULT_SUBTITLE;
        }/*else{
            NewsCategory category = (NewsCategory) mDrawerExpListAdapter.getGroup(0);
            NewsTopic topic = (NewsTopic) mDrawerExpListAdapter.getChild(0,0);
            subTitleString = category.getCategoryName()+": "+
                        topic.getTopicName();
        }*/
        getSupportActionBar().setSubtitle(subTitleString);
        mExpandableListView.setOnChildClickListener(this);
        setDrawerListGroupIndicator(mExpandableListView);
        return mDrawerExpListAdapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SUBTITLE_KEY, subTitleString);
        outState.putString(RECENT_URL_KEY, newsListUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setDrawerListGroupIndicator(ExpandableListView v) {
        if (android.os.Build.VERSION.SDK_INT <
            android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
        v.setIndicatorBounds(v.getWidth()- v.getWidth()/5, v.getWidth()-10);
        } else {
        v.setIndicatorBoundsRelative(v.getWidth()- v.getWidth()/5, v.getWidth()-10);
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
        getSupportActionBar().setSubtitle(subTitleString);
        FragmentManager manager = getFragmentManager();
        SiteListViewFragment siteListViewFragment = (SiteListViewFragment) manager.findFragmentByTag(SiteListViewFragment.TAG);
        SiteWebViewFragment siteWebViewFragment = (SiteWebViewFragment) manager.findFragmentByTag(SiteWebViewFragment.TAG);
        if (siteWebViewFragment!=null && siteWebViewFragment.isVisible()){
            siteListViewFragment = new SiteListViewFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.list_view_fragment, siteListViewFragment, SiteListViewFragment.TAG);
            transaction.addToBackStack(subTitleString);
            siteListViewFragment.setListViewUrl(newsListUrl);
            transaction.commit();
        }else {
            siteListViewFragment.setListViewUrl(newsListUrl);
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