package com.vuforia.samples.VuforiaSamples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vuforia.samples.VuforiaSamples.async.DownloadImageTask;
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

public class BrandMainActivity extends Activity {

    private static final String LOGTAG = "BrandMain";

    LinearLayout video_thumb_layout;

    private TextView tvBrandName, hwTextView;
    private ImageView imBrandLogo;
    private Button btnShopOffers;
    JSONArray videosArray;
    String redeemerId = "", uniqueTargetId = "";

    ArrayList videoHtmls;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_main);

        btnShopOffers = (Button) findViewById(R.id.shop_offers);
        tvBrandName = (TextView) findViewById(R.id.brand_name);
        imBrandLogo = (ImageView) findViewById(R.id.brand_logo_image);
        hwTextView = (TextView) findViewById(R.id.tvUniqueTargetId);

        context = this;

        Bundle extras = getIntent().getExtras();

        //if (extras != null) {
        //uniqueTargetId = extras.getString("unique_target_id");

        String uniqueTargetId = "cb9ca7865acf4b8d88771e7f6ed9a8e3";
        Log.d(LOGTAG, "New target Id: " + uniqueTargetId);
        new ValidateLogoAsyncTask().execute(uniqueTargetId);


        //}


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private class ValidateLogoAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", frameVideo = "", basePath = "";
        int vidwidth = 0;
        int vidheight = 0;
        WebView displayYoutubeVideo;

        public ValidateLogoAsyncTask() {

            url = Webservicelinks.validateLogoURL;
            basePath = Webservicelinks.basePathURL;
        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            String target_id = params[0];

            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                JSONObject registerJson = new JSONObject();
                registerJson.put("webservice_name", "check_target");
                registerJson.put("target_id", target_id);

                Log.d("RMD", "Target Id: " + target_id);
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

                    JSONObject reader = new JSONObject(resp);
                    if (reader.getString("messageCode").equals("R01001")) {

                        Log.d(LOGTAG, "Message Code: " + reader.getString("messageCode"));
                        Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                        /*Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;*/


                        //JSONObject json2 = reader.getJSONObject(reader.getString("data"));

                        JSONObject json2 = new JSONObject(reader.getString("data"));

                        String companyName = (String) json2.get("companyName");
                        companyName = companyName.trim();

                        String logoImage = (String) json2.get("logoImage");


                        redeemerId = (String) json2.get("reedemer_id");
                        Log.d(LOGTAG, "Redeemer Id: " + redeemerId);

                        /*SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.spf_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.spf_redeemer_id), redeemerId);
                        editor.commit();*/


                        Log.d(LOGTAG, "Company Name: " + companyName);
                        Log.d(LOGTAG, "Logo Image: " + logoImage);

                        if (companyName != null) {

                            tvBrandName.setText(companyName);
                        }


                        if (logoImage != null && logoImage != "") {
                            String logoImagePath = basePath + logoImage;

                            //imBrandLogo.setImageBitmap(BitmapFactory.decodeFile(logoImagePath));
                            Log.d(LOGTAG, "Logo Image: " + logoImagePath);
                            new DownloadImageTask(imBrandLogo).execute(logoImagePath);

                        }

                        videosArray = json2.optJSONArray("videoList");

                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        int hheight = displaymetrics.heightPixels;
                        int wwidth = displaymetrics.widthPixels;

                        displayYoutubeVideo = (WebView) findViewById(R.id.videoFragment);
                        displayYoutubeVideo.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                return false;
                            }
                        });
                        WebSettings webSettings = displayYoutubeVideo.getSettings();
                        webSettings.setJavaScriptEnabled(true);


                        displayYoutubeVideo.getSettings().setLoadWithOverviewMode(true);
                        displayYoutubeVideo.getSettings().setUseWideViewPort(true);

                        videoHtmls = new ArrayList();


                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int i = 0; i < videosArray.length(); i++) {
                            JSONObject jsonObject = videosArray.getJSONObject(i);


                            String videoId = jsonObject.optString("video_id").toString();
                            String videoThumb = jsonObject.optString("video_thumb").toString();
                            String videoUrl = jsonObject.optString("video_url").toString();
                            String provider = jsonObject.optString("provider").toString();


                            Log.d(LOGTAG, "Video Id: " + videoId);
                            Log.d(LOGTAG, "Video Thumb: " + videoThumb);
                            Log.d(LOGTAG, "Video URL: " + videoUrl);
                            Log.d(LOGTAG, "Provider: " + provider);
                            Log.d(LOGTAG, "Video Width & Height: " + wwidth + "x" + hheight);


                            vidwidth = wwidth * 3 / 2;
                            vidheight = hheight / 2;

                            Log.d(LOGTAG, "Video Width & Height: " + vidwidth + "x" + vidheight);


                            // Youtube
                            if (provider.equalsIgnoreCase("1")) {
                                frameVideo = "<html><body><iframe width=\"" + vidwidth + "\" height=\"" + vidheight + "\" src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1\"  frameborder=\"0\" allowfullscreen></iframe></body></html>";
                                //frameVideo = "<iframe src=\"https://player.vimeo.com/video/52861634?autoplay=1&title=0&byline=0&portrait=0\" width=\"640\" height=\"360\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";
                            } else if (provider.equalsIgnoreCase("2")) {
                                //frameVideo = "<html><body><iframe src\"https://player.vimeo.com/video/"+videoId+"?autoplay=1\" width=\""+vidwidth+"\"   autoplay=\"autoplay\" height=\""+vidheight+"\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></body></html>";
                                frameVideo = "<html><<body><iframe src=\"https://player.vimeo.com/video/" + videoId + "?autoplay=1&title=0&byline=0&portrait=0\" width=\"" + vidwidth + "\" height=\"" + vidheight + "\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></body></html>";

                            }

                            Log.d(LOGTAG, "Loaded Video is: " + frameVideo);


                            videoHtmls.add(frameVideo);


                            if (videoThumb != "") {

                                if (i == 0) {


                                    displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8");

                                }


                                video_thumb_layout = (LinearLayout) findViewById(R.id.video_thumb_layout);


                                ImageView thImage = new ImageView(getApplicationContext());
                                thImage.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
                                //thImage.setMaxHeight(40);
                                //thImage.setMaxWidth(40);

                                thImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                thImage.setPadding(6, 0, 6, 0);
                                thImage.setId(i);


                                new DownloadImageTask(thImage).execute(videoThumb);


                                // Adds the view to the layout
                                video_thumb_layout.addView(thImage);

                                thImage.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        int p = arg0.getId();

                                        String vidHtml = videoHtmls.get(p).toString();
                                        // Clicked video thumbnail

                                        Log.d(LOGTAG, "Video is: " + vidHtml);

                                        Toast.makeText(getApplicationContext(), "Clicked " + p + "th Image",
                                                Toast.LENGTH_SHORT).show();

                                        displayYoutubeVideo.loadData(vidHtml, "text/html", "utf-8");

                                    }
                                });


                            }


                        } // End of for loop for videos
                    } // End of if

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                btnShopOffers.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent dispOffers = new Intent(getApplicationContext(), RecyclerViewExample.class);
                        dispOffers.putExtra("redeemer_id", redeemerId);
                        startActivity(dispOffers);
                        finish();

                    }
                });


            }
        }


    }


}
