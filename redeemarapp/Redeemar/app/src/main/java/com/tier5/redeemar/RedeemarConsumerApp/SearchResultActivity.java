package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.CategoryViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrandOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.CategoryOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.database.DatabaseHelper;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements OffersLoadedListener {

    private static final String LOGTAG = "SearchResultActivity";

    private RecyclerView mRecyclerView;
    private BrowseOffersViewAdapter mAdapter;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;


    private SharedPreferences sharedpref;
    private Toolbar toolbar;
    private DatabaseHelper db;
    private int catId = 0, catLevel = 0;
    private String catName = "", userId = "";

    private double latitude = 0.0, longitude = 0.0;

    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

        db = new DatabaseHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);

        mAdapter = new BrowseOffersViewAdapter(this, "BrowseOffers");
        //mRecyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();

        // Check whether the usre is validating the
        if (extras != null) {

            String activity = extras.getString(getString(R.string.ext_activity));

        }


        // create class object
        GPSTracker gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            if(latitude == 0 && longitude == 0) {
                gps.showSettingsAlert();
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        Log.d(LOGTAG, "Lat Values: "+latitude);
        Log.d(LOGTAG, "Long Values: "+longitude);

        if(sharedpref.getString(getString(R.string.spf_user_id), null) != null) {
            userId = sharedpref.getString(getString(R.string.spf_user_id), "0");
        }

        if(sharedpref.getInt(getString(R.string.spf_category_id), 0) != 0) {
            catId = sharedpref.getInt(getString(R.string.spf_category_id), 0);
        }

        if(sharedpref.getString(getString(R.string.spf_category_name), null) != null) {
            catName = sharedpref.getString(getString(R.string.spf_category_name), "");
        }

        if(catId != 0) {

            new CategoryOffersAsyncTask(this).execute(String.valueOf(catId), userId, String.valueOf(latitude), String.valueOf(longitude));
        }

        Log.d(LOGTAG, "Cat Id: " + catId);
        Log.d(LOGTAG, "Cat Name: " + catName);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if(!catName.equals(""))
                getSupportActionBar().setTitle(catName);
            else
                getSupportActionBar().setTitle(R.string.category);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            //Your toolbar is now an action bar and you can use it like you always do, for example:
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // back button pressed
                finish();
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                }
            });

        }

    }


    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {

        if (listOffers.size() > 0 && mAdapter != null) {
            mAdapter = new BrowseOffersViewAdapter(getApplicationContext(), listOffers, "BrowseOffers");
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setText(getString(R.string.no_records));
        }

    }

}
