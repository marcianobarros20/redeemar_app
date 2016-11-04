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
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.BrandLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.BrandVideo;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Keys;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import java.util.ArrayList;

import static android.provider.MediaStore.Video.Thumbnails.VIDEO_ID;


public class BrandMainActivity extends YouTubeBaseActivity implements BrandLoadedListener, YouTubePlayer.OnInitializedListener {

    private static final String LOGTAG = "BrandMain";

    LinearLayout video_thumb_layout;

    private TextView tvBrandName, hwTextView;
    private ImageView imBrandLogo;
    private Button btnShopOffers, btnScan;
    String redeemarId = "", brandName = "", uniqueTargetId = "", lastActivity="", videoId = "", basePath = "";
    Context context;

    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;
    private YouTubeThumbnailView youTubeThumbnailView;
    private YouTubeThumbnailLoader youTubeThumbnailLoader;
    BrandVideo brandVideo;

    //public static final String API_KEY = "AIzaSyA4cndO4t85r0NZ-Ux9N8MzBJx06k4iNPA";
    //public static final String VIDEO_ID = "o7VVHhK9zf0";

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;

    private static final int RQS_ErrorDialog = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_main);


        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);
        youTubePlayerView.initialize(Constants.youtubeAPIKey, this);

        btnShopOffers = (Button) findViewById(R.id.shop_offers);
        btnScan = (Button)  findViewById(R.id.btnScan);
        btnScan.setVisibility(View.GONE);

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
            redeemarId = extras.getString("redeemar_id");

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
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {

        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this, RQS_ErrorDialog).show();
        } else {
            Toast.makeText(this,
                    "YouTubePlayer.onInitializationFailure(): " + result.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {


        youTubePlayer = player;

        Log.i("MyAppp",uniqueTargetId);


        new BrandDetailsAsyncTask(this).execute(uniqueTargetId, redeemarId);

        if (!wasRestored) {
            player.cueVideo(Constants.defaultYoutubeVideoId);
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
                    brandName = brand.getCompanyName();

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
                                    youTubePlayer.setFullscreen(true);


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
                editor.putString(getString(R.string.spf_brand_name), brandName); // Storing Brand Name
                //editor.putString(getString(R.string.spf_redir_action), "CategoryOffers"); // Storing Redirect Action
                //editor.putString(getString(R.string.spf_category_id), String.valueOf(String.valueOf(itemId))); // Storing Redirect Action
                //editor.putString(getString(R.string.spf_category_name), itemName); // Storing Redirect Action
                editor.putString(getString(R.string.spf_search_keyword), ""); // Storing Redirect Action
                editor.commit(); // commit changes


                Bundle args = new Bundle();

                Intent intent = new Intent(getApplicationContext(), BrowseOffersActivity.class);
                //args.putString(getString(R.string.ext_redir_to), "BrandOffers");
                //args.putString(getString(R.string.ext_redeemar_id), redeemarId);
                intent.putExtra(getString(R.string.ext_redir_to), "BrandOffers");
                intent.putExtra(getString(R.string.ext_redeemar_id), redeemarId);
                intent.putExtra(res.getString(R.string.ext_more_offers), "1");
                Keys.moreOffers=1;

                Log.d(LOGTAG, "Brand Main Redeemar Id: "+redeemarId);
                //intent.putExtra(getString(R.string.ext_redeemar_id), redeemerId);
                startActivity(intent);
                finish();

            }
        });



        btnScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent sIntent = new Intent(getApplicationContext(), CloudReco.class);
                startActivity(sIntent);
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
