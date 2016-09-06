package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

/**
 * Created by tier5 on 22/6/16.
 */


public class ValidatePassCodeAsyncTask extends AsyncTask<String, Void, String> {


    private static final String LOGTAG = "ValidatePassCode";
    private TaskCompleted myComponent;
    private RequestQueue requestQueue;
    private Context context;
    private ProgressDialog dialog;

    public ValidatePassCodeAsyncTask(TaskCompleted myComponent, Context ctx) {

        this.myComponent = myComponent;
        this.context = ctx;
        dialog = new ProgressDialog(ctx);
        Log.d(LOGTAG, "Inside ValidatePassCode constructor: "+myComponent);

    }

    public ValidatePassCodeAsyncTask() {

    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Processing, please wait.");
        dialog.show();

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
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        Log.d(LOGTAG, "Response is: "+resp);
        if (myComponent != null) {
            myComponent.onTaskComplete(resp);
        }
    }


}




