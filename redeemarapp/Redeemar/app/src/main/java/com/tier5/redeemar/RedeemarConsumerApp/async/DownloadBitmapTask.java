package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ImageDownloadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by tier5 on 18/5/16.
 */
public class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
    private static final String LOGTAG = "DownloadBitmapTask";
    private ImageDownloadedListener myComponent;
    private Context mContext;

    public DownloadBitmapTask(ImageDownloadedListener myComponent) {

        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside DownloadBitmapTask constructor: "+myComponent);
    }


    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (myComponent != null) {
            myComponent.onImageDownloaded(result);
        }
    }

}

