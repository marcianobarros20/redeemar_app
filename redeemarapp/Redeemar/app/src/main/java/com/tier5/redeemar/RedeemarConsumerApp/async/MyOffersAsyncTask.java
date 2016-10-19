package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.network.VolleySingleton;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;

/**
 * Created by tier5 on 22/6/16.
 */


public class MyOffersAsyncTask extends AsyncTask<String, Void, ArrayList<Offer>> {


    private static final String LOGTAG = "MyOffersAsync";
    private OffersLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog mProgress;
    private Context context;

    public MyOffersAsyncTask(OffersLoadedListener myComponent, Context ctx) {
        this.myComponent = myComponent;
        this.context = ctx;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected ArrayList<Offer> doInBackground(String... params) {

        Log.d(LOGTAG, "Inside my list offers");
        String user_id      = params[0];
        String lat          = params[1];
        String lon          = params[2];
        String categoryId   = params[3];

        ArrayList<Offer> listOffers = OfferUtils.loadMyOffers(requestQueue, user_id, lat, lon, categoryId);
        return listOffers;
    }

    @Override
    protected void onPostExecute(ArrayList<Offer> listOffers) {
        if (myComponent != null) {
            myComponent.onOffersLoaded(listOffers);
        }
    }




}




