package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class SuperConnectionDetector {
    private Context _context;
    public SuperConnectionDetector(Context context){
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * ****/

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
          NetworkInfo info = connectivity.getActiveNetworkInfo();

          if (info != null) {
              if (info.getState() == NetworkInfo.State.CONNECTED)
              {
                  return true;
              }
          }

        }
        return false;
    }
}
