package com.jack.newsobserver.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;
import com.jack.newsobserver.fragments.SiteListViewFragment;
import com.jack.newsobserver.fragments.SiteWebViewFragment;



public class MainActivity extends ActionBarActivity implements SiteListViewFragment.OnSelectedLinkListener {

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
            transaction.addToBackStack(SiteListViewFragment.TAG);
            transaction.commit();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    @Override
    public void onListItemSelected(String url) {
        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);

        SiteWebViewFragment sitewebviewfragment = (SiteWebViewFragment) manager.findFragmentByTag(SiteWebViewFragment.TAG);

        if (sitewebviewfragment != null) {
            sitewebviewfragment.setWebViewUrl(url);
        }else {
            sitewebviewfragment = new SiteWebViewFragment();
        }

        transaction.replace(R.id.list_view_fragment, sitewebviewfragment, SiteWebViewFragment.TAG);
        transaction.addToBackStack(SiteWebViewFragment.TAG);
        transaction.commit();
        sitewebviewfragment.setWebViewUrl(url);
    }

}