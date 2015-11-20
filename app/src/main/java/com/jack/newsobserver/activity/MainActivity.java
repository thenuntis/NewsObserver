package com.jack.newsobserver.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.DrawerListCursorAdapter;
import com.jack.newsobserver.fragments.SiteListViewFragment;
import com.jack.newsobserver.fragments.SiteWebViewFragment;
import com.jack.newsobserver.helper.DatabaseHelper;


public class MainActivity extends ActionBarActivity implements SiteListViewFragment.OnSelectedLinkListener,
        AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;

    private Cursor mCursor;
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

        mDatabaseHelper = new DatabaseHelper(this);
        if (mDatabaseHelper.initDataBase()){
            Log.e("-->()<--","empty base, needtofill");
            mDatabaseHelper.fillDataBaseFromUrl(HTML_FEED_URL);
            Log.e("-->()<--", "base not empty, filled");
        }
        String query = "SELECT a._id, b.name AS category, a.name, a.link " +
                "FROM topics a, category b " +
                "WHERE a.category_id=b._id and b._id=1";
        mCursor = mDatabaseHelper.createCursor(query);
        Log.e("-->()<--", String.valueOf(mCursor.getCount()));
        ListView mDrawerListView = (ListView) findViewById(R.id.drawer_ltr_listview);
        DrawerListCursorAdapter drawerListCursorAdapter = new DrawerListCursorAdapter(this,mCursor,0);
        mDrawerListView.setOnItemClickListener(this);
        mDrawerListView.setAdapter(drawerListCursorAdapter);


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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("--_^^_--", String.valueOf(position));
        Log.e("--^(_)^--", String.valueOf(id));
        String mLink = mDatabaseHelper.getStringFromCursor(mCursor,position,"link");
        FragmentManager manager = getFragmentManager();
        SiteListViewFragment siteListViewFragment = (SiteListViewFragment) manager.findFragmentByTag(SiteListViewFragment.TAG);
        if (siteListViewFragment!= null ){
            siteListViewFragment.setListViewUrl(mLink);
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
            drawerLayout.closeDrawer(GravityCompat.START);
            siteListViewFragment.LoadNewsList();
        }
        Toast.makeText(this,mLink + " was selected " ,Toast.LENGTH_SHORT).show();
    }


}