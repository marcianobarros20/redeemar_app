/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrandDetailsAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.MenuItemsAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.BrandLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.CategoriesLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.database.DatabaseHelper;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import android.support.multidex.MultiDex;


public class SplashScreenActivity extends Activity implements CategoriesLoadedListener
{

    private static final String LOGTAG = "SplashScreenActivity";
    private static long SPLASH_MILLIS = 500;

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;


    private DatabaseHelper db;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        db = new DatabaseHelper(this);

        Log.d(LOGTAG, "Count Categories: "+db.countCategories(0));
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
            R.layout.splash_screen, null, false);
        
        addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));

        // Clear all saved preferences
        editor.putString(getString(R.string.spf_redir_action), ""); // Storing Redirect Action
        editor.putString(getString(R.string.spf_redeemer_id), ""); // Storing Redeemar id
        editor.putString(getString(R.string.spf_campaign_id), ""); // Storing Campaign Id
        editor.putString(getString(R.string.spf_category_id), ""); // Storing category Id
        editor.commit();

        // Get the menun items from server
        new MenuItemsAsyncTask(this).execute("0");



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                // DO Nothing now
            }
            
        }, SPLASH_MILLIS);


    }

    @Override
    public void onCategoriesLoaded(ArrayList<Category> listCategories) {

        Log.d(LOGTAG, "Items inside Category List: "+listCategories.size());

        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

        SharedPreferences.Editor editor = sharedpref.edit();

        for(int i = 0; i < listCategories.size(); i++) {
            Category cat = listCategories.get(i);
            Log.d(LOGTAG, "Cat Name: "+cat.getCatName());
            db.addCategory(cat);
        }

        /*Gson gson = new Gson();
        List<Category> textList = new ArrayList<Category>();
        textList.addAll(listCategories);
        String jsonText = gson.toJson(textList);
        Log.d(LOGTAG, "Category Gson Text: "+jsonText);
        editor.putString(getString(R.string.spf_categories), jsonText);
        editor.commit();*/

        Intent intent = new Intent(SplashScreenActivity.this, BrowseOffersActivity.class);

        startActivity(intent);
        finish();

    }
}
