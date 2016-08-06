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
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.Date;
import java.util.GregorianCalendar;

public class ValidateOfferActivity extends AppCompatActivity {

    private static final String LOGTAG = "ValidateOffer";

    SupportMapFragment mapFragment;
    private TextView tvAddress, tvOfferTitle, tvPriceRangeId, tvDiscount, tvValidateAfter, tvValidateWithin;
    private NetworkImageView thumbnail;
    private ImageLoader mImageLoader;
    private Button btnRedeem;
    private Resources res;
    private SharedPreferences sharedpref;
    private SimpleDateFormat fromDateFormat;
    private SimpleDateFormat toDateFormat;
    private Toolbar toolbar;
    Typeface myFont;



    int valCalc;
    String offerId, user_id, perc_sym, cur_sym, days, save, off, disc;
    Double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validate_offer);

        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        tvAddress = (TextView) findViewById(R.id.address);
        tvOfferTitle = (TextView) findViewById(R.id.offer_title);
        //tvWhatYouGet = (TextView) findViewById(R.id.what_you_get);
        tvPriceRangeId = (TextView) findViewById(R.id.price_range_id);
        //tvPayValue = (TextView) findViewById(R.id.pay_value);
        tvDiscount = (TextView) findViewById(R.id.discount);
        //tvRetailValue = (TextView) findViewById(R.id.retail_value);
        tvValidateWithin = (TextView) findViewById(R.id.validate_within);
        tvValidateAfter = (TextView) findViewById(R.id.validate_after);
        //btnBank = (Button) findViewById(R.id.btn_bank_offer);
        //btnPass = (Button) findViewById(R.id.btn_pass_offer);
        btnRedeem = (Button) findViewById(R.id.btn_redeem_offer);


        tvAddress.setTypeface(myFont);
        tvOfferTitle.setTypeface(myFont);
        tvPriceRangeId.setTypeface(myFont);
        tvDiscount.setTypeface(myFont);
        tvValidateWithin.setTypeface(myFont);
        tvValidateAfter.setTypeface(myFont);



        thumbnail = (NetworkImageView) findViewById(R.id.thumbnail);

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


            btnRedeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d(LOGTAG, "Starting validation...");
                    Intent intent = new Intent(ValidateOfferActivity.this, CloudReco.class);
                    intent.putExtra(getString(R.string.ext_activity), "Validation");
                    intent.putExtra(getString(R.string.ext_user_id), user_id);
                    intent.putExtra(getString(R.string.ext_offer_id), offerId);
                    startActivity(intent);

                    //intent.putExtra("ACTIVITY_TO_LAUNCH", "app.CloudRecognition.CloudReco");
                    //intent.putExtra("ABOUT_TEXT", "CloudReco/CR_about.html");
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
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();


                    JSONObject reader = new JSONObject(resp);

                    Log.d(LOGTAG, "Message Data: " + reader.getString("data"));
                    if (!reader.getString("data").equals("")) {

                        //Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                        //JSONObject jsonObject = new JSONObject(reader.getString("data"));

                        JSONArray offerArray = new JSONArray(reader.getString("data"));

                        //Log.d(LOGTAG, "Length: " + offerArray.length());

                        if(offerArray.length() == 1) {

                            JSONObject jsonObject = offerArray.getJSONObject(0);





                            if(jsonObject.optString("offer_description").toString() != null) {
                                tvOfferTitle.setText(jsonObject.optString("offer_description").toString());
                                Log.d(LOGTAG, "offer_description: "+jsonObject.optString("offer_description").toString());
                            }





                            if(jsonObject.getString("discount") != null && jsonObject.getString("discount").toString() != "") {


                               String discVal = jsonObject.getString("discount");

                               StringBuilder sb = new StringBuilder(14);



                                switch(valCalc)
                                {
                                    case 1 :
                                        sb.append(cur_sym).append(discVal).append(" ").append(off);
                                        break;
                                    case 2 :
                                        sb.append(discVal).append(perc_sym).append(" ").append(off);
                                        break;
                                    case 3 :
                                        sb.append(cur_sym).append(discVal).append(" ").append(disc);
                                        break;
                                    case 4 :
                                        sb.append(discVal).append(perc_sym).append(" ").append(disc);
                                        break;
                                    case 5 :
                                        sb.append(save).append(" ").append(cur_sym).append(discVal);
                                        break;
                                    case 6 :
                                        sb.append(save).append(" ").append(discVal).append(perc_sym);
                                        break;
                                    default :
                                        sb.append(cur_sym).append(discVal).append(" ").append(off);
                                }

                                tvDiscount.setText(sb);


                                //Log.d(LOGTAG, "discount: "+jsonObject.getString("discount").toString());
                            }

                            if(!jsonObject.isNull("myoffer_details") && jsonObject.getString("myoffer_details").trim() != "") {



                                JSONArray jsonMyOfferArray = new JSONArray(jsonObject.getString("myoffer_details"));


                                Log.d(LOGTAG, "My Offer Length: "+jsonMyOfferArray.length());

                                if(jsonMyOfferArray.length() == 1) {

                                    JSONObject jsonMyOfferObject = jsonMyOfferArray.getJSONObject(0);

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

                                            //calendar.setTime(fromDateFormat.parse(validate_after));

                                            Date date2 = fromDateFormat.parse(validate_after);

                                            tvValidateAfter.setText(toDateFormat.format(date2));

                                        }


                                    } catch (ParseException ex) {

                                        Log.d(LOGTAG, "Exception in parsing date: "+ex.toString());


                                    }


                                }



                            }




                            if(!jsonObject.isNull("offer_large_image_path") && jsonObject.getString("offer_large_image_path").toString() != "") {


                                String imageUrl = jsonObject.getString("offer_large_image_path");

                                imageUrl = UrlEndpoints.serverBaseUrl  + imageUrl;

                                Log.d(LOGTAG, "offer_image_path: "+imageUrl);


                                mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();

                                mImageLoader.get(imageUrl, ImageLoader.getImageListener(thumbnail,
                                        R.drawable.icon_watermark, android.R.drawable
                                                .ic_dialog_alert));
                                thumbnail.setImageUrl(imageUrl, mImageLoader);


                            }

                            JSONArray jsonCompanyArray = new JSONArray(jsonObject.getString("company_detail"));

                            String address = "";

                            Log.d(LOGTAG, "Company Length: "+jsonCompanyArray.length());

                            if(jsonCompanyArray.length() == 1) {

                                JSONObject jsonCompanyObject = jsonCompanyArray.getJSONObject(0);


                                if (!jsonCompanyObject.isNull("address")  && jsonCompanyObject.getString("address").toString() != "") {
                                    Log.d(LOGTAG, "address: " + jsonCompanyObject.getString("address").toString());
                                    address = jsonCompanyObject.getString("address");
                                    tvAddress.setText(address);


                                }

                            }




                            JSONObject json_partner_settings = new JSONObject(jsonObject.getString("partner_settings"));


                            if(!json_partner_settings.isNull("price_range_id") && json_partner_settings.getString("price_range_id").toString() != "") {
                                tvPriceRangeId.setText(json_partner_settings.getString("price_range_id"));
                                Log.d(LOGTAG, "price_range_id: "+json_partner_settings.getString("price_range_id").toString());

                            }



                        }




                    } // End of if




                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }



}
