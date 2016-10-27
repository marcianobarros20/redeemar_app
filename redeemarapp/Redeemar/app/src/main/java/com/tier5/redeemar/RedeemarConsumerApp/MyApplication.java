package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;

import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Cache;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import com.estimote.sdk.Utils;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils2.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;



public class MyApplication extends Application implements AsyncResponse.Response,AsyncResponse2.Response2{

    private static final String LOGTAG = "MyApplication";

    private BeaconManager beaconManager;
    private Region region;

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private String scanId;

    private static MyApplication sInstance;

    public boolean callingTheApi = false;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    //server connectivity
    HashMap<String,String> data = new HashMap<>();
    CallApi registerUser = new CallApi("POST");
    String route = "admin/public/index.php/bridge/findbeacon";

    HashMap<String,String> data2 = new HashMap<>();
    CallApi2 registerUser2 = new CallApi2("POST");
    String route2 = "admin/public/index.php/bridge/findsticker";



    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("LOG", "Inside MyApplication onCreate()");

        registerUser.delegate = this;
        registerUser2.delegate = this;

        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        LruCache picassoCache = new LruCache(getApplicationContext());
        builder.memoryCache(picassoCache);
        Picasso.setSingletonInstance(builder.build());

        picassoCache.clear();



        res = getResources();
        sharedpref = getApplicationContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        beaconManager = new BeaconManager(getApplicationContext());
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        /*beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
                scanId = beaconManager.startNearableDiscovery();
            }
        });


        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                if(list.size()>0)
                {
                    Nearable nearestNearable = list.get(0);

                    Utils.Proximity nearestNearableDistance = Utils.computeProximity(nearestNearable);

                    //Log.i(LOGTAG,"nearest nearable is moving : "+nearestNearableDistance);
                    if(nearestNearableDistance.toString().equals("IMMEDIATE"))
                    {
                        try
                        {
                            if(!BeaconStatics.beaconTriggred)
                            {
                                BeaconStatics.beaconTriggred = true;
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("webservice_name","findsticker");
                                jsonObject2.put("identifier",nearestNearable.identifier);

                                data2.put("data",jsonObject2.toString());

                                Log.i(LOGTAG,"total data(NEARABLE) is: "+data2.toString());
                                registerUser2.register(data2,route2);
                            }
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
            }
        });


        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    Double nearestBeaconDistance = Utils.computeAccuracy(nearestBeacon);
                    if(nearestBeaconDistance<0.5)
                    {
                        //Log.i(LOGTAG,"beacons distance is "+ nearestBeaconDistance);
                        //List<String> places = placesNearBeacon(nearestBeacon);
                        // TODO: update the UI here
                        //Log.d(LOGTAG, "Nearest beacon: " + nearestBeacon);
                        try
                        {
                            if(!BeaconStatics.beaconTriggred)
                            {
                                BeaconStatics.beaconTriggred = true;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("webservice_name","findbeacon");
                                jsonObject.put("uuid",nearestBeacon.getProximityUUID().toString().toUpperCase());
                                jsonObject.put("major",nearestBeacon.getMajor());
                                jsonObject.put("minor",nearestBeacon.getMinor());

                                //demo beacon


                                data.put("data",jsonObject.toString());

                                Log.i(LOGTAG,"total data(BEACON) is: "+data.toString());

                                registerUser.register(data,route);
                            }
                            else
                            {
                                Log.i(LOGTAG,"One api is being called cannot call another");
                            }

                        }
                        catch (Exception e)
                        {
                            Log.i(LOGTAG,"error in creating json object "+e.toString());
                        }

                    }
                    else
                    {
                        Log.i(LOGTAG,"no beacon in range ");
                    }
                }
            }
        });*/

    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, BrowseOffersActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    public static String getAppEnvironment() {

        String word = "beta";
        String severBase = UrlEndpoints.serverBaseUrl;

        if(severBase.contains("www"))
            return "www";
        else if(severBase.contains("beta"))
            return "beta";
        else
            return "dev";

    }

    @Override
    public void processFinish(String output) {
        Log.i(LOGTAG,"Response is : "+output);
        //callingTheApi = false;
        beaconResponseHandler(output);

    }

    @Override
    public void processFinish2(String output) {
        Log.i(LOGTAG,"OUTPUT of NEARABLE : "+output);
        beaconResponseHandler(output);
    }

