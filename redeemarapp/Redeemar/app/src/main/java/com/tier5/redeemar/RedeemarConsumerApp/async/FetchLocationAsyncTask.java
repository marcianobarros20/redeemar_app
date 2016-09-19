package com.tier5.redeemar.RedeemarConsumerApp.async;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.tier5.redeemar.RedeemarConsumerApp.callbacks.LocationFetchedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Keys;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by tier5 on 22/6/16.
 */


public class FetchLocationAsyncTask extends AsyncTask<String, Void, User> {


    private static final String LOGTAG = "FetchLocationAsyncTask";
    private LocationFetchedListener myComponent;
    private Context context;

    public FetchLocationAsyncTask(LocationFetchedListener myComponent, Context ctx) {

        this.myComponent = myComponent;
        this.context = ctx;
        Log.d(LOGTAG, "Inside LocationFetchedListener constructor");

    }

    public FetchLocationAsyncTask() {

    }

    @Override
    protected void onPreExecute() {


    }


    @Override
    protected User doInBackground(String... params) {

        String lat = params[0];
        String lon = params[1];

        User user = new User();

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);



                Log.d(LOGTAG, "Max Address Line: "+address.getMaxAddressLineIndex());
                String formattedAddress = "";

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {

                    //sb.append(address.getAddressLine(i)).append("\n");

                    if(!formattedAddress.equals(""))
                        formattedAddress = " " + formattedAddress;

                    formattedAddress = formattedAddress + address.getAddressLine(i);

                    Log.d(LOGTAG, "Address Line 1: "+address.getAddressLine(i));

                }

                user.setAddress(formattedAddress);
                user.setCity(address.getLocality());
                user.setState(address.getAdminArea());
                user.setZipcode(address.getPostalCode());
                user.setLocation(address.getSubAdminArea());
                user.setLat(String.valueOf(address.getLatitude()));
                user.setLon(String.valueOf(address.getLongitude()));

                Log.d(LOGTAG, "Full Address: "+address.getLocality());
                Log.d(LOGTAG, "City: "+address.getLocality());
                Log.d(LOGTAG, "Sub Locality: "+address.getLocality());
                Log.d(LOGTAG, "State: "+address.getAdminArea());
                Log.d(LOGTAG, "Zip Code: "+address.getPostalCode());
                Log.d(LOGTAG, "Sub Admin: "+address.getSubAdminArea());
                Log.d(LOGTAG, "Latitude: "+address.getLatitude());
                Log.d(LOGTAG, "Longitude: "+address.getLongitude());



            } else {

            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Unable connect to Geocoder", e);
        }

        return user;

    }

    @Override
    protected void onPostExecute(User user) {

        Log.d(LOGTAG, "User Lat: "+user.getLat());

        if (myComponent != null) {
            myComponent.onLocationFetched(user);
        }
    }


}




