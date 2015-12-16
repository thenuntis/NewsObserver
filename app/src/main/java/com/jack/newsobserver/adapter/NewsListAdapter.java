package com.jack.newsobserver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.manager.GetImageTaskManager;
import com.jack.newsobserver.models.NewsList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NewsListAdapter extends ArrayAdapter<NewsList> {
    Context context;
    private List<NewsList> mSites;
    private List<NewsList> mFilteredSites = new ArrayList<>();

    public NewsListAdapter(Context ctx) {
        super(ctx, R.layout.main_listview_item);
        this.context=super.getContext();
    }

    public void setSites(List<NewsList> sites) {
        mSites = sites;
        mFilteredSites.clear();
        mFilteredSites.addAll(sites);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mSites != null) {
            return mSites.size();
        } else {
            return 0;
        }
    }

    @Override
    public NewsList getItem(int position) {
        return mSites.get(position);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder viewHolder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.main_listview_item, null);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        new GetImageTaskManager(viewHolder,context).execute(getItem(pos).getImgUrl());
        viewHolder.nameTxt.setText(getItem(pos).getStoryTitle());
        viewHolder.pubDateTxt.setText(getItem(pos).getStoryPubdate());
        viewHolder.authorTxt.setText(getItem(pos).getStoryAuthor());

        return row;
    }

    public static class ViewHolder {
        public ImageView iconImg;
        public ProgressBar indicator;
        public TextView nameTxt;
        public TextView pubDateTxt;
        public TextView authorTxt;

        ViewHolder(View v) {
            iconImg = (ImageView) v.findViewById(R.id.iconImg);
            indicator = (ProgressBar) v.findViewById(R.id.progress);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            pubDateTxt = (TextView) v.findViewById(R.id.pubDateTxt);
            authorTxt = (TextView) v.findViewById(R.id.authorTxt);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mSites.clear();
        if (charText.length() == 0) {
            mSites.addAll(mFilteredSites);
        }
        else{
            for (NewsList list : mFilteredSites) {
                if (list.getStoryTitle().toLowerCase(Locale.getDefault()).contains(charText)){
                    mSites.add(list);
                }
            }
        }
        notifyDataSetChanged();
    }
}