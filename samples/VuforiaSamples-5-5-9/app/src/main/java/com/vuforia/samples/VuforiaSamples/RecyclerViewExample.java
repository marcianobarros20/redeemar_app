package com.vuforia.samples.VuforiaSamples;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.vuforia.samples.VuforiaSamples.models.Offer;
import com.vuforia.samples.VuforiaSamples.utils.Webservicelinks;

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
import java.net.URLEncoder;
import java.util.ArrayList;


public class RecyclerViewExample extends AppCompatActivity {

    /**
     * RecyclerView: The new recycler view replaces the list view. Its more modular and therefore we
     * must implement some of the functionality ourselves and attach it to our recyclerview.
     * <p/>
     * 1) Position items on the screen: This is done with LayoutManagers
     * 2) Animate & Decorate views: This is done with ItemAnimators & ItemDecorators
     * 3) Handle any touch events apart from scrolling: This is now done in our adapter's ViewHolder
     */

    private static final String LOGTAG = "BrandMain";

    private ArrayList<Student> mDataSet;

    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private String redeemerId = "";
    private JSONArray offersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swiprecyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // Layout Managers:
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Item Decorator:
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        // mRecyclerView.setItemAnimator(new FadeInLeftAnimator());


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String uniqueTargetId = extras.getString("redeemer_id");

