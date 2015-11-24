package com.jack.newsobserver.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.DrawerCursorAdapter;
import com.jack.newsobserver.fragments.SiteListViewFragment;
import com.jack.newsobserver.fragments.SiteWebViewFragment;
import com.jack.newsobserver.helper.DatabaseHelper;
import com.jack.newsobserver.helper.IsNetworkAvailable;
import com.jack.newsobserver.manager.AlertDialogManager;
import com.jack.newsobserver.manager.GetDataFromHtmlManager;


public class MainActivity extends ActionBarActivity implements SiteListViewFragment.OnSelectedLinkListener, ExpandableListView.OnChildClickListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Cursor mCursor;
    private DrawerCursorAdapter mCursorAdapter;
    private DatabaseHelper mDatabaseHelper;
    private static final String HTML_FEED_URL = "http://www.cbc.ca/rss/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        FragmentManager manager = getFragmentManager();

        if (manager.findFragmentByTag(SiteListViewFragment.TAG)== null ) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.list_view_fragment, new SiteListViewFragment(),SiteListViewFragment.TAG);
            transaction.commit();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDatabaseHelper = new DatabaseHelper(this);
        if (mDatabaseHelper.initDataBase()){
            if (new IsNetworkAvailable(this).testNetwork()) {
                GetDataFromHtmlManager mGetDataFromHtmlManager = new GetDataFromHtmlManager(mDatabaseHelper,this);
                mGetDataFromHtmlManager.execute(HTML_FEED_URL);
            } else{
                new AlertDialogManager().alertDialogShow(this);
            }
        }
        initDrawerExpList();
    }



    public DrawerCursorAdapter initDrawerExpList () {
        mCursor = mDatabaseHelper.getGroupCursor();
        ExpandableListView mExpandableListView = (ExpandableListView)findViewById(R.id.drawer_ltr_expListView);
        mCursorAdapter = new DrawerCursorAdapter(this,
                mCursor,
                R.layout.drawer_explist_group_item,
                new String[]{"name"},
                new int[] {R.id.textGroup},
                R.layout.drawer_explist_child_item,
                new String[]{"name"},
                new int[] {R.id.textChild});
        mExpandableListView.setAdapter(mCursorAdapter);
        getSupportActionBar().setSubtitle(mCursor.getString(1)
                + ": " + mCursorAdapter.getChild(0, 0).getString(1));
        mExpandableListView.setOnChildClickListener(this);
        return mCursorAdapter;
    }


    public void onGetDataFromHtmlDone() {
        initDrawerExpList().notifyDataSetChanged();
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
        transaction.addToBackStack(SiteWebViewFragment.TAG);
        transaction.commit();
        siteWebViewFragment.setWebViewUrl(url);
    }

    @Override
    public void onBackPressed() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getFragmentManager();
            if (manager.getBackStackEntryCount()>0){
                manager.popBackStack();
            }else {
                this.finish();
            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String groupTitle = mCursor.getString(1);
        Cursor childCursor = mCursorAdapter.getChild(groupPosition, childPosition);
        String childTitle = childCursor.getString(1);
        String mLink = childCursor.getString(2);
        getSupportActionBar().setSubtitle(groupTitle+": "+childTitle);
        FragmentManager manager = getFragmentManager();
        SiteListViewFragment siteListViewFragment = (SiteListViewFragment) manager.findFragmentByTag(SiteListViewFragment.TAG);
        if (siteListViewFragment!= null ){
            siteListViewFragment.setListViewUrl(mLink);
            siteListViewFragment.onRefresh();
        }
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

}