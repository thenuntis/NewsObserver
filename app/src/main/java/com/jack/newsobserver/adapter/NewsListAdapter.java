package com.jack.newsobserver.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jack.newsobserver.R;
import com.jack.newsobserver.fragments.RecyclerViewFragment;
import com.jack.newsobserver.interfaces.OnFavoriteCheckChangeListener;
import com.jack.newsobserver.interfaces.OnShareButtonClickListener;
import com.jack.newsobserver.models.NewsList;
import com.jack.newsobserver.util.DateTimeUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    private final Context mContext;
    private List<NewsList> mNewsList;
    private String mSearchText;
    private RecyclerViewFragment fragment;

    public NewsListAdapter(Context context, RecyclerViewFragment fragment) {
        this.mContext = context;
        this.fragment = fragment;
    }

    public void updateList(List<NewsList> list, String searchText) {
        mNewsList = list;
        mSearchText = searchText;
        this.notifyDataSetChanged();
    }

    public void refreshList() {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NewsList newsItem = mNewsList.get(position);
        holder.iconImg.setVisibility(View.INVISIBLE);
        holder.indicator.setVisibility(View.VISIBLE);
        Picasso.with(mContext)
                .load(newsItem.getImgUrl())
                .error( R.drawable.no_image)
                .into(holder.iconImg, new Callback() {
            @Override
            public void onSuccess() {
                holder.indicator.setVisibility(View.INVISIBLE);
                holder.iconImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });

        if (0 == newsItem.getNewsLastWatched().getTime()) {
            holder.nameTxt.setTextColor(mContext.getResources().getColor(R.color.not_watched_news_color));
        } else {
            holder.nameTxt.setTextColor(mContext.getResources().getColor(R.color.watched_news_color));
        }
        if (null == mSearchText) {
            holder.nameTxt.setText(newsItem.getStoryTitle());
        } else {
            holder.nameTxt.setText(highlightedSearchText(newsItem.getStoryTitle()));
        }
        holder.pubDateTxt.setText(DateTimeUtil.getStringFromMsec(mContext, newsItem.getStoryPubdate().getTime()));
        holder.authorTxt.setText(newsItem.getStoryAuthor());
        holder.setClickListener(new ViewHolder.NewsListItemClickListener() {
            @Override
            public void onNewsListItemClick() {
                OnSelectedListItemListener listener = fragment;
                listener.onListItemSelected(newsItem);
            }
        });
        OnFavoriteCheckChangeListener favoriteCheckChangeListener = (OnFavoriteCheckChangeListener) fragment.getActivity();
        holder.setCheckChangeListener(newsItem.getStoryId(), favoriteCheckChangeListener);
        holder.favoriteNewsCheckBox.setChecked(newsItem.getNewsFavorite());
        OnShareButtonClickListener shareButtonClickListener = (OnShareButtonClickListener) fragment.getActivity();
        holder.setShareButtonClickListener(newsItem.getStoryLink(), shareButtonClickListener);

    }

    @Override
    public int getItemCount() {
        if (mNewsList != null) {
            return mNewsList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iconImg;
        public ProgressBar indicator;
        public TextView nameTxt;
        public TextView pubDateTxt;
        public TextView authorTxt;
        public CheckBox favoriteNewsCheckBox;
        public ImageButton shareNewsButton;

        private NewsListItemClickListener clickListener;

        public ViewHolder(View v) {
            super(v);
            iconImg = (ImageView) v.findViewById(R.id.iconImg);
            indicator = (ProgressBar) v.findViewById(R.id.progress);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            pubDateTxt = (TextView) v.findViewById(R.id.pubDateTxt);
            authorTxt = (TextView) v.findViewById(R.id.authorTxt);
            favoriteNewsCheckBox = (CheckBox) v.findViewById(R.id.news_list_favorite_checkBox);
            shareNewsButton = (ImageButton) v.findViewById(R.id.news_list_share_btn);
            v.setOnClickListener(this);
        }

        public void setCheckChangeListener(final long storyId, final OnFavoriteCheckChangeListener listener) {
            favoriteNewsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onFavoriteCheckChanged(storyId, isChecked);
                }
            });
        }

        public void setShareButtonClickListener(final String storyLink, final OnShareButtonClickListener shareButtonClickListener) {
            shareNewsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareButtonClickListener.onShareButtonClick(storyLink);
                }
            });
        }

        public interface NewsListItemClickListener {
            void onNewsListItemClick();
        }


        public void setClickListener(NewsListItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onNewsListItemClick();
        }
    }


    private Spannable highlightedSearchText(String text) {
        int startPos = text.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
        int endPos = startPos + mSearchText.length();
        Spannable highlightedText = new SpannableString(text);
        if (startPos != -1) {
            highlightedText.setSpan(new BackgroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            highlightedText.setSpan(new ForegroundColorSpan(Color.WHITE), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return highlightedText;
    }

    public interface OnSelectedListItemListener {
        void onListItemSelected(NewsList newsList);
    }

}
