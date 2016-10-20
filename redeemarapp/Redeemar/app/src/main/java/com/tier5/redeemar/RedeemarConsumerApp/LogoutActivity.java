package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.plus.Plus;

public class LogoutActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String LOGTAG = "Logout";

    private Resources res;
    private SharedPreferences sharedpref;
    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode



        SharedPreferences.Editor editor = sharedpref.edit();

        editor.remove(getString(R.string.spf_user_id));
        editor.remove(getString(R.string.spf_first_name));
        editor.remove(getString(R.string.spf_last_name));
        editor.remove(getString(R.string.spf_email));
        editor.remove(getString(R.string.spf_mobile));
        editor.remove(getString(R.string.spf_facebook_id));
        editor.remove(getString(R.string.spf_google_id));
        editor.remove(getString(R.string.spf_last_offer_id));


        editor.putString(res.getString(R.string.spf_no_auto_login), "1"); // Storing login method
        editor.commit();


        FacebookSdk.sdkInitialize(getApplicationContext());
        //callbackManager = CallbackManager.Factory.create();
        // We can logout from facebook by calling following method
        //LoginManager.getInstance().logOut();

        //if(isLoggedIn()) {
            LoginManager.getInstance().logOut();
            Log.d(LOGTAG, "Successfully logged out from facebook account");
        //}

        // Logout from Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();

            // updateUI(false);
            Log.d(LOGTAG, "Successfully logged out from google account");
        }
        else {
            Log.d(LOGTAG, "Not connectd from google account");
        }




        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();


        /*
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
            return;
        }
        initializeView();*/
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        mSignInClicked = false;

        // updateUI(true);
        //Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();
        // updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    protected void onStart() {
        super.onStart();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void logout(){

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.d(LOGTAG, "Access Token: "+accessToken);
        return accessToken != null;
    }


}
