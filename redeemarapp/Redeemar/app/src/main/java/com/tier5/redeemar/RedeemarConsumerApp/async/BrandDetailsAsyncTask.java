package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.BrandLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.network.VolleySingleton;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tier5 on 22/6/16.
 */


public class BrandDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {


    private static final String LOGTAG = "BrowseOffersAsync";
    private BrandLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog mProgress;
    private Context context;

    public BrandDetailsAsyncTask(BrandLoadedListener myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside BrandDetailsAsyncTask constructor: "+myComponent);

        //this.context = ctx;

    }

    @Override
    protected void onPreExecute() {

        //mProgress = new ProgressDialog(this);
        //mProgress.setMessage("Loading, please wait...");
        //mProgress.show();
    }


    @Override
    protected ArrayList<Object> doInBackground(String... params) {

        String target_id = params[0];
        ArrayList<Object> brandInfo = OfferUtils.loadBrandDetails(requestQueue, target_id);
        return brandInfo;
    }

    @Override
    protected void onPostExecute(ArrayList<Object> brandInfo) {
        if (myComponent != null) {
            myComponent.onBrandLoaded(brandInfo);
        }
        //mProgress.dismiss();
    }




}




