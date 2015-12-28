package com.jack.newsobserver.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jack.newsobserver.R;
import com.jack.newsobserver.adapter.NewsListAdapter;
import com.jack.newsobserver.util.ImageCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetImageTaskManager extends AsyncTask<String, Void, Bitmap> {

    private ImageView imgIcon;
    private ProgressBar bar;
    private Context ctx;

    public GetImageTaskManager(NewsListAdapter.ViewHolder holder, Context context) {
        this.ctx=context;
        this.imgIcon = holder.iconImg;
        this.bar = holder.indicator;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.setVisibility(View.VISIBLE);
        imgIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap myBitmap;
        if (null != urls[0]){
            myBitmap = ImageCache.getBitmapFromMemCache(urls[0]);
            if (myBitmap == null) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    if (myBitmap == null) {
                        myBitmap = BitmapFactory.decodeResource(ctx.getResources() , R.drawable.no_image);
                    }
                    ImageCache.addBitmapToMemoryCache(urls[0], myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            myBitmap = BitmapFactory.decodeResource(ctx.getResources() , R.drawable.no_image);
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


