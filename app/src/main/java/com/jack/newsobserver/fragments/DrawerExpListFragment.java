package com.jack.newsobserver.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
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
import com.jack.newsobserver.parser.MainUrlHtmlParser;
import com.jack.newsobserver.util.Constants;

import java.io.IOException;

public class DrawerExpListFragment extends Fragment {

    public static final String TAG = "DrawerExpListFragmentTag";
    private static final String GROUP_TO_EXPAND = "groupNumber";
    private TopicsDatabaseHelper mTopicsDatabaseHelper;
    private DrawerExpListAdapter mDrawerExpListAdapter;
    private ExpandableListView mExpandableListView;
    private int mRecentChildIndex = -1;
    private int mRecentGroupIndex = -1;

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
        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.drawer_ltr_expListView);
        if (null == savedInstanceState) {
            if (new TestNetwork(getActivity()).isNetworkAvailable()) {
                HtmlDataParseTask parseTask = new HtmlDataParseTask();
                parseTask.execute(Constants.HTML_FEED_URL);
            } else {
                new AlertDialogManager().alertDialogShow(getActivity());
            }
        } else {
            mRecentGroupIndex = savedInstanceState.getInt(GROUP_TO_EXPAND);
            initDrawerExpandableList();
        }
        final View favoriteView = rootView.findViewById(R.id.drawer_favorite);
        favoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoritesClickListener listener = (onFavoritesClickListener) getActivity();
                listener.onDrawerFavoritesItemClick(Constants.FAVORITE_NEWS_TITLE,-1);
            }
        });
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(GROUP_TO_EXPAND, mRecentGroupIndex);
    }

    private class HtmlDataParseTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                if (null == mTopicsDatabaseHelper) {
                    mTopicsDatabaseHelper = new TopicsDatabaseHelper(getActivity());
                }
                mTopicsDatabaseHelper.addCategoryAndRelatedTopics(MainUrlHtmlParser
                        .startParsingUrl(params[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initDrawerExpandableList().notifyDataSetChanged();
        }
    }

    private DrawerExpListAdapter initDrawerExpandableList() {
        setDrawerListGroupIndicator();
        if (null == mTopicsDatabaseHelper) {
            mTopicsDatabaseHelper = new TopicsDatabaseHelper(getActivity());
        }
        mDrawerExpListAdapter = new DrawerExpListAdapter(getActivity(),
                mTopicsDatabaseHelper.getCategoriesWithRelatedTopics());
        mExpandableListView.setAdapter(mDrawerExpListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (-1 != mRecentGroupIndex && groupPosition == mRecentGroupIndex) {
                    int index = mExpandableListView.getFlatListPosition(ExpandableListView
                            .getPackedPositionForChild(mRecentGroupIndex, mRecentChildIndex));
                    mExpandableListView.setItemChecked(index, true);
                }
            }
        });
        if (-1 == mRecentGroupIndex) {
            mRecentGroupIndex = mRecentChildIndex = Constants.DEFAULT_DRAWER_EXPAND_VALUE;
            int index = mExpandableListView.getFlatListPosition(ExpandableListView
                    .getPackedPositionForChild(mRecentGroupIndex, mRecentChildIndex));
            mExpandableListView.setItemChecked(index, true);
        }
        mExpandableListView.expandGroup(mRecentGroupIndex);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                mRecentChildIndex = childPosition;
                mRecentGroupIndex = groupPosition;
                parent.setItemChecked(index, true);
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
        int pxEnd = (int) (widthExpList - (10 * density));
        int pxStart = (int) (widthExpList - (60 * density));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mExpandableListView.setIndicatorBounds(pxStart, pxEnd);
        } else {
            mExpandableListView.setIndicatorBoundsRelative(pxStart, pxEnd);
        }
    }

    public interface onSelectedExpListListener {
        void onExpandableChildItemClick(String title, String url, long id);
    }

    public interface onFavoritesClickListener {
        void onDrawerFavoritesItemClick(String title, long newsListId);
    }
}
