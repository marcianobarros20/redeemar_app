package com.tier5.redeemar.RedeemarConsumerApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.MyOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyOffersActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * RecyclerView: The new recycler view replaces the list view. Its more modular and therefore we
     * must implement some of the functionality ourselves and attach it to our recyclerview.
     * <p/>
     * 1) Position items on the screen: This is done with LayoutManagers
     * 2) Animate & Decorate views: This is done with ItemAnimators & ItemDecorators
     * 3) Handle any touch events apart from scrolling: This is now done in our adapter's ViewHolder
     */

    private static final String LOGTAG = "MyOffers";

    private ArrayList<Offer> mDataSet = null;

    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private String redeemerId = "";
    private JSONArray offersArray;
    private BottomBar mBottomBar;
    private GPSTracker gps;
    double latitude = 0.0, longitude = 0.0;
    private Resources res;
    private SharedPreferences sharedpref;
    private String user_id = "";
    private SimpleDateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in. if not redirect user to logoff
        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        SharedPreferences.Editor editor = sharedpref.edit();


        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {

            Log.d(LOGTAG, "No user id found, redirecting to login");


            Intent i = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(i);
            finish();

        }
        else {


            user_id = sharedpref.getString(res.getString(R.string.spf_user_id), null);

            Log.d(LOGTAG, "User Id: " + user_id);

            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            setContentView(R.layout.offers_recycler);
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            tvEmptyView = (TextView) findViewById(R.id.empty_view);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            // Layout Managers:
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Item Decorator:
            // Check if the Android version code is greater than or equal to Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return resources.getDrawable(id, context.getTheme());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider, getApplicationContext().getTheme())));
            } else {
                //return resources.getDrawable(id);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
            }

            // create class object
            gps = new GPSTracker(this);

            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                if(latitude == 0 && longitude == 0) {
                    gps.showRequestPermissionDialog();
                }

                // \n is for new line
                //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                //gps.showSettingsAlert();
                gps.showRequestPermissionDialog();
            }


            mDataSet = new ArrayList<Offer>();
            //ArrayList<Creator> list = new ArrayList<Creator>;

            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(R.string.my_offers);
            }


            new GetOfferListAsyncTask().execute(redeemerId);


            mBottomBar = BottomBar.attach(this, savedInstanceState);
            mBottomBar.setMaxFixedTabs(5);
            mBottomBar.setDefaultTabPosition(1);
            mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
                @Override
                public void onMenuTabSelected(@IdRes int menuItemId) {

                    if (menuItemId == R.id.bottom_scan_offer) {
                        //Toast.makeText(getApplicationContext(), "Scan offer selected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MyOffersActivity.this, CloudReco.class);
                        startActivity(intent);
                    } else if (menuItemId == R.id.bottom_my_offers) {

                        //Toast.makeText(getApplicationContext(), "You are already here", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(MyOffersActivity.this, MyOffersActivity.class);
                        //startActivity(intent);
                    } else if (menuItemId == R.id.bottom_nearby) {

                        Toast.makeText(getApplicationContext(), "Neaby offers selected", Toast.LENGTH_SHORT).show();
                    } else if (menuItemId == R.id.bottom_browse_offers) {

                        //Toast.makeText(getApplicationContext(), "Browse offers selected", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MyOffersActivity.this, BrowseOffersActivity.class);
                        startActivity(intent);

                    }
                    else if (menuItemId == R.id.bottom_daily_deals) {

                        //Toast.makeText(getApplicationContext(), "Daily deals selected", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MyOffersActivity.this, LogoutActivity.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getApplicationContext(), "You have logged out.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {
                    if (menuItemId == R.id.bottom_scan_offer) {
                        //Toast.makeText(getApplicationContext(), "Scan offer reselected", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MyOffersActivity.this, CloudReco.class);
                        startActivity(intent);
                    } else if (menuItemId == R.id.bottom_my_offers) {

                        Toast.makeText(getApplicationContext(), "You are already here", Toast.LENGTH_SHORT).show();

                        //Intent intent = new Intent(MyOffersActivity.this, MyOffersActivity.class);
                        //startActivity(intent);
                    } else if (menuItemId == R.id.bottom_nearby) {

                        //Toast.makeText(getApplicationContext(), "Neaby offers reselected", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(OfferListActivity.this, MyOffersActivity.class);
                        //startActivity(intent);

                        Intent intent = new Intent(MyOffersActivity.this, NearMeActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (menuItemId == R.id.bottom_browse_offers) {

                        Intent intent = new Intent(MyOffersActivity.this, BrowseOffersActivity.class);
                        startActivity(intent);

                    } else if (menuItemId == R.id.bottom_daily_deals) {

                        //Toast.makeText(getApplicationContext(), "Daily deals reselected", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedpref.edit();

                        editor.remove(getString(R.string.spf_user_id));
                        editor.remove(getString(R.string.spf_email));

                        Intent intent = new Intent(MyOffersActivity.this, LoginActivity.class);
                        startActivity(intent);
                        editor.commit();
                        finish();

                        Toast.makeText(getApplicationContext(), "You have logged out.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            // Setting colors for different tabs when there's more than three of them.
            // You can set colors for tabs in three different ways as shown below.
            //mBottomBar.setBackgroundColor(ContextCompat.getColor(this, R.color.green_header));


            //mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.header_bgcolor));
            //mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.white));
            //mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.white));
            //mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.white));
            //mBottomBar.mapColorForTab(4, ContextCompat.getColor(this, R.color.white));
            //mBottomBar.mapColorForTab(2, "#7B1FA2");



        /* Scroll Listeners */
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.e("RecyclerView", "onScrollStateChanged");
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

        }
    }


    @Override
    public void onStop(){
        super.onStop();
        // put your code here... to get sharedpreferences
        Log.d(LOGTAG, "Inside onStop of My Offer");
        //new BrowseOffersAsyncTask().execute();

    }


    @Override
    public void onResume(){
        super.onResume();
        // put your code here... to get sharedpreferences
        Log.d(LOGTAG, "Inside onResume of My Offer");
        //new BrowseOffersAsyncTask().execute();

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }





    private class GetOfferListAsyncTask extends AsyncTask<String, Void, String> {


        String url = "";


        public GetOfferListAsyncTask() {

            url = UrlEndpoints.myOffersURL;

        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";


            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                JSONObject data = new JSONObject();
                //JSONObject auth=new JSONObject();
                //JSONObject parent=new JSONObject();
                data.put("webservice_name","myoffer");
                //data.put("reedemer_id", reedemer_id);
                data.put("user_id", user_id); // Deliberately sending user id 0


                OutputStream os = conn.getOutputStream();

                Log.d(LOGTAG, "Request: " + data.toString());


                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write("data="+data.toString());
                bufferedWriter.flush();
                bufferedWriter.close();


                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                Log.d(LOGTAG, "Do In background: " + response);
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String resp) {
            //do what ever you want with the response

            if (resp != null) {


                try {
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();


                    JSONObject reader = new JSONObject(resp);


                    if (reader.getString("messageCode").equals("R01001")) {

                        //Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                        offersArray = new JSONArray(reader.getString("data"));



                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int i = 0; i < offersArray.length(); i++) {
                            JSONObject jsonObject = offersArray.getJSONObject(i);



                            Offer offer = new Offer();


                            if(jsonObject.optString("offer_id") != null && jsonObject.optString("id").toString() != "") {
                                offer.setOfferId(jsonObject.optString("id").toString());
                                //Log.d(LOGTAG, "offer_id: "+jsonObject.optString("id").toString());
                            }




                            if(jsonObject.optString("offer_description").toString() != null) {
                                offer.setOfferDescription(jsonObject.optString("offer_description").toString());
                                //Log.d(LOGTAG, "offer_description: "+jsonObject.optString("offer_description").toString());
                            }


                            if(jsonObject.getString("retails_value") != "" && jsonObject.getString("retails_value").toString() != "") {
                                offer.setRetailvalue(Double.parseDouble(jsonObject.getString("retails_value").toString()));
                                //Log.d(LOGTAG, "retails_value: "+jsonObject.getString("retails_value").toString());
                            }


                            if(jsonObject.getString("pay_value") != null && jsonObject.getString("pay_value").toString() != "") {
                                offer.setPayValue(Double.parseDouble(jsonObject.getString("pay_value").toString()));
                                //Log.d(LOGTAG, "pay_value: "+jsonObject.getString("pay_value").toString());
                            }

                            if(jsonObject.getString("discount") != null && jsonObject.getString("discount").toString() != "") {
                                offer.setDiscount(Double.parseDouble(jsonObject.getString("discount").toString()));
                                //Log.d(LOGTAG, "discount: "+jsonObject.getString("discount").toString());
                            }

                            if(jsonObject.getString("value_calculate") != null && jsonObject.getString("value_calculate").toString() != "") {
                                offer.setValueCalculate(Integer.parseInt(jsonObject.getString("value_calculate").toString()));
                                //Log.d(LOGTAG, "value_calculate: "+jsonObject.getString("value_calculate").toString());
                            }


                            if(jsonObject.getString("expires") != null && jsonObject.getString("expires").toString() != "") {
                                offer.setExpiredInDays(Integer.parseInt(jsonObject.getString("expires")));
                                //Log.d(LOGTAG, "expires: "+jsonObject.getString("expires").toString());
                            }


                            if(jsonObject.getString("offer_image_path") != null && jsonObject.getString("offer_image_path").toString() != "") {
                                offer.setImageUrl(jsonObject.getString("offer_image_path"));
                                //Log.d(LOGTAG, "offer_image_path: "+jsonObject.getString("offer_image_path").toString());
                            }



                            //partnerSettingArray = reader.optJSONArray(reader.getString("partner_settings"));
                            JSONObject json_partner_settings = new JSONObject(jsonObject.getString("partner_settings"));


                            if(json_partner_settings.getString("price_range_id").toString() != null) {
                                offer.setPriceRangeId(json_partner_settings.getString("price_range_id").toString());
                                //Log.d(LOGTAG, "price_range_id: "+json_partner_settings.getString("price_range_id").toString());

                            }


                            JSONArray jsonMyOfferArray = new JSONArray(jsonObject.getString("myoffer_details"));

                            String address = "";

                            Log.d(LOGTAG, "My Offer Length: "+jsonMyOfferArray.length());

                            try {

                                if (jsonMyOfferArray.length() > 0) {

                                    JSONObject jsonCompanyObject = jsonMyOfferArray.getJSONObject(0);

                                    if (jsonCompanyObject.getString("validate_after") != null && jsonCompanyObject.getString("validate_after").toString() != "") {
                                        //Log.d(LOGTAG, "validate_after: " + jsonCompanyObject.getString("validate_after").toString());
                                        String validate_after = jsonCompanyObject.getString("validate_after");

                                        Date startDate = (Date) dateFormat.parse(jsonCompanyObject.getString("validate_after"));
                                        offer.setStartDate(startDate);


                                    }


                                    if (jsonCompanyObject.getString("validate_within") != null && jsonCompanyObject.getString("validate_within").toString() != "") {
                                        //Log.d(LOGTAG, "validate_within: " + jsonCompanyObject.getString("validate_within").toString());
                                        String validate_within = jsonCompanyObject.getString("validate_within");


                                        Date endDate = (Date) dateFormat.parse(jsonCompanyObject.getString("validate_within"));

                                        offer.setEndDate(endDate);


                                    }




                                }
                            } catch (ParseException e) {
                                    e.printStackTrace();
                            }




                            mDataSet.add(offer);


                        } // End of for loop for videos



                    } // End of if

                    if (mDataSet.isEmpty()) {
                        mRecyclerView.setVisibility(View.GONE);
                        tvEmptyView.setVisibility(View.VISIBLE);

                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tvEmptyView.setVisibility(View.GONE);
                    }



                    // Creating Adapter object

                    Log.d(LOGTAG, "Items in My Offers "+mDataSet.size());

                    MyOffersViewAdapter mAdapter = new MyOffersViewAdapter(getApplicationContext(), mDataSet, "MyOffers");


                    // Setting Mode to Single to reveal bottom View for one item in List
                    // Setting Mode to Mutliple to reveal bottom Views for multile items in List
                    ((MyOffersViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

                    mRecyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
        }


    }




}
