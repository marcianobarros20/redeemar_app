package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.BrowseOffersActivity;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.network.VolleySingleton;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;

/**
 * Created by tier5 on 22/6/16.
 */


public class BrowseOffersAsyncTask extends AsyncTask<String, Void, ArrayList<Offer>> {


    private static final String LOGTAG = "BrowseOffersAsync";
    private OffersLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog mProgress;
    private Context context;

    public BrowseOffersAsyncTask(OffersLoadedListener myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside BrowseOffersAsyncTask constructor");

    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected ArrayList<Offer> doInBackground(String... params) {

        Log.d(LOGTAG, "Inside list offers");

        String user_id = params[0];
        String lat = params[1];
        String lng = params[2];
        String sLat = params[3];
        String sLng = params[4];
        String catId = params[5];



        ArrayList<Offer> listOffers = OfferUtils.loadBrowseOffers(requestQueue, user_id, lat, lng, sLat, sLng, catId);
        return listOffers;
    }

    @Override
    protected void onPostExecute(ArrayList<Offer> listOffers) {
        if (myComponent != null) {
            myComponent.onOffersLoaded(listOffers);
        }
        //mProgress.dismiss();
    }




}




