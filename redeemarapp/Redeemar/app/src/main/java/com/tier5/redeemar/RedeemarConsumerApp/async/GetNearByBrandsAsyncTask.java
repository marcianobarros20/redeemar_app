package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.MyItem;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by tier5 on 16/6/16.
 */

public class GetNearByBrandsAsyncTask extends AsyncTask<String, Void, ArrayList<User>> {

    private static final String LOGTAG = "NearByOffersAsync";
    private UsersLoadedListener myComponent;
    private Context mContext;
    private RequestQueue requestQueue;
    ProgressDialog mProgress;
    private TaskCompleted mCallback;
    String url = "";


    public GetNearByBrandsAsyncTask(UsersLoadedListener myComponent){
        this.myComponent = myComponent;
        Log.d(LOGTAG, "Inside GetNearByBrandsAsyncTask constructor");

    }

    @Override
    public void onPreExecute() {
        //mProgress = new ProgressDialog(mContext);
        //mProgress.setMessage("Loading, please wait...");
        //mProgress.show();
    }

    @Override
    protected ArrayList<User> doInBackground(String... params) {

        String lat = params[0];
        String lng = params[1];

        ArrayList<User> listBrands = OfferUtils.loadNearByBrands(requestQueue, lat, lng);
        return listBrands;

    }


    @Override
    protected void onPostExecute(ArrayList<User> listUsers) {
        if (myComponent != null) {
            myComponent.onUsersLoaded(listUsers);
        }
        //mProgress.dismiss();
    }





}