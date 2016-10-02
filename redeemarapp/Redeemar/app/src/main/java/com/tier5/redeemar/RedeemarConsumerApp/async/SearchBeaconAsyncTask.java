package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.BeaconFoundListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.network.VolleySingleton;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Beacon;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;

/**
 * Created by tier5 on 22/6/16.
 */


public class SearchBeaconAsyncTask extends AsyncTask<String, Void, Beacon> {


    private static final String LOGTAG = "SearchBeaconAsyncTask";
    private BeaconFoundListener myComponent;
    private RequestQueue requestQueue;

    public SearchBeaconAsyncTask(BeaconFoundListener myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside SearchBeaconAsyncTask constructor");
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected Beacon doInBackground(String... params) {

        Log.d(LOGTAG, "Inside list category offers...");

        String uuid = params[0];
        String major = params[1];
        String minor = params[2];

        Beacon beacon = OfferUtils.loadSearchBeacon(requestQueue, uuid, Integer.parseInt(major), Integer.parseInt(minor));

        return beacon;
    }

    @Override
    protected void onPostExecute(Beacon beacon) {
        if (myComponent != null) {
            myComponent.onBeaconFound(beacon);
        }

    }




}




