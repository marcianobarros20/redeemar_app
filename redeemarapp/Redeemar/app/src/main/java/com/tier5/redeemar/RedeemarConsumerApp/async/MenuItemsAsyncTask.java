package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.CategoriesLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.network.VolleySingleton;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.MenuUtils;
import com.tier5.redeemar.RedeemarConsumerApp.utils.OfferUtils;

import java.util.ArrayList;

/**
 * Created by tier5 on 22/6/16.
 */


public class MenuItemsAsyncTask extends AsyncTask<String, Void, ArrayList<Category>> {


    private static final String LOGTAG = "MenuItemsAsyncTask";
    private CategoriesLoadedListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private ProgressDialog mProgress;
    private Context context;

    public MenuItemsAsyncTask(CategoriesLoadedListener myComponent) {
        this.myComponent = myComponent;
    }

    @Override
    protected void onPreExecute() {

        //mProgress = new ProgressDialog(this);
        //mProgress.setMessage("Loading, please wait...");
        //mProgress.show();
    }


    @Override
    protected ArrayList<Category> doInBackground(String... params) {

        Log.d(LOGTAG, "Inside list categories");

        String parent_id = params[0];

        ArrayList<Category> listMenuItems = MenuUtils.loadMenuItem(requestQueue, parent_id);
        return listMenuItems;
    }

    @Override
    protected void onPostExecute(ArrayList<Category> listMenuItems) {
        if (myComponent != null) {
            myComponent.onCategoriesLoaded(listMenuItems);
        }
        //mProgress.dismiss();
    }




}




