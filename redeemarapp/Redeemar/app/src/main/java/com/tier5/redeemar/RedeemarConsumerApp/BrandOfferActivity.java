package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.SearchViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
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

public class BrandOfferActivity extends AppCompatActivity implements OffersLoadedListener {

    private static final String LOGTAG = "BrandOffer";

    private ArrayList<Offer> mListOffers;
    private BrowseOffersViewAdapter mAdapter;
    private SearchViewAdapter sAdapter;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;
    private ProgressDialog pd;
    String userId, redeemarId, viewType, selfLat, selfLon;
    Double latitude, longitude;
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);



        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));




        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
            userId = sharedpref.getString(res.getString(R.string.spf_user_id), "0");
            sharedpref.getString(res.getString(R.string.spf_first_name), "");
        }
        mListOffers = new ArrayList<Offer>();

        //setHasOptionsMenu(true);
        Bundle args1 = getIntent().getExtras();

        if(args1 != null && args1.size() > 0) {

            redeemarId = args1.getString(getString(R.string.ext_redeemar_id), "");
            viewType = args1.getString(getString(R.string.ext_view_type), "");

        }

        if(sharedpref.getString(res.getString(R.string.spf_last_lat), null) != null) {
            latitude = Double.parseDouble(sharedpref.getString(res.getString(R.string.spf_last_lat), null));

            if(selfLat.equals(""))
                selfLat = String.valueOf(latitude);

            if(selfLon.equals(""))
                selfLon = String.valueOf(latitude);
        }


        if(sharedpref.getString(res.getString(R.string.spf_last_lon), null) != null) {
            longitude = Double.parseDouble(sharedpref.getString(res.getString(R.string.spf_last_lon), null));


            if(selfLon.equals(""))
                selfLon = String.valueOf(longitude);
        }

        getSupportActionBar().setTitle(R.string.browse_offers);



        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.offer_details);

            //setSupportActionBar(toolbar);
            //Your toolbar is now an action bar and you can use it like you always do, for example:
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // back button pressed
                finish();
                }
            });

        }




    }



    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.menu_main, menu);
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
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Toast.makeText(BrandOfferActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {

    }



    private void showProgressDialog() {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }

        if (pd != null && !pd.isShowing()) {
            pd.setMessage(getString(R.string.loading));
            pd.setIndeterminate(true);
            pd.show();

        }

    }

    private void hideProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


}
