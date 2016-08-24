package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

/**
 * Created by tier5 on 22/6/16.
 */


public class ValidatePassCodeAsyncTask extends AsyncTask<String, Void, String> {


    private static final String LOGTAG = "ValidatePassCode";
    private TaskCompleted myComponent;
    private RequestQueue requestQueue;

    private Context context;

    public ValidatePassCodeAsyncTask(TaskCompleted myComponent) {
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside ValidatePassCode constructor: "+myComponent);
    }

    public ValidatePassCodeAsyncTask() {

    }

    @Override
    protected void onPreExecute() {


    }


    @Override
    protected String doInBackground(String... params) {

        Log.d(LOGTAG, "Inside BG Task for ValidatePassCode");


        String user_id = params[0];
        String offer_id = params[1];
        String pass_code = params[2];

        String res = OfferUtils.loadValidatePassCode(requestQueue, user_id, offer_id, pass_code);
        return res;
    }

    @Override
    protected void onPostExecute(String resp) {

        if (myComponent != null) {
            Log.d(LOGTAG, "Response is: "+resp);
            myComponent.onTaskComplete(resp);
        }
    }


}




