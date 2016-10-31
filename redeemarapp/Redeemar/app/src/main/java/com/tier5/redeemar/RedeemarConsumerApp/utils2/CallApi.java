package com.tier5.redeemar.RedeemarConsumerApp.utils2;

import android.os.AsyncTask;

import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import java.util.HashMap;

/**
 * Created by root on 15/7/16.
 */
public class CallApi {
    String method;
    public AsyncResponse.Response delegate = null;

    public CallApi(String method)
    {
        this.method = method;
    }


    public void register(final HashMap<String, String> data, String route) {
        final String FEED_URL = UrlEndpoints.serverBaseUrl+route;
        class RegisterUserData extends AsyncTask<String, Void, String> {

            ConnectToServer ruc = new ConnectToServer(method);


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                delegate.processFinish(s);
            }

            @Override
            protected String doInBackground(String... params) {

                String result = ruc.sendPostRequest(FEED_URL,data);

                return  result;
            }
        }


        RegisterUserData ru = new RegisterUserData();

        ru.execute();

    }
}