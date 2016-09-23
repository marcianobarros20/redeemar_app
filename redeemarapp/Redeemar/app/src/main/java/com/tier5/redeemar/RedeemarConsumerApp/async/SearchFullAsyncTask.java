package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.SearchActivity;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.SearchLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Search;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;

/**
 * Created by tier5 on 22/6/16.
 */


public class SearchFullAsyncTask extends AsyncTask<String, Void, ArrayList<Search>> {


    private static final String LOGTAG = "SearchFullAsyncTask";
    private SearchLoadedListener myComponent;
    private RequestQueue requestQueue;
    private Context context;

    public SearchFullAsyncTask(SearchLoadedListener myComponent, Context ctx) {

        this.myComponent = myComponent;
        this.context = ctx;
        Log.d(LOGTAG, "Inside SearchFullAsyncTask constructor");

    }

    public SearchFullAsyncTask() {

    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected ArrayList<Search> doInBackground(String... params) {

        Log.d(LOGTAG, "Inside BG Task for SearchFullAsyncTask");

        String keyword = params[0];

        ArrayList<Search> res = OfferUtils.loadSearchFull(requestQueue, keyword);
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Search> resp) {

        if (myComponent != null) {
            myComponent.onSearchLoaded(resp);
        }
    }


}




