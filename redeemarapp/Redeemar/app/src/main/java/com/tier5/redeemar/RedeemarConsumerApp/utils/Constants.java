package com.tier5.redeemar.RedeemarConsumerApp.utils;

/**
 * Created by Windows on 13-02-2015.
 */
public interface Constants {
    String NA = "NA";
    final String defaultRadius = "1000"; // Default search radius in KM



    // Vuforia Access Key & Secret

    // For Development
    final String kAccessKeyDev = "714f4ae9df46063b7f16cf7ba20601514888b76b";
    final String kSecretKeyDev = "469943ef7070eaad5da6179e0cd0698263860fb4";

    // For Beta
    final String kAccessKeyBeta = "8d5237bf2f0beb2f93fe22b4c411989b2e10fb3b";
    final String kSecretKeyBeta = "900484a81dcdb9ea85c7c6dbc4e2e23cc5ebf6bf";


    final String youtubeAPIKey = "AIzaSyA4cndO4t85r0NZ-Ux9N8MzBJx06k4iNPA";
    final String defaultYoutubeVideoId = "qcw0jQ5o-Ls";

    final String logoDir = "redeemar/logos/";
    final String storeImgDir = "redeemar/stores/";
    final String brandImgDir = "redeemar/brands/";
    final String offerDir = "redeemar/offers/";

    final String beaconUUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";

    // Hour difference before the estimote beacons can again listened to again, usually it will be 24 hours, for now its just 1
    final int beaconListenDelayHours = 1;
    final int beaconListenDelaySecs = 600;



}
