package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.tier5.redeemar.RedeemarConsumerApp.async.SearchFullAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.SearchShortAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.async.ValidatePassCodeAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
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

public class SearchActivity extends AppCompatActivity implements TaskCompleted {

    private static final String LOGTAG = "Search";

    //private TextView tvAddress, tvOfferTitle, tvWhatYouGet, tvPriceRangeId, tvPayValue, tvDiscount, tvRetailValue, tvExpires;
    private EditText txtLocation, txtKeyword;
    private Button btnSearch;
    private SharedPreferences sharedpref;
    private RecyclerView mRecyclerView;
    private BrowseOffersViewAdapter mAdapter;
    private Toolbar toolbar;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog pd;

    String location = "", keyword = "";
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        txtLocation = (EditText) findViewById(R.id.location);
        txtKeyword = (EditText) findViewById(R.id.location);


        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);

        //mAdapter = new SearchViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        btnSearch = (Button) findViewById(R.id.btnSearch);



        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.search);



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


        txtLocation.addTextChangedListener(new TextWatcher() {



            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

                //new SearchShortAsyncTask(this, getApplicationContext()).execute(location, keyword);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
                Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                boolean flag = true;
                int errMsg = 0;


                location = txtLocation.getText().toString();
                keyword = txtKeyword.getText().toString();

                if(location.equals("")) {

                    flag = false;
                    errMsg = R.string.enter_valid_first_name;
                    txtLocation.requestFocus();
                }

                else if(keyword.equals("")) {
                    flag = false;
                    errMsg = R.string.enter_valid_last_name;
                    txtKeyword.requestFocus();
                }

                if(!flag) {

                    builder = new AlertDialog.Builder(getApplicationContext());//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(errMsg));
                    alertDialog = builder.create();

                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);
                    alertDialog.show();

                }
                else {

                    callSearchTask();
                    pd = new ProgressDialog(getApplicationContext());
                    pd.setMessage(getString(R.string.sending_data));
                    pd.show();

                }
            }
        });

    }

    private void callSearchTask() {

        /*if(location.equals("") && keyword.equals(""))
            new SearchFullAsyncTask(this, getApplicationContext()).execute(location, keyword);*/


    }




    @Override
    public void onTaskComplete(String listResult) {

        Log.d(LOGTAG, "OnTaskCompleted inside Search: "+listResult);



        /*if (listResult.size() > 0 && mAdapter != null) {
            mModels = listOffers;
            //Log.d(LOGTAG, "Inside Adapter: "+mAdapter);
            mAdapter = new BrowseOffersViewAdapter(getActivity().getApplicationContext(), listOffers, "BrowseOffers");

            mRecyclerOffers.setAdapter(mAdapter);
            mRecyclerOffers.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setText(getString(R.string.no_records));
        }*/



        /*
        builder = new AlertDialog.Builder(SearchActivity.this);//Context parameter
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });



        if(result.equalsIgnoreCase("R01001")) {

            builder.setMessage(getString(R.string.error_validation_success));
            alertDialog = builder.create();
            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_tick);
            alertDialog.show();

        }
        else {

            if(result.equalsIgnoreCase("R01002"))
                builder.setMessage(getString(R.string.offer_not_found));

            else if(result.equalsIgnoreCase("R01003"))
                builder.setMessage(getString(R.string.error_offer_expired));

            else if(result.equalsIgnoreCase("R01004"))
                builder.setMessage(getString(R.string.error_duplicate_validation));

            else if(result.equalsIgnoreCase("R01005"))
                builder.setMessage(getString(R.string.redemption_code_mismatch));

            else
                builder.setMessage(getString(R.string.offer_not_found));



            alertDialog = builder.create();
            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_cross);
            alertDialog.show();

        }*/



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
                Toast.makeText(SearchActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