            redeemerId = "68";
            Log.d(LOGTAG, "New redeemer Id: " + redeemerId);


        }


        mDataSet = new ArrayList<Student>();
        Log.d("RecyclerView", "First One");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Offer List");


        }

        loadData();

        if (mDataSet.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        Log.d("RecyclerView", "Second One");


        // Creating Adapter object
        SwipeRecyclerViewAdapter mAdapter = new SwipeRecyclerViewAdapter(this, mDataSet);


        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((SwipeRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

        mRecyclerView.setAdapter(mAdapter);

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


    // load initial data
    public void loadData() {


        new GetOfferListAsyncTask().execute(redeemerId);

        /*for (int i = 0; i <= 20; i++) {
            mDataSet.add(new Student("Student " + i, "androidstudent" + i + "@gmail.com"));
            Log.d("RecyclerView", "Student " + i);

        }*/


    }




    private class GetOfferListAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", frameVideo = "", basePath = "";
        private ArrayList<Offer> mDataSet;

        public GetOfferListAsyncTask() {

            url = Webservicelinks.getOfferListURL;
            basePath = Webservicelinks.basePathURL;
        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            String reedemer_id = params[0];

            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);




                JSONObject registerJson = new JSONObject();
                registerJson.put("webservice_name", "showoffers");
                registerJson.put("reedemer_id", reedemer_id);

                Log.d("RMD", "Redeemer Id: " + reedemer_id);
                Log.d("RMD", "JSON: " + registerJson.toString());

                String postData = URLEncoder.encode("data", "UTF-8") + "=" + registerJson.toString();

                OutputStream os = conn.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(postData);
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
            Log.d(LOGTAG, "On Post Execute: " + resp);

            if (resp != null) {


                try {
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();

                    resp = "{\"messageCode\":\"R01002\",\"data\":[{\"id\":\"3\",\"campaign_id\":\"25\",\"inventory_id\":\"21\",\"cat_id\":\"5\",\"subcat_id\":\"0\",\"offer_description\":\"Lorem Iosum\",\"max_redeemar\":\"20\",\"price\":\"30\",\"pay\":\"1\",\"start_date\":\"2016-05-23 11:36:38\",\"end_date\":\"2016-05-31 20:50:50\",\"what_you_get\":\"Lorem Iosum\",\"more_information\":\"Lorem Iosum\",\"pay_value\":\"100\",\"retails_value\":\"90\",\"include_product_value\":\"1\",\"discount\":\"10\",\"value_calculate\":\"1\",\"created_by\":\"68\",\"offer_image\":\"\",\"offer_image_path\":\"\",\"created_at\":\"2016-05-23 19:48:45\",\"updated_at\":\"2016-05-23 16:41:41\",\"offer_detail\":[],\"campaign_details\":{\"id\":\"25\",\"campaign_name\":\"Campaign 1\",\"campaign_image\":\"\",\"start_date\":\"2016-05-12\",\"end_date\":\"2016-05-30\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"2016-05-10 08:19:20\",\"updated_at\":\"2016-05-17 12:54:14\"},\"category_details\":{\"id\":\"5\",\"cat_name\":\"Category 2\"},\"sub_category_details\":null,\"partner_settings\":{\"id\":\"5\",\"setting_val\":\"Price Range\",\"price_range_id\":\"$$$$\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"2016-05-17 13:25:12\"},\"company_detail\":[{\"id\":\"68\",\"company_name\":\" Abc Company\",\"email\":\"abc@a.com\",\"web_address\":\"http:\\/\\/www.abc.com\"}]},{\"id\":\"1\",\"campaign_id\":\"25\",\"inventory_id\":\"28\",\"cat_id\":\"5\",\"subcat_id\":\"0\",\"offer_description\":\"Offer Description\",\"max_redeemar\":\"20\",\"price\":\"13\",\"pay\":\"1\",\"start_date\":\"2016-05-12 00:00:00\",\"end_date\":\"2016-05-30 00:00:00\",\"what_you_get\":\"What you get\",\"more_information\":\"more info\",\"pay_value\":\"100\",\"retails_value\":\"120\",\"include_product_value\":\"1\",\"discount\":\"20\",\"value_calculate\":\"3\",\"created_by\":\"68\",\"offer_image\":\"\",\"offer_image_path\":\"\",\"created_at\":\"2016-05-17 08:19:20\",\"updated_at\":\"2016-05-17 08:19:20\",\"offer_detail\":[{\"id\":\"1\",\"offer_id\":\"1\",\"inventory_id\":\"28\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\",\"inventory_details\":{\"id\":\"28\",\"inventory_name\":\"Inventory 1\",\"inventory_image\":\"1462868439_246626466.jpg\",\"sell_price\":\"100\",\"cost\":\"30\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"2016-05-10 08:20:40\",\"updated_at\":\"2016-05-10 08:20:40\"}}],\"campaign_details\":{\"id\":\"25\",\"campaign_name\":\"Campaign 1\",\"campaign_image\":\"\",\"start_date\":\"2016-05-12\",\"end_date\":\"2016-05-30\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"2016-05-10 08:19:20\",\"updated_at\":\"2016-05-17 12:54:14\"},\"category_details\":{\"id\":\"5\",\"cat_name\":\"Category 2\"},\"sub_category_details\":null,\"partner_settings\":{\"id\":\"5\",\"setting_val\":\"Price Range\",\"price_range_id\":\"$$$$\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"2016-05-17 13:25:12\"},\"company_detail\":[{\"id\":\"68\",\"company_name\":\" Abc Company\",\"email\":\"abc@a.com\",\"web_address\":\"http:\\/\\/www.abc.com\"}]},{\"id\":\"2\",\"campaign_id\":\"25\",\"inventory_id\":\"30\",\"cat_id\":\"5\",\"subcat_id\":\"0\",\"offer_description\":\"Offer Description\",\"max_redeemar\":\"25\",\"price\":\"4\",\"pay\":\"1\",\"start_date\":\"2016-05-12 00:00:00\",\"end_date\":\"2016-05-30 00:00:00\",\"what_you_get\":\"What you get\",\"more_information\":\"more info\",\"pay_value\":\"100\",\"retails_value\":\"120\",\"include_product_value\":\"1\",\"discount\":\"20\",\"value_calculate\":\"3\",\"created_by\":\"68\",\"offer_image\":\"\",\"offer_image_path\":\"\",\"created_at\":\"2016-05-17 08:19:20\",\"updated_at\":\"2016-05-17 08:19:20\",\"offer_detail\":[],\"campaign_details\":{\"id\":\"25\",\"campaign_name\":\"Campaign 1\",\"campaign_image\":\"\",\"start_date\":\"2016-05-12\",\"end_date\":\"2016-05-30\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"2016-05-10 08:19:20\",\"updated_at\":\"2016-05-17 12:54:14\"},\"category_details\":{\"id\":\"5\",\"cat_name\":\"Category 2\"},\"sub_category_details\":null,\"partner_settings\":{\"id\":\"5\",\"setting_val\":\"Price Range\",\"price_range_id\":\"$$$$\",\"status\":\"1\",\"created_by\":\"68\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"2016-05-17 13:25:12\"},\"company_detail\":[{\"id\":\"68\",\"company_name\":\" Abc Company\",\"email\":\"abc@a.com\",\"web_address\":\"http:\\/\\/www.abc.com\"}]}]}";

                    JSONObject reader = new JSONObject(resp);


                    if (reader.getString("messageCode").equals("R02001")) {

                        Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                        /*Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;*/


                        offersArray = reader.optJSONArray(reader.getString("data"));

                        //JSONObject json2 = new JSONObject(reader.getString("data"));




                        //offersArray = json2.optJSONArray("data");







                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int i = 0; i < offersArray.length(); i++) {
                            JSONObject jsonObject = offersArray.getJSONObject(i);



                            Offer offer = new Offer();
                            if(jsonObject.optString("offer_description").toString() != null)
                                offer.setOfferDescription(jsonObject.optString("offer_description").toString());


                            //offer.setWhatYouGet(jsonObject.optString("what_you_get").toString());
                            //offer.setMoreInformation(jsonObject.optString("more_information").toString());
                            //offer.setPrice(Double.parseDouble(jsonObject.optString("price").toString()));

                            if(jsonObject.optString("retail_value").toString() != null)
                                offer.setRetailvalue(Double.parseDouble(jsonObject.optString("retail_value").toString()));

                            if(jsonObject.optString("pay_value").toString() != null)
                                offer.setPayValue(Double.parseDouble(jsonObject.optString("pay_value").toString()));

                            if(jsonObject.optString("discount").toString() != null)
                                offer.setDiscount(Double.parseDouble(jsonObject.optString("discount").toString()));

                            if(jsonObject.optString("value_calculate").toString() != null)
                                offer.setValueCalculate(Integer.parseInt(jsonObject.optString("value_calculate").toString()));

                            if(jsonObject.optString("expires").toString() != null)
                                offer.setExpiredInDays(Integer.parseInt(jsonObject.optString("expires").toString()));

                            //partnerSettingArray = reader.optJSONArray(reader.getString("partner_settings"));
                            JSONObject json_partner_settings = new JSONObject(reader.getString("partner_settings"));


                            if(json_partner_settings.getString("price_range_id").toString() != null) {
                                offer.setPriceRangeId(json_partner_settings.optString("price_range_id").toString());
                            }




                            mDataSet.add(offer);





                        } // End of for loop for videos
                    } // End of if

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }


}