    public void beaconResponseHandler(String output)
    {
        if (output != null) {
            BeaconStatics.callingFromBeaconPage = true;


            try {
                //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();

                JSONObject reader = new JSONObject(output);
                if (reader.getString("messageCode").equals("R01001")) {

                    Log.d(LOGTAG, "Message Code: " + reader.getString("messageCode"));
                    Log.d(LOGTAG, "Message Data: " + reader.getString("data"));


                    JSONObject json2 = new JSONObject(reader.getString("data"));

                    //Log.i(LOGTAG,"Json2 is : "+json2.isNull("redeemar_id"));

                    String action_id = "";
                    String redeemar_id = "";
                    ArrayList<Offer> offerList;

                    if(!json2.isNull("action_id") && !json2.isNull("redeemar_id")) {
                        //Log.i(LOGTAG,"IN IF");

                        action_id = String.valueOf(json2.get("action_id"));
                        redeemar_id = String.valueOf(json2.get("redeemar_id"));


                        Log.d(LOGTAG, "Action Id: "+action_id);
                        Log.d(LOGTAG, "Redeemar Id: "+redeemar_id);


                        // Redirect the user to appropriate activity based on the action id

                        // If 2 = Show list of offers for a particular campaign
                        if (action_id.equals("2")) {

                            if(!json2.isNull("campaign_id")) {

                                String campaign_id = (String) json2.get("campaign_id");
                                Log.d(LOGTAG, "Inside action id 2: "+campaign_id);

                                editor.putString(getString(R.string.spf_redir_action), "CampaignOffers"); // Storing Last Activity
                                editor.putString(getString(R.string.spf_redeemer_id), campaign_id); // Storing Redeemar Id
                                editor.commit(); // commit changes

                                Intent sIntent = new Intent(getApplicationContext(), BrowseOffersActivity.class);
                                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sIntent.putExtra(getString(R.string.ext_redir_to), "CampaignOffers");
                                sIntent.putExtra(getString(R.string.ext_redeemar_id), redeemar_id);
                                sIntent.putExtra(getString(R.string.ext_campaign_id), campaign_id);
                                startActivity(sIntent);

                            }
                            else
                            {
                                Log.i(LOGTAG,"campaign id is null");
                            }

                        }

// If 3 = Goto a particular offer
                        else if (action_id.equals("3")) {

                            if(!json2.isNull("offer_id")) {

                                String offerId = json2.get("offer_id").toString();

                                Log.d(LOGTAG, "Inside action id 3: "+offerId);

                                Intent sIntent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
//sIntent.putExtra(getString(R.string.ext_redeemar_id), "R01002");
                                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sIntent.putExtra(getString(R.string.ext_offer_id), offerId);
                                startActivity(sIntent);
//finish();

                            }

                        }


// If 4 = Validate a particular offer
                        else if (action_id.equals("4")) {

                            if(!json2.isNull("action_id") && !json2.isNull("redeemar_id")) {
                                Log.d(LOGTAG, "Inside action id 4");
//showInitializationErrorMessage(getString(R.string.error_validation_wrong_place));

/*Intent intent = new Intent(getApplicationContext(), DisplaySuccessActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
intent.putExtra(getString(R.string.ext_scan_err), "R02001");
startActivity(intent);*/
//finish();

                                Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getString(R.string.ext_scan_err), "R02005");
                                startActivity(intent);
//finish();

                            }
                            else {

                                Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getString(R.string.ext_scan_err), "R02005");
                                startActivity(intent);
//finish();

                            }

                        }


// If 5 = Display a CGI Animation on top of the scanned image
                        else if (action_id.equals("5")) {

                            if(!json2.isNull("action_id") && !json2.isNull("redeemar_id")) {
                                Log.d(LOGTAG, "Inside action id 5");



//Toast.makeText(getApplicationContext(), "This will display a CGI animation.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getString(R.string.ext_scan_err), "R02010");
                                startActivity(intent);
//finish();


                            }

                        }

// If 1 or default case = Go to brand dashboard
                        else {


                            String r_id = json2.get("redeemar_id").toString();

                            Log.d(LOGTAG, "Inside action id 1 or default");

                            Intent sIntent = new Intent(getApplicationContext(), BrandMainActivity.class);
                            sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            sIntent.putExtra("unique_target_id", "");
                            sIntent.putExtra("redeemar_id", r_id);
                            startActivity(sIntent);

                        }

                    }
                    else
                    {
                        Log.i(LOGTAG,"IN ELSE");
                    }


                } // End of if
                else if (reader.getString("messageCode").equals("R01002")) {

                    Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                    errIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    errIntent.putExtra(getString(R.string.ext_scan_err), "R01002");
                    startActivity(errIntent);

                    //tvBrandName.setText(getString(R.string.brand_not_found));
                    // tvBrandName.setTextSize(12);

                }

                else if (reader.getString("messageCode").equals("R01003")) {

                    Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                    errIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    errIntent.putExtra(getString(R.string.ext_scan_err), "R01003");
                    startActivity(errIntent);

//tvBrandName.setText(getString(R.string.brand_not_found));
//tvBrandName.setTextSize(12);
                }

            } catch (JSONException e) {
                Log.i(LOGTAG,"error in parsing respopnse: "+e.toString());
                e.printStackTrace();
//Utils.redirectToError(getApplicationContext());

                Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                errIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(errIntent);
            }


        }
    }

}
