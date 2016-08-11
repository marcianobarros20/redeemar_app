package com.tier5.redeemar.RedeemarConsumerApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrandDetailsAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadImageTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.MyOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.BrandLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.BrandVideo;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
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
import java.net.URLEncoder;
import java.util.ArrayList;


public class BrandMainActivity extends YouTubeBaseActivity implements BrandLoadedListener, YouTubePlayer.OnInitializedListener {

    private static final String LOGTAG = "BrandMain";

    LinearLayout video_thumb_layout;

    private TextView tvBrandName, hwTextView;
    private ImageView imBrandLogo;
    private Button btnShopOffers;
    String redeemarId = "", uniqueTargetId = "", lastActivity="", videoId = "", basePath = "";
    Context context;


    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private YouTubeThumbnailView youTubeThumbnailView;
    private YouTubeThumbnailLoader youTubeThumbnailLoader;
    BrandVideo brandVideo;

    public static final String API_KEY = "AIzaSyA4cndO4t85r0NZ-Ux9N8MzBJx06k4iNPA";
    public static final String VIDEO_ID = "o7VVHhK9zf0";

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_main);


        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);
        youTubePlayerView.initialize(API_KEY, this);

        btnShopOffers = (Button) findViewById(R.id.shop_offers);
        tvBrandName = (TextView) findViewById(R.id.brand_name);
        imBrandLogo = (ImageView) findViewById(R.id.brand_logo_image);
        context = this;

        basePath = UrlEndpoints.basePathURL;

        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            uniqueTargetId = extras.getString("unique_target_id");
            lastActivity = extras.getString(getString(R.string.ext_activity));
            Log.d(LOGTAG, "Brand Main Unique Target Id: " + uniqueTargetId);
            Log.d(LOGTAG, "Brand Main Last Activity: " + lastActivity);
            //new ValidateLogoAsyncTask().execute(uniqueTargetId);

        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult result) {

    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {


        youTubePlayer = player;


        new BrandDetailsAsyncTask(this).execute(uniqueTargetId);

        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }

    @Override
    public void onBrandLoaded(ArrayList<Object> brandInfo) {


        Log.d(LOGTAG, "Items inside Brand Info: "+brandInfo.size());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int hheight = displaymetrics.heightPixels;
        int wwidth = displaymetrics.widthPixels;


        // The first element contains brand information
        if(brandInfo.size() >= 1) {



            int p = 0;
            final  ArrayList videos = new ArrayList();


            for(int i = 0; i < brandInfo.size(); i++) {


                // Because the first index contains brand information, show brand information

                if (i == 0) {

                    User brand = (User) brandInfo.get(i);

                    SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.spf_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.spf_redeemer_id), brand.getId());
                    editor.commit();

                    redeemarId = brand.getId();


                    Log.d(LOGTAG, "My Brand name: "+brand.getCompanyName());

                    tvBrandName.setText(brand.getCompanyName());

                    //String defaultLogoImage =  brand.get

                    String logoImagePath = basePath +  brand.getLogoName();

                    Log.d(LOGTAG, "Logo path new: "+logoImagePath);

                    new DownloadImageTask(imBrandLogo).execute(logoImagePath);



                    if(youTubePlayer != null) {
                        youTubePlayer.cueVideo(Constants.defaultYoutubeVideoId);

                    }


                } else {

                    brandVideo = (BrandVideo) brandInfo.get(i);



                    String videoThumb = brandVideo.getVideothumb();



                    if (brandVideo.getVideoProvider().equals("1")) {

                        if (p == 0) {



                            videoId = brandVideo.getVideoId();

                            Log.d(LOGTAG, "Inside video cue: "+videoId);
                            if(!videoId.equals("")) {

                                //youTubePlayer.cueVideo("y2Ky3Wo37AY");
                                if(youTubePlayer != null) {
                                    youTubePlayer.cueVideo(videoId);

                                }
                            }



                        }



                        BrandVideo video  = new BrandVideo();

                        video.setVideoId(brandVideo.getVideoId());
                        video.setVideoProvider(brandVideo.getVideoProvider());
                        video.setVideothumb(brandVideo.getVideothumb());
                        video.setVideoUrl(brandVideo.getVideoUrl());


                        videos.add(video);


                        video_thumb_layout = (LinearLayout) findViewById(R.id.video_thumb_layout);


                        ImageView thImage = new ImageView(getApplicationContext());
                        thImage.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
                        //thImage.setMaxHeight(40);
                        //thImage.setMaxWidth(40);

                        thImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        thImage.setPadding(6, 0, 6, 0);

                        thImage.setId(p);



                        new DownloadImageTask(thImage).execute(videoThumb);


                        // Adds the view to the layout
                        video_thumb_layout.addView(thImage);

                        thImage.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                int j = arg0.getId();
                                Log.d(LOGTAG, "Youtube Video Clicked Id: " + j);

                                BrandVideo myVid = (BrandVideo) videos.get(j);

                                if (myVid.getVideoProvider().equalsIgnoreCase("1")) {

                                    Log.d(LOGTAG, "Youtube Video URL: " + myVid.getVideoUrl());

                                    youTubePlayer.cueVideo(myVid.getVideoId());
                                    youTubePlayer.play();


                                }


                            }
                        });


                        p++;


                    }


                }

            }


        }
        else {
            Toast.makeText(getApplicationContext(), "No brand information found", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(BrandMainActivity.this, BrowseOffersActivity.class);
            startActivity(intent);
            finish();
        }

        btnShopOffers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                editor.putString(getString(R.string.spf_redir_action), "BrandOffers"); // Storing Last Activity
                editor.putString(getString(R.string.spf_redeemer_id), redeemarId); // Storing Redeemar Id
                editor.commit(); // commit changes


                Bundle args = new Bundle();

                Intent intent = new Intent(getApplicationContext(), BrowseOffersActivity.class);
                //args.putString(getString(R.string.ext_redir_to), "BrandOffers");
                //args.putString(getString(R.string.ext_redeemar_id), redeemarId);
                intent.putExtra(getString(R.string.ext_redir_to), "BrandOffers");
                intent.putExtra(getString(R.string.ext_redeemar_id), redeemarId);

                Log.d(LOGTAG, "Brand Main Redeemar Id: "+redeemarId);
                //intent.putExtra(getString(R.string.ext_redeemar_id), redeemerId);
                startActivity(intent);
                finish();

            }
        });

    }



    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(BrandMainActivity.this, BrowseOffersActivity.class);
        startActivity(intent);
        finish();

    }


}
