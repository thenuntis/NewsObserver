package com.jack.newsobserver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;



public class SitesAdapter extends ArrayAdapter<StoriesDigest> {


    public SitesAdapter(Context ctx, int textViewResourceId, List<StoriesDigest> sites) {
        super(ctx, textViewResourceId, sites);
    }

    private static class ViewHolder {
        public ImageView iconImg;
        public ProgressBar indicator;
        public TextView nameTxt;
        public TextView pubDateTxt;
        public TextView authorTxt;

        ViewHolder (View v) {
            iconImg = (ImageView) v.findViewById(R.id.iconImg);
            indicator = (ProgressBar) v.findViewById(R.id.progress);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            pubDateTxt = (TextView) v.findViewById(R.id.pubDateTxt);
            authorTxt = (TextView) v.findViewById(R.id.authorTxt);
        }
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        Log.i("StoriesIndex", "getView pos = " + pos);

        View row = convertView;
        ViewHolder viewHolder;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.site_list_item, null);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) row.getTag();
        }

        new GetImageTask(viewHolder).execute(getItem(pos).getImgUrl());

        viewHolder.nameTxt.setText(getItem(pos).getStoryTitle());
        viewHolder.pubDateTxt.setText(getItem(pos).getStoryPubdate());
        viewHolder.authorTxt.setText(getItem(pos).getStoryAuthor());

        return row;
    }

    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imgIcon;
        private ProgressBar bar;

        public GetImageTask(ViewHolder viewHolder) {
            this.imgIcon = viewHolder.iconImg;
            this.bar = viewHolder.indicator;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);
            imgIcon.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            Bitmap myBitmap = ImageCache.getBitmapFromMemCache(urls[0]);
            if (myBitmap == null) {
                Log.e("LOG>>>>>>", "BITMAP = NULL");
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    ImageCache.addBitmapToMemoryCache(urls[0], myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("LOG>>>>>>", "BITMAP = From Cache");
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgIcon.setImageBitmap(bitmap);
            bar.setVisibility(View.INVISIBLE);
            imgIcon.setVisibility(View.VISIBLE);
        }
    }
}