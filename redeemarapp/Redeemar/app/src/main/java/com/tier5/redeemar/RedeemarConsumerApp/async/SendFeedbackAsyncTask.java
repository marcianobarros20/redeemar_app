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


public class SendFeedbackAsyncTask extends AsyncTask<String, Void, String> {


    private static final String LOGTAG = "SendFeedbackAsync";
    private TaskCompleted myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog mProgress;
    private Context context;

    public SendFeedbackAsyncTask(TaskCompleted myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside SendFeedbackAsync constructor: "+myComponent);
    }

    public SendFeedbackAsyncTask() {

    }

    @Override
    protected void onPreExecute() {

        //mProgress = new ProgressDialog(context);
        //mProgress.setMessage("Loading, please wait...");
        //mProgress.show();
    }


    @Override
    protected String doInBackground(String... params) {

        Log.d(LOGTAG, "Inside list offers");

        String email = params[0];
        String user_id = params[1];
        String feedback = params[2];
        String rating = params[3];


        String res = OfferUtils.loadSendFeedback(requestQueue, email, user_id, feedback, rating);
        return res;
    }

    @Override
    protected void onPostExecute(String resp) {

        Log.d(LOGTAG, "Response is: "+resp);

        if (myComponent != null) {
            //myComponent.onOffersLoaded(listOffers);
            Log.d(LOGTAG, "Response is: "+resp);
            myComponent.onTaskComplete(resp);
        }
        //mProgress.dismiss();
    }





}




