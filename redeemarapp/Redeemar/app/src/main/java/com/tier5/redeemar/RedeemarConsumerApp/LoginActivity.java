package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

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

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOGTAG = "Login";

    private TextView tvRegister, tvForgotPass;
    private EditText txtEmail, txtPassword;
    private Button btnLogin;
    private LoginButton btnFBLogin;
    private SignInButton btnGoogleSignIn;
    private String email="", password="", firstName = "", lastName = "";;
    private ProgressDialog pd;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private User user;
    private CallbackManager callbackManager;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiAvailability googleApiAvailability;

    private Intent intent;
    private Typeface myFont;

    // For Google Plus
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private int mRequestCode;

    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;
    private ProfileTracker tracker;
    private String loginMethod = "", offerId = "", androidId = "", noAutoLogin = "";




    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {

            Log.d(LOGTAG, "Login success");
            AccessToken accessToken = loginResult.getAccessToken();

            Log.d(LOGTAG, "Login Result: "+loginResult.toString());

            tracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                    if(currentProfile != null)
                        Log.d(LOGTAG, "My First: "+currentProfile.getFirstName());

                }
            };

            tracker.startTracking();
            facebookGraphAccess(loginResult);

        }

        @Override
        public void onCancel() {
            Log.d(LOGTAG, "Login cancelled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d(LOGTAG, "Login error: "+error.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        if(sharedpref.getString(res.getString(R.string.spf_no_auto_login), null) != null) {
            noAutoLogin = sharedpref.getString(res.getString(R.string.spf_no_auto_login), "");
        }


        if(sharedpref.getString(res.getString(R.string.spf_login_method), null) != null) {
            loginMethod = sharedpref.getString(res.getString(R.string.spf_login_method), "");
        }


        if(sharedpref.getString(res.getString(R.string.spf_last_offer_id), null) != null) {
            offerId = sharedpref.getString(res.getString(R.string.spf_last_offer_id), "");
            Log.d(LOGTAG, "Last Offer Id New: "+offerId);
        }


        Log.d(LOGTAG, "Login Method: "+loginMethod);



        // For Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // For Google Sign In
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        buildGoogleApiClient();


        setContentView(R.layout.login);

        androidId = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
        Log.d(LOGTAG, "Android Id is: "+androidId);



        //myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));



        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        btnFBLogin = (LoginButton) findViewById(R.id.btn_login_fb);
        btnFBLogin.setReadPermissions("public_profile", "email");

        btnFBLogin.registerCallback(callbackManager, mCallback);

        btnFBLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOGTAG, "FB Login clicked");
            }
        });

        initRegularSignIn();
        //initFBSignIn();
        initGoogleSignIn();



        // If using in a fragment
        //loginButton.setFragment(this);
        // Other app specific specialization



        // Callback registration

        try {

            //procFBSignIn();

            //Thread.sleep(4000);

        } catch(Exception e) { Log.d(LOGTAG, "Login exception: "+e.toString()); }


        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Before Google sign in");
                editor.putString(res.getString(R.string.spf_login_method), "google"); // Storing action
                editor.commit();
                showProgressDialog();
                googleSignIn();

            }
        });


        btnFBLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "FB Login clicked");
                editor.putString(res.getString(R.string.spf_login_method), "facebook"); // Storing action
                editor.commit();

                //showProgressDialog();

            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Email is: "+txtEmail.getText());
                Log.d(LOGTAG, "Pass is: "+txtPassword.getText());

                if(txtEmail.getText().toString().equals("") || txtPassword.getText().toString().equals("")) {


                    builder = new AlertDialog.Builder(LoginActivity.this);//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(R.string.enter_password_email));
                    alertDialog = builder.create();

                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);


                    alertDialog.show();

                }

                else {


                    showProgressDialog();
                    //String androidId=Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                    Log.d(LOGTAG, "Android Id is: "+androidId);
                    Log.d(LOGTAG, "Last Offer Id is: "+offerId);

                    new LoginAsyncTask().execute(txtEmail.getText().toString(), txtPassword.getText().toString(), offerId);



                }




            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                //startActivity(intent);
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOGTAG, "Request Code: "+String.valueOf(requestCode));
        Log.d(LOGTAG, "Result Code: "+String.valueOf(resultCode));
        Log.d(LOGTAG, "Data: "+String.valueOf(data));
        Log.d(LOGTAG, "RC_SIGN_IN: "+String.valueOf(RC_SIGN_IN));


        if(loginMethod.equalsIgnoreCase("facebook")) {
            Log.d(LOGTAG, "Successfully logged in from Facebook");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        else if(loginMethod.equalsIgnoreCase("google")) {

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {


                // Check which request we're responding to

                mRequestCode = requestCode;

                if (resultCode != RESULT_OK) {
                    mSignInClicked = false;
                    hideProgressDialog();

                }


                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }

            }
        }

    }




    public void initRegularSignIn() {

        txtEmail = (EditText) findViewById(R.id.email);
        txtPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvForgotPass = (TextView) findViewById(R.id.tv_forgot_password);

        //tvRegister.setTypeface(myFont);
        //tvForgotPass.setTypeface(myFont);


    }

    private void initGoogleSignIn(){

        btnGoogleSignIn = (SignInButton) findViewById(R.id.btn_sign_in_google);
        btnGoogleSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnGoogleSignIn.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});

    }


    protected synchronized void buildGoogleApiClient() {


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        Log.d(LOGTAG, "Finished building API Client");

    }


    ////////////////////////////////////////////////////////////// FOR GOOGLE PLUS LOGIN //////////////////////////////////////////////////////////////



    @Override
    public void onStart() {
        super.onStart();

        Log.d(LOGTAG, "No auto login: "+noAutoLogin);

        /*if(noAutoLogin.equalsIgnoreCase("1")) {

            Log.d(LOGTAG, "Inside revoke access");
            gPlusRevokeAccess();
            gPlusSignOut();


            //mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();


        }
        else
            mGoogleApiClient.connect();*/

    }


    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(LOGTAG, "Inside onConnectionFailed");

        if (!connectionResult.hasResolution()) {
            googleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(),mRequestCode).show();
            return;
        }

        if (!mIntentInProgress) {

            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }

        Log.d(LOGTAG, "onConnectionFailed:" + connectionResult);
    }


    /*
    Sign-out from Google+ account
    */

    private void gPlusSignOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

        }
    }

    /*
     Revoking access from Google+ account
     */

    private void gPlusRevokeAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.d(LOGTAG, "User access revoked!");
                            buildGoogleApiClient();
                            mGoogleApiClient.connect();

                        }

                    });
        }
    }

    /*
    Sign-in into the Google + account
    */
    // [START signIn]
    private void googleSignIn() {

        if (!mGoogleApiClient.isConnecting()) {
            Log.d(LOGTAG, "gPlusSignIn not connected");



            editor.putString(res.getString(R.string.spf_login_method), "google"); // Storing login method
            editor.commit();

            mGoogleApiClient.connect();

            mSignInClicked = true;
            //showProgressDialog();
            resolveSignInError();

        }

        //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    // Google Sign In onConnected
    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        // Update the UI after signin
        //changeUI(true);

    }

    // Google Sign In onConnected
    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        //changeUI(false);
    }



    /*
    * Google Plus: get user's information name, email, profile pic,Date of birth,tag line and about me
    */

    private void getProfileInfo() {

        try {


            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                setPersonalInfo(currentPerson);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Unable to fetch personal information", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    * Google Plus: get user's information name, email, profile pic,Date of birth,tag line and about me
    */
    private void setPersonalInfo(Person currentPerson){
        String googleId = currentPerson.getId();
        String personName = currentPerson.getDisplayName();
        String personPhotoUrl = currentPerson.getImage().getUrl();
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


        // Email, Device Token, facebook Id, Google Id, where 0 implies no value
        new RegisterSocialAsyncTask().execute(email, androidId, "", googleId, offerId);


        //setProfilePic(personPhotoUrl);
        hideProgressDialog();
        Log.d(LOGTAG, "Person Name: "+personName);
        Log.d(LOGTAG, "Person Photo: "+personPhotoUrl);
        Log.d(LOGTAG, "Person Email: "+email);



        //Toast.makeText(this, "Person information is shown!", Toast.LENGTH_LONG).show();
    }


    /**
    * Google Plus: Method to resolve any signin errors
    * */
    private void resolveSignInError() {

        Log.d(LOGTAG, "Inside resolveSignInError");

        if(mConnectionResult != null) {

            Log.d(LOGTAG, "mConnectionResult is not null");

            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                    Log.d(LOGTAG, "Trying to connect");
                }
            }
        }

        else {

            Log.d(LOGTAG, "mConnectionResult is null");
        }



    }



    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();

        }

    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }



    private void facebookGraphAccess(LoginResult mLoginResult) {

        // App code
        GraphRequest request = GraphRequest.newMeRequest(
                mLoginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {


                    boolean isFBLoginSuccess = false;


                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        Log.d(LOGTAG, "Response from graph: " + response);

                        try {

                            if (!object.isNull("email")) {
                                //String androidId=Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                                Log.d(LOGTAG, "Email is: "+object.getString("email"));
                                 email = object.getString("email");
                                isFBLoginSuccess = true;
                            }

                            if (!object.isNull("first_name")) {
                                //String androidId=Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                                firstName = object.getString("first_name");
                                Log.d(LOGTAG, "First Name is: "+object.getString("first_name"));

                            }


                            if (!object.isNull("last_name")) {
                                //String androidId=Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                                lastName = object.getString("last_name");
                                Log.d(LOGTAG, "Last Name is: "+object.getString("last_name"));
                                // Email, Device Token, facebook Id, Google Id, where 0 implies no value
                                //new RegisterSocialAsyncTask().execute(object.getString("email"), androidId, object.getString("id"), "", offerId);
                            }

                            if (!object.isNull("id")) {





                                String facebookUserId = object.getString("id");
                                Log.d(LOGTAG, "Profile Id: "+facebookUserId);

                                if(facebookUserId != "") {

                                    Bitmap bitmap = Utils.getFacebookProfilePicture(facebookUserId);

                                    /*if(bitmap != null) {
                                        String profilePicPath = Utils.saveToInternalStorage(getApplicationContext(), bitmap);
                                        Log.d(LOGTAG, "Profile Image Path: "+profilePicPath);

                                    }*/


                                    /*

                                        String profileURL = "/" + facebookUserId + "/picture";
                                        new GraphRequest(
                                                AccessToken.getCurrentAccessToken(),
                                                profileURL,
                                                null,
                                                HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    public void onCompleted(GraphResponse response) {
                                                        JSONObject picObj = response.getJSONObject();
                                                        try {

                                                        String b = picObj.getString("FACEBOOK_NON_JSON_RESULT");

                                                        } catch (Exception ex) {
                                                            Log.d(LOGTAG, "Profile Picture Exception: " + ex.toString());
                                                        }

                                                        Log.d(LOGTAG, "Profile Picture: " + response.toString());
                                                    }
                                                }
                                        ).executeAsync();*/


                                }


                            }

                            Log.d(LOGTAG, firstName);
                            Log.d(LOGTAG, lastName);

                            //showProgressDialog();

                            if(isFBLoginSuccess) {
                                new RegisterSocialAsyncTask().execute(object.getString("email"), androidId, object.getString("id"), "", offerId);
                            }

                            //tvWelcome.setText("Welcome "+firstName+" "+lastName);

                        }catch (Exception e){
                            e.printStackTrace();
                            hideProgressDialog();

                        }


                    }

                });

        Bundle parameters = new Bundle();
        //parameters.putString("fields", "id,name,email,gender, birthday");
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }






    private class LoginAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", basePath = "";

        public LoginAsyncTask() {

            //showProgressDialog();

            url = UrlEndpoints.loginURL;
            basePath = UrlEndpoints.basePathURL;

        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            String email = params[0];
            String password = params[1];
            String offer_id = params[2];

            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                JSONObject data = new JSONObject();
                //JSONObject auth=new JSONObject();
                //JSONObject parent=new JSONObject();
                data.put("webservice_name","userlogin");
                data.put("email", email);
                data.put("password", password);

                if(!offer_id.equalsIgnoreCase(""))
                    data.put("offer_id", offer_id);

                OutputStream os = conn.getOutputStream();

                Log.d(LOGTAG, "Request: " + data.toString());

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write("data="+data.toString());
                bufferedWriter.flush();
                bufferedWriter.close();


                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                //Log.d(LOGTAG, "Do In background: " + response);
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

            if (resp != null) {


                try {
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();


                    JSONObject reader = new JSONObject(resp);

                    Log.d(LOGTAG, "Response: " + reader.getString("data"));


                    // Login successful
                    if (reader.getString("messageCode").equals("R01001")) {

                        JSONObject jsonObject = new JSONObject( reader.getString("data"));

                        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(getString(R.string.spf_user_id), jsonObject.getString("user_id")); // Storing User Id
                        editor.putString(getString(R.string.spf_first_name), jsonObject.getString("first_name")); // Storing First name
                        editor.putString(getString(R.string.spf_last_name), jsonObject.getString("last_name")); // Storing Last Name
                        editor.putString(getString(R.string.spf_email), jsonObject.getString("email")); // Storing Email
                        editor.putString(getString(R.string.spf_mobile), jsonObject.getString("phone")); // Storing Mobile
                        editor.commit(); // commit changes

                        Log.d(LOGTAG, "Login success");

                        // After successful login, redirect to the previous action

                        Bundle extras = getIntent().getExtras();

                        if (extras != null) {
                            Resources res = getResources();
                            String lastActivity = extras.getString(res.getString(R.string.ext_activity));
                            //String offerId = extras.getString(res.getString(R.string.ext_offer_id));

                            Log.d(LOGTAG, "Last Activity: "+lastActivity);

                            if(lastActivity.equalsIgnoreCase("BrowseOffers"))
                                intent = new Intent(LoginActivity.this, BrowseOffersActivity.class);

                            else if(lastActivity.equalsIgnoreCase("OfferList"))
                                intent = new Intent(LoginActivity.this, OfferListActivity.class);

                            else if(lastActivity.equalsIgnoreCase("OfferDetails") && extras.getString(res.getString(R.string.ext_offer_id)) != null) {
                                intent = new Intent(LoginActivity.this, OfferDetailsActivity.class);
                                intent.putExtra(res.getString(R.string.ext_activity), extras.getString(res.getString(R.string.ext_offer_id))); // Settings the activty name where it will be redirected to
                            }

                            else if(lastActivity.equalsIgnoreCase("MyOffers")) {
                                intent = new Intent(LoginActivity.this, OfferDetailsActivity.class);
                                intent.putExtra(res.getString(R.string.ext_activity), extras.getString(res.getString(R.string.ext_offer_id))); // Settings the activty name where it will be redirected to
                            }

                            else
                                intent = new Intent(LoginActivity.this, BrowseOffersActivity.class);


                            //intent = new Intent(LoginActivity.this, LoadingActivity.class);

                            hideProgressDialog();

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else {

                            hideProgressDialog();

                            Intent intent2 = new Intent(LoginActivity.this, BrowseOffersActivity.class);
                            startActivity(intent2);
                            finish();
                        }

                    }
                    // You are not allowed to login from mobile device, login from web.This is for admin user.
                    else if (reader.getString("messageCode").equals("R01002") || reader.getString("messageCode").equals("R01003") || reader.getString("messageCode").equals("R01004")) {

                        hideProgressDialog();

                        builder = new AlertDialog.Builder(LoginActivity.this);//Context parameter
                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do stuff
                            }
                        });

                        if(reader.getString("messageCode").equals("R01002"))
                            builder.setMessage(getString(R.string.login_auth_denied));

                        if(reader.getString("messageCode").equals("R01003"))
                            builder.setMessage(getString(R.string.user_status_inactive));

                        if(reader.getString("messageCode").equals("R01004"))
                            builder.setMessage(getString(R.string.invalid_user_pass));


                        alertDialog = builder.create();

                        alertDialog.setTitle(getString(R.string.alert_title));
                        alertDialog.setIcon(R.drawable.icon_cross);


                        alertDialog.show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    hideProgressDialog();
                }



            }
        }


    }






    private class RegisterSocialAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", basePath = "", email = "", android_id = "", facebook_id = "", google_id = "", offer_id = "";

        public RegisterSocialAsyncTask() {

            url = UrlEndpoints.registerSocialURL;
            basePath = UrlEndpoints.basePathURL;


        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            email = params[0];
            android_id = params[1];
            facebook_id = params[2];
            google_id = params[3];
            offer_id = params[4];


            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                JSONObject data = new JSONObject();
                data.put("webservice_name","socialsignup");
                data.put("email", email);
                data.put("device_token", android_id);
                data.put("source", 2); // 2= Android

                if(!facebook_id.equals("")) {
                    data.put("first_name", facebook_id);
                    data.put("facebook_id", facebook_id);
                    data.put("social_type", "facebook");
                }

                else if(!google_id.equals("")) {
                    data.put("google_id", google_id);
                    data.put("social_type", "google");
                }


                if(!offer_id.equals("")) {
                    data.put("offer_id", offer_id);

                }



                Log.d(LOGTAG, "Social Async Offer Id "+offer_id);


                OutputStream os = conn.getOutputStream();

                Log.d(LOGTAG, "Social Request: " + data.toString());


                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write("data="+data.toString());
                bufferedWriter.flush();
                bufferedWriter.close();


                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                //Log.d(LOGTAG, "Do In background: " + response);
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

            if (resp != null) {


                try {
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();


                    JSONObject reader = new JSONObject(resp);

                    Log.d(LOGTAG, "Social Register Response: " + reader.getString("data"));
                    Log.d(LOGTAG, "Social Message Code: " + reader.getString("messageCode"));


                    // Login successful
                    if (reader.getString("messageCode").equals("R01001") || reader.getString("messageCode").equals("R01002")) {

                        JSONObject jsonObject = new JSONObject( reader.getString("data"));

                        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(getString(R.string.spf_user_id), jsonObject.getString("id")); // Storing User Id
                        editor.putString(getString(R.string.spf_email), jsonObject.getString("email")); // Storing Email

                        if(!facebook_id.equals(""))
                            editor.putString(getString(R.string.spf_facebook_id), facebook_id); // Storing facebook Id

                        if(!google_id.equals(""))
                            editor.putString(getString(R.string.spf_google_id), google_id); // Storing google Id

                        editor.commit(); // commit changes

                        //Log.d(LOGTAG, "Login successful");
                        //showProgressDialog();


                        // After successful login, redirect to the previous action

                        Bundle extras = getIntent().getExtras();

                        if (extras != null) {
                            Resources res = getResources();
                            String lastActivity = extras.getString(res.getString(R.string.ext_activity));
                            //String offerId = extras.getString(res.getString(R.string.ext_offer_id));



                            //Log.d(LOGTAG, "Last Activity: "+lastActivity);

                            if(lastActivity.equalsIgnoreCase("BrowseOffers"))
                                intent = new Intent(LoginActivity.this, BrowseOffersActivity.class);

                            else if(lastActivity.equalsIgnoreCase("OfferList"))
                                intent = new Intent(LoginActivity.this, OfferListActivity.class);

                            else if(lastActivity.equalsIgnoreCase("OfferDetails") && extras.getString(res.getString(R.string.ext_offer_id)) != null) {
                                intent = new Intent(LoginActivity.this, OfferDetailsActivity.class);
                                intent.putExtra(res.getString(R.string.ext_activity), extras.getString(res.getString(R.string.ext_offer_id))); // Settings the activty name where it will be redirected to
                            }

                            else if(lastActivity.equalsIgnoreCase("MyOffers")) {
                                intent = new Intent(LoginActivity.this, OfferDetailsActivity.class);
                                intent.putExtra(res.getString(R.string.ext_activity), extras.getString(res.getString(R.string.ext_offer_id))); // Settings the activty name where it will be redirected to
                            }

                            else if(lastActivity.equalsIgnoreCase("EditProfile")) {
                                // Settings the activty name where it will be redirected to
                                intent = new Intent(LoginActivity.this, BrowseOffersActivity.class);
                                intent.putExtra(res.getString(R.string.ext_activity), extras.getString(res.getString(R.string.ext_offer_id)));
                            }

                            else
                                intent = new Intent(LoginActivity.this, BrowseOffersActivity.class);


                            //intent = new Intent(LoginActivity.this, LoadingActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent2 = new Intent(LoginActivity.this, BrowseOffersActivity.class);
                            startActivity(intent2);
                            finish();
                        }

                    }

                    // In case the user already exists then set the information
                    else if (reader.getString("messageCode").equals("R01002")) {

                        // TODO Set session here

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }


    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        //hideProgressDialog();

        //super.onBackPressed();
        intent = new Intent(LoginActivity.this, BrowseOffersActivity.class);
        startActivity(intent);
        finish();



    }

}
