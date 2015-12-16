package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.DrawerExpListAdapter;
import com.jack.newsobserver.helper.TestNetwork;
import com.jack.newsobserver.helper.TopicsDatabaseHelper;
import com.jack.newsobserver.manager.AlertDialogManager;
import com.jack.newsobserver.models.NewsCategory;
import com.jack.newsobserver.models.NewsTopic;
import com.jack.newsobserver.parser.DataFromHtmlParser;

public class DrawerExpListFragment extends Fragment implements DataFromHtmlParser.OnFillFinished {

    public static final String TAG = "DrawerExpListFragmentTag";
    private static final String HTML_FEED_URL = "http://www.cbc.ca/rss/";
    private TopicsDatabaseHelper mTopicsDatabaseHelper;
    private DrawerExpListAdapter mDrawerExpListAdapter;
    private ExpandableListView mExpandableListView;

    public DrawerExpListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.drawer_explist_fragment, container, false);
        mExpandableListView = (ExpandableListView)rootView.findViewById(R.id.drawer_ltr_expListView);
        if (null == savedInstanceState){
            if (new TestNetwork(getActivity()).isNetworkAvailable()) {
                DataFromHtmlParser mDataFromHtmlParser = new DataFromHtmlParser(getActivity(),this);
                mDataFromHtmlParser.execute(HTML_FEED_URL);
            } else{
                new AlertDialogManager().alertDialogShow(getActivity());
            }
        }else {
            initDrawerExpandableList();
        }
        return rootView;
    }

    private DrawerExpListAdapter initDrawerExpandableList() {
        setDrawerListGroupIndicator();
        if (null == mTopicsDatabaseHelper){
            mTopicsDatabaseHelper=new TopicsDatabaseHelper(getActivity());
        }
        mDrawerExpListAdapter = new DrawerExpListAdapter(getActivity(),
                mTopicsDatabaseHelper.getCategoriesWithRelatedTopics());
        mExpandableListView.setAdapter(mDrawerExpListAdapter);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                NewsCategory category = (NewsCategory) mDrawerExpListAdapter.getGroup(groupPosition);
                NewsTopic topic = (NewsTopic) mDrawerExpListAdapter.getChild(groupPosition, childPosition);
                String subTitle = category.getCategoryName() + ": " + topic.getTopicName();
                onSelectedExpListListener listener = (onSelectedExpListListener) getActivity();
                listener.onExpandableChildItemClick(subTitle, topic.getTopicLink(), topic.getTopicId());
                return false;
            }
        });
        return mDrawerExpListAdapter;
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
    public void callBack() {
        initDrawerExpandableList().notifyDataSetChanged();
    }


    public interface onSelectedExpListListener{
        void onExpandableChildItemClick(String title, String url, long id);
    }


}
