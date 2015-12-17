package com.jack.newsobserver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.manager.GetImageTaskManager;
import com.jack.newsobserver.models.NewsList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsListRecyclerAdapter extends RecyclerView.Adapter<NewsListRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private List<NewsList> mNewsList;
    private List<NewsList> mFilteredList = new ArrayList<>();

    public NewsListRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updateList (List<NewsList> list){
        mNewsList = list;
        mFilteredList.clear();
        mFilteredList.addAll(list);
        this.notifyDataSetChanged();
    }

    public NewsList getItem(int position) {
        return mNewsList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsList newsItem = mNewsList.get(position);
        new GetImageTaskManager(holder,mContext).execute(newsItem.getImgUrl());
        holder.nameTxt.setText(newsItem.getStoryTitle());
        holder.pubDateTxt.setText(newsItem.getStoryPubdate());
        holder.authorTxt.setText(newsItem.getStoryAuthor());
        holder.setClickListener(new ViewHolder.NewsListItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                String url = newsItem.getStoryLink();
                OnSelectedLinkListener listener = (OnSelectedLinkListener) mContext;
                listener.onListItemSelected(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mNewsList != null) {
            return mNewsList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView iconImg;
        public ProgressBar indicator;
        public TextView nameTxt;
        public TextView pubDateTxt;
        public TextView authorTxt;
        private NewsListItemClickListener clickListener;

        private ViewHolder(View v) {
            super(v);
            iconImg = (ImageView) v.findViewById(R.id.iconImg);
            indicator = (ProgressBar) v.findViewById(R.id.progress);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            pubDateTxt = (TextView) v.findViewById(R.id.pubDateTxt);
            authorTxt = (TextView) v.findViewById(R.id.authorTxt);
            v.setOnClickListener(this);
        }

        public interface NewsListItemClickListener {
            void onClick(View v, int position);
        }

        public void setClickListener(NewsListItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getPosition());
        }

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mNewsList.clear();
        if (charText.length() == 0) {
            mNewsList.addAll(mFilteredList);
        }
        else{
            for (NewsList list : mFilteredList) {
                if (list.getStoryTitle().toLowerCase(Locale.getDefault()).contains(charText)){
                    mNewsList.add(list);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnSelectedLinkListener {
        void onListItemSelected(String url);
    }
}
