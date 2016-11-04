package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.google.gson.Gson;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.estimote.sdk.Utils;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.UserBeacon;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils2.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MyApplication extends Application implements AsyncResponse.Response,AsyncResponse2.Response2{

    private static final String LOGTAG = "MyApplication";
    private BeaconManager beaconManager;
    private Region region;
    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private String scanId = "", uuid = "";
    private int major = 0, minor = 0;
    private ArrayList<UserBeacon> userBeacons;
    private Gson gson;

    private static MyApplication sInstance;
    //public boolean callingTheApi = false;


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

        res = getResources();
        sharedpref = getApplicationContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        BeaconStatics.beaconTriggred = false;
        gson = new Gson();

        // Check whether any beacon data exists in the SharedPreference
        // If exists then build the array list
        String json = sharedpref.getString(res.getString(R.string.spf_user_beacons), "");

        String beaconTriggered = sharedpref.getString(res.getString(R.string.spf_beacon_triggered), "0");
        Log.d("LOG", "Beacons Triggered: "+beaconTriggered);


        // 0 = false Stands for beacon has not been triggered
        // 1 = true Stands for beacon has been triggered
        /*if(beaconTriggered.equals("1"))
            BeaconStatics.beaconTriggred = false;
        else
            BeaconStatics.beaconTriggred = true;*/

        userBeacons = new ArrayList<UserBeacon>();

        Log.d("LOG", "User Beacons Object : ");

        /*Type collectionType = new TypeToken<Collection<UserBeacon>>(){}.getType();
        userBeacons = gson.fromJson(json, collectionType);

        if(userBeacons != null && userBeacons.size() >0)
            Log.d("LOG", "No. of user beacons: "+userBeacons.size());*/

        registerUser.delegate = this;
        registerUser2.delegate = this;

        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        LruCache picassoCache = new LruCache(getApplicationContext());
        builder.memoryCache(picassoCache);
        Picasso.setSingletonInstance(builder.build());
        picassoCache.clear();


        beaconManager = new BeaconManager(getApplicationContext());
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
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
                                //BeaconStatics.beaconTriggred = true;
                                //editor.putString(res.getString(R.string.spf_beacon_triggered), "1");
                                //editor.commit();

                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("webservice_name","findsticker");
                                jsonObject2.put("identifier",nearestNearable.identifier);

                                data2.put("data", jsonObject2.toString());

                                Log.i(LOGTAG,"total data(NEARABLE) is: "+data2.toString());
                                registerUser2.register(data2,route2);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.i(LOGTAG, "Exception occured in getting nearables: "+ e.toString());
                        }
                    }
                }
            }
        });


        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {


                String beaconTriggered = sharedpref.getString(res.getString(R.string.spf_beacon_triggered), "0");
                Log.d("LOG", "Beacons Triggered: "+beaconTriggered);


                // 0 = false Stands for beacon has not been triggered
                // 1 = true Stands for beacon has been triggered
                /*if(beaconTriggered.equals("1"))
                    BeaconStatics.beaconTriggred = true;
                else*/
                BeaconStatics.beaconTriggred = false;

                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    Double nearestBeaconDistance = Utils.computeAccuracy(nearestBeacon);
                    if(nearestBeaconDistance < 0.5)
                    {
                        Log.d(LOGTAG,"beacons distance is "+ nearestBeaconDistance);
                        //List<String> places = placesNearBeacon(nearestBeacon);
                        // TODO: update the UI here
                        Log.d(LOGTAG, "Nearest beacon: " + nearestBeacon);
                        try
                        {
                            // Check whether the Beacon has already been triggered in recent time

                            if(userBeacons != null && userBeacons.size() >0) {
                                Log.d(LOGTAG,"Size UserBeacon: "+ userBeacons.size());
                                Iterator it = userBeacons.iterator();

                                while(it.hasNext()) {

                                    UserBeacon usb = (UserBeacon) it.next();
                                    Date dateCheckTimeNow = new Date();
                                    Date lastAccTime = usb.getLastAccessTime();

                                    // Beacon matched, now check the hour difference
                                    if(usb.getUuid().equalsIgnoreCase(uuid) && usb.getMajor() == major && usb.getMinor() == minor) {

                                        // Check hour difference
                                        //int hourDiff = com.tier5.redeemar.RedeemarConsumerApp.utils.Utils.hoursDifference(dateCheckTimeNow, lastAccTime);
                                        //int minDiff = com.tier5.redeemar.RedeemarConsumerApp.utils.Utils.minuteDifference(dateCheckTimeNow, lastAccTime);
                                        int secDiff = com.tier5.redeemar.RedeemarConsumerApp.utils.Utils.secondDifference(dateCheckTimeNow, lastAccTime);

                                        //Log.d(LOGTAG, "Hour Diff: " + hourDiff);
                                        Log.d(LOGTAG, "Seconds Diff: " + secDiff);

                                        // If the hour difference between last transaction and the current one
                                        if(secDiff >= Constants.beaconListenDelaySecs) {
                                            BeaconStatics.beaconTriggred = false;
                                            editor.putString(res.getString(R.string.spf_beacon_triggered), "0");
                                            editor.commit();
                                        }
                                        else
                                            BeaconStatics.beaconTriggred = true;
                                    }
                                    else
                                        BeaconStatics.beaconTriggred = false;
                                }

                            }
                            else
                                Log.d(LOGTAG, "Array List Size is 0");



                            Log.d(LOGTAG, "Beacon Triggered AA: " + BeaconStatics.beaconTriggred);





                            // In case the global is found to be false then start reading
                            if(!BeaconStatics.beaconTriggred)
                            {
                                //BeaconStatics.beaconTriggred = true;
                                //editor.putString(res.getString(R.string.spf_beacon_triggered), "1");
                                //editor.commit();

                                uuid = nearestBeacon.getProximityUUID().toString().toUpperCase();
                                major = nearestBeacon.getMajor();
                                minor = nearestBeacon.getMinor();


                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("webservice_name","findbeacon");
                                jsonObject.put("uuid", uuid.toString().toUpperCase());
                                jsonObject.put("major", major);
                                jsonObject.put("minor", minor);
                                data.put("data", jsonObject.toString());

                                Log.d(LOGTAG, "Beacon matched info: " + uuid +" "+ major +" "+minor);



                                // Set values to UserBeacon
                                UserBeacon userBeacon = new UserBeacon();

                                Log.d(LOGTAG, "A111");

                                // Get the current date time
                                Date dateTimeNow = new Date();

                                userBeacon.setUuid(uuid);                   // Set UUID

                                Log.d(LOGTAG, "A112");

                                userBeacon.setMajor(major);                 // Set Major
                                userBeacon.setMinor(minor);                 // Set Minor

                                Log.d(LOGTAG, "A113");
                                userBeacon.setLastAccessTime(dateTimeNow);  // Set the lastAccessTime
                                Log.d(LOGTAG, "A114");
                                userBeacons.add(userBeacon);                // Add UserBeacon to the array list

                                Log.d(LOGTAG, "Beacon matched info X: " + uuid+" "+major+" "+minor);


                                /*Gson gson = new Gson();
                                List<UserBeacon> textList = new ArrayList<UserBeacon>();
                                textList.addAll(userBeacons);
                                String jsonText = gson.toJson(textList);
                                Log.d(LOGTAG, "User Beacons Gson Text: "+jsonText);
                                editor.putString(getString(R.string.spf_user_beacons), jsonText);
                                editor.commit();*/

                                // Set the array list containing the list of beacons already listed by the current session, to GSon
                                // Then set the Gson to the Shared Preference

                                /*String json = gson.toJson(userBeacons);
                                editor.putString(res.getString(R.string.spf_user_beacons), json);
                                editor.commit();*/

                                Log.i(LOGTAG, "Total data(BEACON) is: "+data.toString());
                                registerUser.register(data,route);
                            }
                            else
                            {
                                Log.i(LOGTAG,"One api is being called cannot call another");
                            }

                        }
                        catch (Exception e)
                        {
                            Log.i(LOGTAG,"Error in creating json object "+e.toString());
                        }

                    }
                    else
                    {
                        Log.i(LOGTAG,"no beacon in range ");
                    }
                }
            }
        });

    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, BrowseOffersActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
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
                Log.d(LOGTAG, "Output: " + output);


                JSONObject reader = new JSONObject(output);

                if (reader.getString("messageCode").equals("R01001")) {

                    Log.d(LOGTAG, "Message Code: " + reader.getString("messageCode"));
                    Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                    JSONObject json2 = new JSONObject(reader.getString("data"));

                    String action_id = "";
                    String redeemar_id = "";
                    ArrayList<Offer> offerList;

                    if(!json2.isNull("action_id") && !json2.isNull("redeemar_id")) {

                        action_id = String.valueOf(json2.get("action_id"));
                        redeemar_id = String.valueOf(json2.get("redeemar_id"));
                        String offerid = String.valueOf(json2.get("redeemar_id"));

                        Log.d(LOGTAG, "Action Id: "+action_id);
                        Log.d(LOGTAG, "Redeemar Id: "+redeemar_id);
                        Log.d(LOGTAG, "Offer Id: "+offerid);


                        // Redirect the user to appropriate activity based on the action id
                        // If 2 = Show list of offers for a particular campaign
                        if (action_id.equals("2")) {

                            if(!json2.isNull("particular_id")) {

                                // Set to Shared Preferences, so next the beacon will be triggered after specified duration
                                editor.putString(res.getString(R.string.spf_beacon_triggered), "1");
                                editor.commit();

                                String campaign_id = json2.get("particular_id").toString();
                                Log.d(LOGTAG, "Inside action id 2: "+campaign_id);

                                editor.putString(getString(R.string.spf_redir_action), "CampaignOffers"); // Storing Last Activity
                                editor.putString(getString(R.string.spf_redeemer_id), campaign_id); // Storing Campaign Id
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

                            if(!json2.isNull("particular_id")) {


                                // Set to Shared Preferences, so next the beacon will be triggered after specified duration
                                editor.putString(res.getString(R.string.spf_beacon_triggered), "1");
                                editor.commit();

                                String offerId = json2.get("particular_id").toString();
                                Log.d(LOGTAG, "Inside action id 3: "+offerId);

                                Intent sIntent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
                                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sIntent.putExtra(getString(R.string.ext_offer_id), offerId);
                                startActivity(sIntent);

                            }
                            else
                            {
                                Log.i(LOGTAG,"offer id is null");
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

                                /*Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getString(R.string.ext_scan_err), "R02005");
                                startActivity(intent);*/
                                //finish();

                            }
                            /*else {

                                Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getString(R.string.ext_scan_err), "R02005");
                                startActivity(intent);
                                //finish();
                            }*/



                        }


                        // If 5 = Display a CGI Animation on top of the scanned image
                        else if (action_id.equals("5")) {

                            if(!json2.isNull("action_id") && !json2.isNull("redeemar_id")) {
                                Log.d(LOGTAG, "Inside action id 5");


                                // Set to Shared Preferences, so next the beacon will be triggered after specified duration
                                editor.putString(res.getString(R.string.spf_beacon_triggered), "1");
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(getString(R.string.ext_scan_err), "R02010");
                                startActivity(intent);

                            }

                        }

                        // If 1 or default case = Go to brand dashboard
                        else {


                            String r_id = json2.get("redeemar_id").toString();

                            Log.d(LOGTAG, "Inside action id 1 or default");

                            // Set to Shared Preferences, so next the beacon will only be triggered after specified duration
                            editor.putString(res.getString(R.string.spf_beacon_triggered), "1");
                            editor.commit();

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
                /*else if (reader.getString("messageCode").equals("R01002")) {

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

                }*/

            } catch (JSONException e) {
                Log.i(LOGTAG,"error in parsing respopnse: "+e.toString());
                e.printStackTrace();

                /*Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                errIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(errIntent);*/
            }


        }
    }

}
