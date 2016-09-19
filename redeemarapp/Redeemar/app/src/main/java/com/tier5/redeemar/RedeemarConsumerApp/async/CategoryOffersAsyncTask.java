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


public class CategoryOffersAsyncTask extends AsyncTask<String, Void, ArrayList<Offer>> {


    private static final String LOGTAG = "CategoryOffersAsync";
    private OffersLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog mProgress;
    private Context context;

    public CategoryOffersAsyncTask(OffersLoadedListener myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside CategoryOffersAsync constructor");
    }

    @Override
    protected void onPreExecute() {

        //mProgress = new ProgressDialog(context);
        //mProgress.setMessage("Loading, please wait...");
        //mProgress.show();
    }


    @Override
    protected ArrayList<Offer> doInBackground(String... params) {

        Log.d(LOGTAG, "Inside list category offers...");

        String category_id = params[0];
        String user_id = params[1];
        String lat = params[2];
        String lng = params[3];
        String sLat = params[4];
        String slng = params[5];

        ArrayList<Offer> listOffers = OfferUtils.loadCategoryOffers(requestQueue, category_id, "0", user_id, lat, lng, sLat, slng);

        Log.d(LOGTAG, "List category offers size: "+listOffers.size());

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




