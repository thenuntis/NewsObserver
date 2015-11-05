package com.jack.newsobserver;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.jack.newsobserver.fragments.SiteListViewFragment;
import com.jack.newsobserver.fragments.SiteWebViewFragment;



public class MainActivity extends Activity implements SiteListViewFragment.OnSelectedLinkListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        FragmentManager manager = getFragmentManager();
        //SiteListViewFragment frgmnt1 = (SiteListViewFragment) manager.findFragmentById(R.id.list_view_fragment);

        if (manager.findFragmentByTag(SiteListViewFragment.TAG)== null ) {
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.add(R.id.list_view_fragment, new SiteListViewFragment(),SiteListViewFragment.TAG);
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
        transaction.addToBackStack(SiteListViewFragment.TAG);
        transaction.commit();
        sitewebviewfragment.setWebViewUrl(url);
    }

}