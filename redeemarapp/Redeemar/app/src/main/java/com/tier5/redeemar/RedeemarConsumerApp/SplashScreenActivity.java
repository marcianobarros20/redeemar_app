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
import android.widget.Toast;
import com.tier5.redeemar.RedeemarConsumerApp.async.FetchLocationAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.LocationFetchedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.SuperConnectionDetector;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

public class SplashScreenActivity extends Activity implements CategoriesLoadedListener, LocationFetchedListener
{

    private static final String LOGTAG = "SplashScreenActivity";
    private static long SPLASH_MILLIS = 500;
    private static final int LOCATION_SETTINGS_REQUEST = 1;
    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private GPSTracker gps;
    private double latitude = 0.0, longitude = 0.0;
    private SuperConnectionDetector cd;
    private boolean isInternetPresent = false;

    private DatabaseHelper db;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Check internet connectivity
        cd = new SuperConnectionDetector(this);

        // Check if Internet is available
        isInternetPresent = cd.isConnectingToInternet();
        Log.d(LOGTAG, "Internet enabled: "+isInternetPresent);


        // Define Resources
        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();
        db = new DatabaseHelper(this);
        int cnt = db.countCategories(0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.splash_screen, null, false);

        addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Clear all saved preferences
        editor.putString(getString(R.string.spf_redir_action), ""); // Storing Redirect Action
        editor.putString(getString(R.string.spf_redeemer_id), ""); // Storing Redeemar id
        editor.putString(getString(R.string.spf_campaign_id), ""); // Storing Campaign Id
        editor.putString(getString(R.string.spf_category_id), ""); // Storing category Id
        editor.commit();
        // Get the menun items from server
        new MenuItemsAsyncTask(this).execute("0");

    }

    @Override
    public void onCategoriesLoaded(ArrayList<Category> listCategories) {

        //Log.d(LOGTAG, "Items inside Category List: "+listCategories.size());


        // return 0.75 if it's LDPI
        // return 1.0 if it's MDPI
        // return 1.5 if it's HDPI
        // return 2.0 if it's XHDPI
        // return 3.0 if it's XXHDPI
        // return 4.0 if it's XXXHDPI

        Log.d(LOGTAG, "Screen Density: "+getResources().getDisplayMetrics().density);
        Toast.makeText(SplashScreenActivity.this, "Screen Density: "+getResources().getDisplayMetrics().density, Toast.LENGTH_LONG).show();

        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

        SharedPreferences.Editor editor = sharedpref.edit();

        for(int i = 0; i < listCategories.size(); i++) {
            Category cat = listCategories.get(i);
            //Log.d(LOGTAG, "Cat Name: "+cat.getCatName());
            db.addCategory(cat);
        }


        /*Gson gson = new Gson();
        List<Category> textList = new ArrayList<Category>();
        textList.addAll(listCategories);
        String jsonText = gson.toJson(textList);
        Log.d(LOGTAG, "Category Gson Text: "+jsonText);
        editor.putString(getString(R.string.spf_categories), jsonText);
        editor.commit();*/


        //check permission in marshmallow
        if(Utils.checkLocationPermissions(this, getApplicationContext())){
            // Fetch location
            Log.d(LOGTAG, "Inside location");
            getMyLocation();

        } else {
            Log.d(LOGTAG, "Location permission not granted");
        }


        Intent intent = new Intent(SplashScreenActivity.this, BrowseOffersActivity.class);
        startActivity(intent);
        finish();

    }


    public void getMyLocation() {
        // Initialize GPSTracker
        gps = new GPSTracker(this);

        // If GPSTracker can get the location
        if(gps.canGetLocation()) {

            // Your current location (Self Location)
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Log.d(LOGTAG, "My Values Lat: " + latitude);
            Log.d(LOGTAG, "My Values Long: " + longitude);

            // Save the latitude and longitude in SharedPreferences
            // By default your current location is the location you are searching the offers from
            if (latitude != 0.0 && longitude != 0.0) {
                editor.putString(res.getString(R.string.spf_last_lat), String.valueOf(latitude));
                editor.putString(res.getString(R.string.spf_last_lon), String.valueOf(longitude));
                editor.commit();
            }


            // CHeck if internet is enabled in device
            if (isInternetPresent) {
                // If internet is enabled then call a API which will get location
                new FetchLocationAsyncTask(this, getApplicationContext()).execute(String.valueOf(latitude), String.valueOf(longitude));
            }
        }
    }


    @Override
    public void onLocationFetched(User locUser) {

        String curLoc = "";

        // Get the location from web services
        if(locUser.getCity() != null && !locUser.getCity().equals("")) {
            curLoc = locUser.getCity();
            Log.d(LOGTAG, "City: "+curLoc);
        }
        else if(locUser.getLocation() != null && !locUser.getLocation().equals("")) {
            curLoc = locUser.getLocation();
            Log.d(LOGTAG, "Location: "+curLoc);
        }
        else if(locUser.getState() != null && !locUser.getState().equals("")) {
            curLoc = locUser.getState();
            Log.d(LOGTAG, "State: "+curLoc);

        }
        else if(locUser.getZipcode() != null && !locUser.getZipcode().equals("")) {
            curLoc = locUser.getZipcode();
            Log.d(LOGTAG, "Zip: "+curLoc);
        }



        if(!curLoc.equals("")) {

            Log.d(LOGTAG, "My Location Point: "+curLoc);

            editor.putString(res.getString(R.string.spf_last_lat), String.valueOf(latitude));
            editor.putString(res.getString(R.string.spf_last_lon), String.valueOf(longitude));
            editor.putString(res.getString(R.string.spf_last_location_keyword), curLoc); // Set location keyword
            //editor.putString(res.getString(R.string.spf_location_keyword), curLoc); // Set location keyword
            editor.commit();

        }


    }

}