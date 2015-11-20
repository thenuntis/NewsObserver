package com.jack.newsobserver.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jack.newsobserver.manager.GetImageTaskManager;
import com.jack.newsobserver.R;
import com.jack.newsobserver.StoriesDigest;

import java.util.List;


public class SitesAdapter extends ArrayAdapter<StoriesDigest> {
    Context context;
    private List<StoriesDigest> mSites;

    public void setSites(List<StoriesDigest> sites) {
        mSites = sites;
        notifyDataSetChanged();
    }

    public SitesAdapter(Context ctx, List<StoriesDigest> sites) {
        super(ctx, R.layout.site_list_item);
        mSites = sites;
        this.context=super.getContext();
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
    public StoriesDigest getItem(int position) {
        return mSites.get(position);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Log.i("StoriesIndex", "getView pos = " + pos);

        View row = convertView;
        ViewHolder viewHolder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.site_list_item, null);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

//        new GetImageTaskManager(viewHolder).execute(getItem(pos).getImgUrl());
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

}