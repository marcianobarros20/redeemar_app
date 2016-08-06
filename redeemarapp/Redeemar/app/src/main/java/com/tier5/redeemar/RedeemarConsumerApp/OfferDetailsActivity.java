package com.tier5.redeemar.RedeemarConsumerApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class OfferDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String LOGTAG = "OfferDetail";

    SupportMapFragment mapFragment;
    private TextView tvAddress, tvOfferTitle, tvWhatYouGet, tvPriceRangeId, tvPayValue, tvDiscount, tvRetailValue, tvExpires;
    private NetworkImageView thumbnail;
    private ImageLoader mImageLoader;
    private Button btnBank,  btnPass,  btnRedeem;
    private SharedPreferences sharedpref;
    private GoogleMap mMap;
    private Toolbar toolbar;

    int valCalc;
    String offerId, perc_sym, cur_sym;
    Double lat, lon;
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));


        tvAddress = (TextView) findViewById(R.id.address);
        tvOfferTitle = (TextView) findViewById(R.id.offer_title);
        tvWhatYouGet = (TextView) findViewById(R.id.what_you_get);
        tvPriceRangeId = (TextView) findViewById(R.id.price_range_id);
        tvPayValue = (TextView) findViewById(R.id.pay_value);
        tvDiscount = (TextView) findViewById(R.id.discount);
        tvRetailValue = (TextView) findViewById(R.id.retail_value);
        tvExpires = (TextView) findViewById(R.id.expires);
        btnBank = (Button) findViewById(R.id.btn_bank_offer);
        btnPass = (Button) findViewById(R.id.btn_pass_offer);
        btnRedeem = (Button) findViewById(R.id.btn_redeem_offer);


        tvAddress.setTypeface(myFont);
        tvOfferTitle.setTypeface(myFont);
        tvWhatYouGet.setTypeface(myFont);
        tvPriceRangeId.setTypeface(myFont);
        tvPayValue.setTypeface(myFont);
        tvDiscount.setTypeface(myFont);
        tvRetailValue.setTypeface(myFont);
        tvExpires.setTypeface(myFont);
        tvPayValue.setTypeface(myFont);


        thumbnail = (NetworkImageView) findViewById(R.id.thumbnail);

        perc_sym = getResources().getString(R.string.percentage_symbol);
        cur_sym = getResources().getString(R.string.currency_symbol);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            offerId = extras.getString(getString(R.string.ext_offer_id));
            Log.d(LOGTAG, "Offer Id: "+offerId);

        }

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



        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode



                Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {

                    SharedPreferences.Editor editor = sharedpref.edit();
                    editor.putString(res.getString(R.string.spf_redir_action), "BANK_OFFER"); // Storing Email

                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    intent.putExtra(res.getString(R.string.ext_activity), "OfferDetails"); // Settings the activty name where it will be redirected to
                    view.getContext().startActivity(intent);

                }
                else {

                    String userId = sharedpref.getString(res.getString(R.string.spf_user_id), null);

                    Log.d(LOGTAG, "View Adapter Offer Id: "+offerId);
                    Log.d(LOGTAG, "View Adapter User Id: "+userId);

                    new SaveOfferAsyncTask().execute("bank", userId, offerId);



                }
            }
        });


        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
                Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {

                    SharedPreferences.Editor editor = sharedpref.edit();
                    editor.putString(res.getString(R.string.spf_redir_action), "PASS_OFFER");

                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    intent.putExtra(res.getString(R.string.ext_activity), "OfferDetails"); // Settings the activty name where it will be redirected to
                    view.getContext().startActivity(intent);

                }
                else {

                    String userId = sharedpref.getString(res.getString(R.string.spf_user_id), null);

                    Log.d(LOGTAG, "View Adapter Offer Id: "+offerId);
                    Log.d(LOGTAG, "View Adapter User Id: "+userId);

                    new SaveOfferAsyncTask().execute("pass", userId, offerId);

                }
            }
        });





    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.

        /*LatLng point = new LatLng(40.758896, -73.985130);

        mMap.addMarker(new MarkerOptions().position(point).title("Brand Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(point, 10));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));*/


        new GetOffeDetailsAsyncTask().execute(offerId);

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
                Toast.makeText(OfferDetailsActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }





    private class GetOffeDetailsAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", frameVideo = "";
        //private ArrayList<Offer> mDataSet;

        public GetOffeDetailsAsyncTask() {

            url = UrlEndpoints.getOfferDetailsURL;
        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            String offer_id = params[0];

            Log.d(LOGTAG, "Offer Details Offer Id: "+offer_id);


            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject data = new JSONObject();

                data.put("webservice_name","offerdetail");
                data.put("offer_id", offer_id);

                Resources res = getResources();
                sharedpref = getApplicationContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
                Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {
                    data.put("user_id", 0); // Deliberately sending user id 0
                }
                else {
                    data.put("user_id", sharedpref.getString(res.getString(R.string.spf_user_id), null)); // Deliberately sending user id 0

                }





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

                    Log.d(LOGTAG, "Message Data: " + reader.getString("data"));
                    if (!reader.getString("data").equals("")) {

                        //Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                        //JSONObject jsonObject = new JSONObject(reader.getString("data"));

                        JSONArray offerArray = new JSONArray(reader.getString("data"));

                        Log.d(LOGTAG, "Length: " + offerArray.length());

                        if(offerArray.length() == 1) {

                            JSONObject jsonObject = offerArray.getJSONObject(0);



                            if(jsonObject.optString("offer_description").toString() != null) {
                                tvOfferTitle.setText(jsonObject.optString("offer_description").toString());
                                Log.d(LOGTAG, "offer_description: "+jsonObject.optString("offer_description").toString());
                            }


                            if(jsonObject.optString("what_you_get").toString() != null) {
                                tvWhatYouGet.setText(jsonObject.optString("what_you_get").toString());
                                Log.d(LOGTAG, "what_you_get: "+jsonObject.optString("what_you_get").toString());
                            }


                            if(jsonObject.getString("retails_value") != "" && jsonObject.getString("retails_value").toString() != "") {

                                tvRetailValue.setText(getString(R.string.currency_symbol).concat(jsonObject.getString("retails_value").toString()));
                                Log.d(LOGTAG, "retails_value: "+jsonObject.getString("retails_value").toString());
                            }


                            if(jsonObject.getString("pay_value") != null && jsonObject.getString("pay_value").toString() != "") {

                                tvPayValue.setText(getString(R.string.currency_symbol).concat(jsonObject.getString("pay_value").toString()));
                                Log.d(LOGTAG, "pay_value: "+jsonObject.getString("pay_value").toString());
                            }


                            if(jsonObject.getString("value_calculate") != null && jsonObject.getString("value_calculate").toString() != "") {
                                valCalc = Integer.parseInt(jsonObject.getString("value_calculate"));
                                Log.d(LOGTAG, "value_calculate: "+jsonObject.getString("value_calculate").toString());
                            }

                            if(jsonObject.getString("discount") != null && jsonObject.getString("discount").toString() != "") {

                                if(valCalc % 2 == 0)
                                    tvDiscount.setText(jsonObject.getString("discount").toString().concat(perc_sym));
                                else
                                    tvDiscount.setText(jsonObject.getString("discount").toString().concat(perc_sym));
                                //Log.d(LOGTAG, "discount: "+jsonObject.getString("discount").toString());
                            }




                            if(jsonObject.getString("expires") != null && jsonObject.getString("expires").toString() != "") {
                                String expiredIn = jsonObject.getString("expires").concat(" ").concat(getString(R.string.days));
                                tvExpires.setText(expiredIn);
                                Log.d(LOGTAG, "expires: "+jsonObject.getString("expires").toString());
                            }


                            if(jsonObject.getString("offer_large_image_path") != null && jsonObject.getString("offer_large_image_path").toString() != "") {


                                String imageUrl = jsonObject.getString("offer_large_image_path");

                                imageUrl = UrlEndpoints.serverBaseUrl  + imageUrl;

                                Log.d(LOGTAG, "offer_large_image_path: "+imageUrl);



                                mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();

                                mImageLoader.get(imageUrl, ImageLoader.getImageListener(thumbnail,
                                        R.drawable.icon_watermark, android.R.drawable
                                                .ic_dialog_alert));
                                thumbnail.setImageUrl(imageUrl, mImageLoader);

                            }

                            if(!jsonObject.isNull("latitude") && jsonObject.getString("latitude").toString().trim() != "") {

                                Log.d(LOGTAG, "latitude: "+jsonObject.getString("latitude").toString());
                                try {
                                    lat = Double.parseDouble(jsonObject.getString("latitude").toString());
                                } catch (NumberFormatException e) {
                                    lat = 0.0; // your default value
                                }
                            }

                            if(!jsonObject.isNull("longitude") && jsonObject.getString("longitude").toString().trim() != "") {

                                Log.d(LOGTAG, "longitude: "+jsonObject.getString("longitude").toString());


                                try {
                                    lon = Double.parseDouble(jsonObject.getString("longitude").toString());
                                } catch (NumberFormatException e) {
                                    lon = 0.0; // your default value
                                }

                            }

                            JSONArray jsonCompanyArray = new JSONArray(jsonObject.getString("company_detail"));

                            String address = "";

                            Log.d(LOGTAG, "Company Length: "+jsonCompanyArray.length());

                            if(jsonCompanyArray.length() == 1) {

                                JSONObject jsonCompanyObject = jsonCompanyArray.getJSONObject(0);


                                if (!jsonCompanyObject.isNull("address") && jsonCompanyObject.getString("address").toString() != "") {
                                    Log.d(LOGTAG, "address: " + jsonCompanyObject.getString("address").toString());
                                    address = jsonCompanyObject.getString("address");
                                    tvAddress.setText(address);
                                }

                            }


                            if(mMap != null) {

                                LatLng point = new LatLng(lat, lon);

                                if(address != "")
                                    mMap.addMarker(new MarkerOptions().position(point).title(address));
                                else
                                    mMap.addMarker(new MarkerOptions().position(point).title(getString(R.string.brand_location)));

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                            }


                            JSONObject json_partner_settings = new JSONObject(jsonObject.getString("partner_settings"));


                            if(!json_partner_settings.isNull("price_range_id") && json_partner_settings.getString("price_range_id").toString() != "") {
                                tvPriceRangeId.setText(json_partner_settings.getString("price_range_id"));
                                Log.d(LOGTAG, "price_range_id: "+json_partner_settings.getString("price_range_id").toString());

                            }

                        }

                    } // End of if


                    if (reader.getString("messageCode").equals("R01002")) {

                        Toast.makeText(getApplicationContext(), "You have already banked this offer", Toast.LENGTH_SHORT).show();
                        btnPass.setVisibility(View.GONE);
                        btnBank.setVisibility(View.GONE);
                    }
                    else if (reader.getString("messageCode").equals("R01003")) {
                        Toast.makeText(getApplicationContext(), "You have already passed this offer", Toast.LENGTH_SHORT).show();
                        btnBank.setVisibility(View.GONE);
                        btnPass.setVisibility(View.GONE);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }




    // Either pass or bank offers using this service
    public class SaveOfferAsyncTask extends AsyncTask<String, Void, String> {


        String bankUrl = "", passUrl = "", action = "";
        //private ArrayList<Offer> mDataSet;

        public SaveOfferAsyncTask() {

            bankUrl = UrlEndpoints.bankOffersURL;
            passUrl = UrlEndpoints.passOffersURL;

        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";

            action = params[0];
            String user_id = params[1];
            String offer_id = params[2];



            try {
                if(action.equalsIgnoreCase("pass"))
                    myUrl = new URL(passUrl);
                else
                    myUrl = new URL(bankUrl);

                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject data = new JSONObject();
                //JSONObject auth=new JSONObject();
                //JSONObject parent=new JSONObject();
                if(action.equalsIgnoreCase("bank")) {
                    data.put("webservice_name", "bankoffer");
                }
                else if(action.equalsIgnoreCase("pass")) {
                    data.put("webservice_name", "passkoffer");
                }
                data.put("user_id", user_id);
                data.put("offer_id", offer_id);

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

                //Log.d(LOGTAG, "Do In background: " + response);
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

                        // TODO: Return value

                        if(action.equalsIgnoreCase("pass")) {

                            Intent intent = new Intent(OfferDetailsActivity.this, BrowseOffersActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(getApplicationContext(), "Offer has been passed", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Intent intent = new Intent(OfferDetailsActivity.this, BrowseOffersActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(getApplicationContext(), "Offer has been banked", Toast.LENGTH_SHORT).show();
                        }



                    } // End of if

                    else if (reader.getString("messageCode").equals("R01002")) {

                        // TODO: Return value

                        if(action.equalsIgnoreCase("pass")) {

                            Toast.makeText(getApplicationContext(), "You have already passed this offer", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Toast.makeText(getApplicationContext(), "You have already banked this offer", Toast.LENGTH_SHORT).show();
                        }

                    } // End of if




                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }

}
