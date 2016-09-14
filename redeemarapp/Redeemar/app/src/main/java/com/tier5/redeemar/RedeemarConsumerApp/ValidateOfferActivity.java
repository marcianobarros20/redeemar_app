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
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ValidateOfferActivity extends AppCompatActivity implements TaskCompleted, OnMapReadyCallback {

    private static final String LOGTAG = "ValidateOffer";

    SupportMapFragment mapFragment;
    private TextView tvAddress, tvOfferTitle, tvWhatYouGet, tvPriceRangeId, tvDiscount, tvValidateAfter, tvValidateWithin;
    private NetworkImageView thumbnail, logoThumbnail;
    private ImageLoader mImageLoader;
    private EditText etRedeemCode;
    private Button btnRedeemScan, btnRedeemPassCode;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private GoogleMap mMap;
    private Resources res;
    private SharedPreferences sharedpref;
    private SimpleDateFormat fromDateFormat;
    private SimpleDateFormat toDateFormat;
    private Toolbar toolbar;
    Typeface myFont;

    private ProgressDialog mProgressDialog;

    int valCalc, valText;
    String offerId, user_id, perc_sym, cur_sym, days, save, off, disc, redemption_code;
    Double lat = 0.0, lon = 0.0, payValue = 0.0, retailValue = 0.0 ;
    String discount_text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validate_offer);

        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        tvAddress = (TextView) findViewById(R.id.address);
        tvOfferTitle = (TextView) findViewById(R.id.offer_title);
        tvWhatYouGet = (TextView) findViewById(R.id.what_you_get);
        tvPriceRangeId = (TextView) findViewById(R.id.price_range_id);
        //tvPayValue = (TextView) findViewById(R.id.pay_value);
        tvDiscount = (TextView) findViewById(R.id.discount);
        //tvRetailValue = (TextView) findViewById(R.id.retail_value);
        tvValidateWithin = (TextView) findViewById(R.id.validate_within);
        tvValidateAfter = (TextView) findViewById(R.id.validate_after);
        //btnBank = (Button) findViewById(R.id.btn_bank_offer);
        btnRedeemPassCode = (Button) findViewById(R.id.btn_redeem_by_passcode);
        btnRedeemScan = (Button) findViewById(R.id.btn_redeem_by_scan);

        tvAddress.setTypeface(myFont);
        tvOfferTitle.setTypeface(myFont);
        tvWhatYouGet.setTypeface(myFont);
        tvPriceRangeId.setTypeface(myFont);
        tvDiscount.setTypeface(myFont);
        tvValidateWithin.setTypeface(myFont);
        tvValidateAfter.setTypeface(myFont);

        thumbnail = (NetworkImageView) findViewById(R.id.thumbnail);
        logoThumbnail = (NetworkImageView) findViewById(R.id.logo_image);

        perc_sym = getResources().getString(R.string.percentage_symbol);
        cur_sym = getResources().getString(R.string.currency_symbol);
        save = getResources().getString(R.string.save);
        days = getResources().getString(R.string.days);
        off = getResources().getString(R.string.off);
        disc = getResources().getString(R.string.discount);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.validate);
            //getSupportActionBar().hide();

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

            fromDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            toDateFormat = new SimpleDateFormat("MM/dd/yyy HH:mm");


            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                offerId = extras.getString(getString(R.string.ext_offer_id));
                Log.d(LOGTAG, "Validate Offer Id: " + offerId);
                new GetOffeDetailsAsyncTask().execute(offerId);

            }


            btnRedeemPassCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOGTAG, "Starting validation by passcode...");

                    etRedeemCode = (EditText) findViewById(R.id.redeem_code);
                    redemption_code = etRedeemCode.getText().toString();

                    if(redemption_code.equals("")) {

                        builder = new AlertDialog.Builder(ValidateOfferActivity.this);//Context parameter
                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do stuff
                            }
                        });

                        builder.setMessage(getString(R.string.enter_redemption_code));
                        alertDialog = builder.create();

                        alertDialog.setTitle(getString(R.string.alert_title));
                        alertDialog.setIcon(R.drawable.icon_cross);

                        alertDialog.show();

                    }
                    else
                        callValidationTask();
                }
            });

            btnRedeemScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d(LOGTAG, "Starting validation by scanning...");
                    Intent intent = new Intent(ValidateOfferActivity.this, CloudReco.class);
                    intent.putExtra(getString(R.string.ext_activity), "Validation");
                    intent.putExtra(getString(R.string.ext_user_id), user_id);
                    intent.putExtra(getString(R.string.ext_offer_id), offerId);
                    startActivity(intent);
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
                Toast.makeText(ValidateOfferActivity.this, "Search coming soon", Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(LOGTAG, "Inside onMapReady");
        mMap = googleMap;
    }

    private void callValidationTask() {

        new ValidatePassCodeAsyncTask(this, getApplicationContext()).execute(user_id, offerId, redemption_code);
    }





    private class GetOffeDetailsAsyncTask extends AsyncTask<String, Void, String> {


        String url = "";
        //private ArrayList<Offer> mDataSet;

        public GetOffeDetailsAsyncTask() {

            url = UrlEndpoints.validateOfferDetailsURL;
        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            String offer_id = params[0];


            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);



                JSONObject data = new JSONObject();

                //data.put("webservice_name","offerdetail");
                data.put("offer_id", offer_id);
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

                    JSONObject reader = new JSONObject(resp);

                    Log.d(LOGTAG, "Message Data: " + reader.getString("data"));
                    if (!reader.getString("data").equals("")) {

                        //Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                        JSONObject jsonObject = new JSONObject(reader.getString("data"));

                        if(!jsonObject.isNull("offer_description") && !jsonObject.optString("offer_description").toString().equalsIgnoreCase("")) {
                            tvOfferTitle.setText(jsonObject.optString("offer_description").toString());
                            Log.d(LOGTAG, "Offer description: "+jsonObject.optString("offer_description").toString());
                        }

                        if(!jsonObject.isNull("what_you_get") && !jsonObject.optString("what_you_get").toString().equalsIgnoreCase("")) {
                            tvWhatYouGet.setText(jsonObject.optString("what_you_get").toString());
                            Log.d(LOGTAG, "What you get: "+jsonObject.optString("what_you_get").toString());
                        }


                        if(jsonObject.getString("retails_value") != "" && jsonObject.getString("retails_value").toString() != "") {

                            retailValue = jsonObject.getDouble("retails_value");

                            //tvRetailValue.setText(getString(R.string.currency_symbol).concat(jsonObject.getString("retails_value").toString()));
                            Log.d(LOGTAG, "retails_value: "+jsonObject.getString("retails_value").toString());
                        }


                        if(jsonObject.getString("pay_value") != null && jsonObject.getString("pay_value").toString() != "") {

                            payValue = jsonObject.getDouble("pay_value");

                            //tvPayValue.setText(getString(R.string.currency_symbol).concat(jsonObject.getString("pay_value").toString()));
                            Log.d(LOGTAG, "pay_value: "+jsonObject.getString("pay_value").toString());
                        }


                        if(jsonObject.getString("value_text") != null && jsonObject.getString("value_text").toString() != "") {
                            valText = Integer.parseInt(jsonObject.getString("value_text"));
                            Log.d(LOGTAG, "value_text: "+jsonObject.getString("value_text").toString());
                        }

                        if(jsonObject.getString("value_calculate") != null && jsonObject.getString("value_calculate").toString() != "") {
                            valCalc = Integer.parseInt(jsonObject.getString("value_calculate"));
                            Log.d(LOGTAG, "value_calculate: "+jsonObject.getString("value_calculate").toString());
                        }


                        if(retailValue > 0 && payValue > 0) {
                            discount_text = Utils.calculateDiscount(retailValue, payValue, valCalc);
                            Log.d(LOGTAG, "My Discount Value: "+discount_text);
                        }

                        /*if(valCalc == 2)
                            tvDiscount.setText(discount_text.concat(perc_sym));
                        else
                            tvDiscount.setText(cur_sym.concat(discount_text));*/

                        StringBuffer sDisc = new StringBuffer();

                        if(valCalc == 2) {

                            if(valText == 3) {
                                sDisc.append(save).append(" ").append(discount_text).append(perc_sym);
                            }
                            else if(valText == 2) {
                                sDisc.append(discount_text).append(perc_sym).append(" ").append(disc);
                            }
                            else {
                                sDisc.append(discount_text).append(perc_sym).append(" ").append(off);
                            }


                        }
                        else {

                            if(valText == 3) {
                                //sb.append(cur_sym).append(discVal).append(" ").append(off);
                                sDisc.append(save).append(" ").append(perc_sym).append(discount_text);

                            }
                            else if(valText == 2) {
                                sDisc.append(cur_sym).append(discount_text).append(" ").append(disc);
                            }
                            else {
                                sDisc.append(cur_sym).append(discount_text).append(" ").append(off);
                            }
                        }

                        tvDiscount.setText(sDisc);


                        if(!jsonObject.isNull("myoffer_details") && jsonObject.getString("myoffer_details").trim() != "") {



                            //JSONArray jsonMyOfferArray = new JSONArray(jsonObject.getString("myoffer_details"));

                            JSONObject jsonMyOfferObject = new JSONObject(jsonObject.getString("myoffer_details"));


                                try {

                                    GregorianCalendar calendar = new GregorianCalendar();

                                    if (!jsonMyOfferObject.isNull("validate_within")  && jsonMyOfferObject.getString("validate_within").toString().trim() != "") {
                                        Log.d(LOGTAG, "Validate Within: " + jsonMyOfferObject.getString("validate_within").toString());
                                        String validate_within = jsonMyOfferObject.getString("validate_within");

                                        Date date1 = fromDateFormat.parse(validate_within);
                                        tvValidateWithin.setText(toDateFormat.format(date1));
                                    }


                                    if (!jsonMyOfferObject.isNull("validate_after")  && jsonMyOfferObject.getString("validate_after").toString().trim() != "") {
                                        Log.d(LOGTAG, "Validate After: " + jsonMyOfferObject.getString("validate_after").toString());
                                        String validate_after = jsonMyOfferObject.getString("validate_after");

                                        Date date2 = fromDateFormat.parse(validate_after);

                                        tvValidateAfter.setText(toDateFormat.format(date2));

                                    }


                                } catch (ParseException ex) {

                                    Log.d(LOGTAG, "Exception in parsing date: "+ex.toString());

                                }

                        }


                        if(!jsonObject.isNull("offer_large_image_path") && jsonObject.getString("offer_large_image_path").toString() != "") {


                            String imageUrl = jsonObject.getString("offer_large_image_path");

                            imageUrl = UrlEndpoints.serverBaseUrl  + imageUrl;

                            Log.d(LOGTAG, "offer_image_path: "+imageUrl);


                            mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();

                            mImageLoader.get(imageUrl, ImageLoader.getImageListener(thumbnail, R.drawable.ic_placeholder, R.drawable.ic_placeholder));
                            thumbnail.setImageUrl(imageUrl, mImageLoader);


                        }


                        if(!jsonObject.isNull("logo_details") && !jsonObject.getString("logo_details").equalsIgnoreCase("")) {

                            JSONObject jsonLogoSettings = new JSONObject(jsonObject.getString("logo_details"));

                            if(!jsonLogoSettings.isNull("logo_name") && !jsonLogoSettings.getString("logo_name").equals("")) {

                                Log.d(LOGTAG, "My Logo URL 2: "+jsonLogoSettings.getString("logo_name"));

                                String logoImageUrl = UrlEndpoints.baseLogoMediumURL+ jsonLogoSettings.getString("logo_name");
                                mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();
                                //mImageLoader.get(logoImageUrl, ImageLoader.getImageListener(logoThumbnail, R.drawable.icon_watermark, android.R.drawable.ic_dialog_alert));
                                mImageLoader.get(logoImageUrl, ImageLoader.getImageListener(logoThumbnail, 0, 0));
                                logoThumbnail.setImageUrl(logoImageUrl, mImageLoader);
                            }


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

                            Address addr = new Address();


                            if (!jsonCompanyObject.isNull("address") && jsonCompanyObject.getString("address").toString() != "") {
                                Log.d(LOGTAG, "address: " + jsonCompanyObject.getString("address").toString());
                                addr.setStreet(jsonCompanyObject.getString("address"));
                            }
                            else
                                addr.setStreet("");



                            if (!jsonCompanyObject.isNull("city") && jsonCompanyObject.getString("city").toString() != "") {
                                Log.d(LOGTAG, "city: " + jsonCompanyObject.getString("city").toString());
                                addr.setCity(jsonCompanyObject.getString("city"));
                            }
                            else
                                addr.setCity("");


                            if (!jsonCompanyObject.isNull("state") && jsonCompanyObject.getString("state").toString() != "") {
                                Log.d(LOGTAG, "state: " + jsonCompanyObject.getString("state").toString());
                                addr.setState(jsonCompanyObject.getString("state"));
                            }
                            else
                                addr.setState("");

                            if (!jsonCompanyObject.isNull("zipcode") && jsonCompanyObject.getString("zipcode").toString() != "") {
                                Log.d(LOGTAG, "zipcode: " + jsonCompanyObject.getString("zipcode").toString());
                                addr.setZip(jsonCompanyObject.getString("zipcode"));
                            }
                            else
                                addr.setZip("");

                            if (!jsonCompanyObject.isNull("location") && jsonCompanyObject.getString("location").toString() != "") {
                                Log.d(LOGTAG, "location: " + jsonCompanyObject.getString("location").toString());
                                addr.setLocation(jsonCompanyObject.getString("location"));
                            }
                            else
                                addr.setLocation("");

                            String fullAddress = Utils.getFormattedAddress(addr);

                            tvAddress.setText(fullAddress);

                        }



                        if(mMap != null) {

                            Log.d(LOGTAG, "MAP: "+lat);
                            Log.d(LOGTAG, "MAP: "+lat);

                            LatLng point = new LatLng(lat, lat);

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



                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }



    @Override
    public void onTaskComplete(String result) {

        Log.d(LOGTAG, "OnTaskCompleted inside ValidateOfferActivity: "+result);


        builder = new AlertDialog.Builder(ValidateOfferActivity.this);//Context parameter
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do stuff
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

        }

    }





    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }




}
