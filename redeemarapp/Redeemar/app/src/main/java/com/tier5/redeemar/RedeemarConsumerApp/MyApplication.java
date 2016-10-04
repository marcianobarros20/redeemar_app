package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import java.util.List;
import java.util.UUID;

public class MyApplication extends Application {

    private static final String LOGTAG = "MyApplication";

    private BeaconManager beaconManager;

    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Dibs", "Inside MyApplication onCreate()");

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.d("Dibs", "Your gate closes in 47 minutes");
                //Toast.makeText(getApplicationContext(), "You entered the monitoring region.", Toast.LENGTH_LONG);
                showNotification("Welcome to Redeemar.", "Explore our store");

//                String target_id = "dfadf13e7c8d431f8cdb52d9e78afa6d";
//                Intent i = new Intent(MyApplication.this, BrandMainActivity.class);
//                i.putExtra("unique_target_id", target_id);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
            }
            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
                Toast.makeText(getApplicationContext(), "Your exited the monitoring region", Toast.LENGTH_LONG);

                showNotification("Goodbye from Redeemar.",  "Visit us again soon!");

            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d("Dibs", "Inside Monitor");
                beaconManager.startMonitoring(new Region("monitored region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 51845, 29961));
            }
        });
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

        if(severBase.contains(word))
            return "beta";
        else
            return "dev";

    }
}
