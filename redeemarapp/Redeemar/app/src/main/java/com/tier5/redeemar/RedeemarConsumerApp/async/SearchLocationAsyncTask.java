package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;

/**
 * Created by tier5 on 22/6/16.
 */


public class SearchLocationAsyncTask extends AsyncTask<String, Void, ArrayList<User>> {


    private static final String LOGTAG = "SearchFullAsyncTask";
    private UsersLoadedListener myComponent;
    private RequestQueue requestQueue;
    private Context context;
    private ProgressDialog dialog;

    public SearchLocationAsyncTask(UsersLoadedListener myComponent) {
        this.myComponent = myComponent;
        //this.context = ctx;
        Log.d(LOGTAG, "Inside SearchLocationAsyncTask constructor: "+myComponent);
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected ArrayList<User> doInBackground(String... params) {
        Log.d(LOGTAG, "Inside BG Task for ValidatePassCode");
        String location = params[0];
        ArrayList<User> locations = OfferUtils.loadSearchLocation(requestQueue, location);
        return locations;
    }

    @Override
    protected void onPostExecute(ArrayList<User> listLocations) {
        Log.d(LOGTAG, "Response is: "+listLocations.size());
        if (myComponent != null) {
            myComponent.onUsersLoaded(listLocations);
        }
    }

}




