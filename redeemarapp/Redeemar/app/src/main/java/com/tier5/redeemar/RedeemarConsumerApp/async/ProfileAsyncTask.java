package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

/**
 * Created by tier5 on 22/6/16.
 */


public class ProfileAsyncTask extends AsyncTask<String, Void, String> {


    private static final String LOGTAG = "ProfileAsyncTask";
    private TaskCompleted myComponent;
    private RequestQueue requestQueue;

    private Context context;

    public ProfileAsyncTask(TaskCompleted myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside ProfileAsyncTask constructor: "+myComponent);
    }

    public ProfileAsyncTask() {

    }

    @Override
    protected void onPreExecute() {


    }


    @Override
    protected String doInBackground(String... params) {

        Log.d(LOGTAG, "Inside bg task for ProfileAsyncTask");


        String user_id = params[0];
        String first_name = params[1];
        String last_name = params[2];
        String email = params[3];
        String phone = params[4];

        String res = OfferUtils.loadUpdateProfile(requestQueue, user_id, first_name, last_name, email, phone);
        return res;
    }

    @Override
    protected void onPostExecute(String resp) {

        if (myComponent != null) {
            //myComponent.onOffersLoaded(listOffers);
            Log.d(LOGTAG, "Response is: "+resp);
            myComponent.onTaskComplete(resp);
        }
        //mProgress.dismiss();
    }





}




