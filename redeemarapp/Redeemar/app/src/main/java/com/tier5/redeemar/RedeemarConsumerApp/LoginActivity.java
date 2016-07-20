package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

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
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String LOGTAG = "Login";

    private TextView tvRegister, tvForgotPass;
    private EditText txtEmail, txtPassword;
    private Button btnLogin;
    private LoginButton btnFBLogin;
    private SignInButton btnGoogleSignIn;
    private String email, password;
    private ProgressDialog pd;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Resources res;
    private SharedPreferences sharedpref;
    private User user;
    CallbackManager callbackManager;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    Intent intent;
    Typeface myFont;

    private static final int RC_SIGN_IN = 9001;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        // For Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // For Google Sign In
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        setContentView(R.layout.login);

        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        txtEmail = (EditText) findViewById(R.id.email);
        txtPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvForgotPass = (TextView) findViewById(R.id.tv_forgot_password);

        tvRegister.setTypeface(myFont);
        tvForgotPass.setTypeface(myFont);

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        btnFBLogin = (LoginButton) findViewById(R.id.btn_login_fb);
        btnFBLogin.setReadPermissions("public_profile", "email");

        btnGoogleSignIn = (SignInButton) findViewById(R.id.btn_sign_in_google);
        // If using in a fragment
        //loginButton.setFragment(this);
        // Other app specific specialization



        // Callback registration

        try {



            btnFBLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.d(LOGTAG, "Facebook login successful: "+loginResult);

                    btnFBLogin.setText(getString(R.string.signing_in));

                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

                                    Log.d(LOGTAG, "Response from graph: " + response);
                                    String user_full_name = "";
                                    try {
                                        /*user = new User();
                                        user.facebookID = object.getString("id").toString();
                                        user.email = object.getString("email").toString();
                                        user.name = object.getString("name").toString();
                                        user.gender = object.getString("gender").toString();
                                        PrefUtils.setCurrentUser(user,LoginActivity.this);*/

                                        if (object.getString("email") != null) {
                                            String androidId=Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                                            Log.d(LOGTAG, "Android Id is: "+androidId);
                                            // Email, Device Token, facebook Id, Google Id, where 0 implies no value
                                            new RegisterSocialAsyncTask().execute(object.getString("email"), androidId, object.getString("id"), "");
                                        }

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    //Toast.makeText(LoginActivity.this,"Welcome "+user_full_name, Toast.LENGTH_LONG).show();
                                    Toast.makeText(LoginActivity.this,"Loggin in...", Toast.LENGTH_LONG).show();
                                    //Intent intent=new Intent(LoginActivity.this, BrowseOffersActivity.class);
                                    //startActivity(intent);
                                    //finish();

                                }

                            });

                    Bundle parameters = new Bundle();
                    //parameters.putString("fields", "id,name,email,gender, birthday");
                    parameters.putString("fields", "id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {
                    // App code
                    Log.d(LOGTAG, "Facebook login cancelled");
                    Toast.makeText(getApplicationContext(), "Facebook login has been cancelled", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.d(LOGTAG, "Facebook login error");
                    Toast.makeText(getApplicationContext(), "Error occured in facebook login", Toast.LENGTH_LONG).show();

                }
            });

        } catch(Exception e) { Log.d(LOGTAG, "facebook login exception"); }


        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Before Google sign in");

                signIn();

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



                    String androidId=Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                    Log.d(LOGTAG, "Android Id is: "+androidId);

                    new LoginAsyncTask().execute(txtEmail.getText().toString(), txtPassword.getText().toString(), androidId);

                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage(getString(R.string.logging_in));
                    pd.show();

                }




            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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
    public void onStart() {
        super.onStart();

        /*OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(LOGTAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }



    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOGTAG, "Google SignIn Result:" + result.isSuccess());
        Log.d(LOGTAG, "Google SignIn Account:" + result.getSignInAccount());
        Log.d(LOGTAG, "Google SignIn Status:" + result.getStatus());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            Log.d(LOGTAG, "Google Display Name: "+acct.getDisplayName());
            Log.d(LOGTAG, "Google Family Name: "+acct.getFamilyName());
            Log.d(LOGTAG, "Google Given Name: "+acct.getGivenName());
            Log.d(LOGTAG, "Google Email: "+acct.getEmail());
            Log.d(LOGTAG, "Google Id: "+acct.getId());
            Log.d(LOGTAG, "Google Id Token: "+acct.getIdToken());


            String androidId = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
            Log.d(LOGTAG, "Android Id is: "+androidId);



            if (acct.getEmail() != null) {
                // Email, Device Token, facebook Id, Google Id
                new RegisterSocialAsyncTask().execute(acct.getEmail(), androidId, "", acct.getId());
            }


        } else {
            // Signed out, show unauthenticated UI.


            builder = new AlertDialog.Builder(LoginActivity.this);//Context parameter
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do stuff
                }
            });
            builder.setMessage(getString(R.string.error_signing_in));
            alertDialog = builder.create();

            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_cross);


            alertDialog.show();



        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {

        Log.d(LOGTAG, "Inside Google sign in");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(LOGTAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.btn_sign_in_google:
                signIn();
                break;
            case R.id.btn_login_fb:
                signOut();
                break;
            case R.id.btn_login:
                revokeAccess();
                break;
        }*/
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOGTAG, "Request Code: "+String.valueOf(requestCode));
        Log.d(LOGTAG, "Result Code: "+String.valueOf(resultCode));
        Log.d(LOGTAG, "Data: "+String.valueOf(data));
        Log.d(LOGTAG, "RC_SIGN_IN: "+String.valueOf(RC_SIGN_IN));

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        /*if (requestCode == RC_SIGN_IN) {

            Log.d(LOGTAG, "Returned result");

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }*/

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }









    private class LoginAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", basePath = "";

        public LoginAsyncTask() {

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
            String android_id = params[2];

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


                    pd.dismiss();


                    // Login successful
                    if (reader.getString("messageCode").equals("R01001")) {

                        JSONObject jsonObject = new JSONObject( reader.getString("data"));

                        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(getString(R.string.spf_user_id), jsonObject.getString("user_id")); // Storing User Id
                        editor.putString(getString(R.string.spf_email), jsonObject.getString("email")); // Storing Email
                        //editor.putString(getString(R.string.spf_mobile), jsonObject.getString("mobile")); // Storing Mobile
                        //editor.putString(getString(R.string.spf_first_name), jsonObject.getString("first_name")); // Storing First name
                        //editor.putString(getString(R.string.spf_last_name), jsonObject.getString("last_name")); // Storing Last Name
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
                    // You are not allowed to login from mobile device, login from web.This is for admin user.
                    else if (reader.getString("messageCode").equals("R01002") || reader.getString("messageCode").equals("R01003") || reader.getString("messageCode").equals("R01004")) {

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
                }



            }
        }


    }






    private class RegisterSocialAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", basePath = "", email = "", android_id = "", facebook_id = "", google_id = "";

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
                data.put("webservice_name","socialsignup");
                data.put("email", email);
                data.put("device_token", android_id);
                data.put("source", 2); // 2= Android

                if(!facebook_id.equals("")) {
                    data.put("facebook_id", facebook_id);
                    data.put("social_type", "facebook");
                }

                else if(!google_id.equals("")) {
                    data.put("google_id", google_id);
                    data.put("social_type", "google");
                }


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

                    Log.d(LOGTAG, "Social Register Response: " + reader.getString("data"));
                    Log.d(LOGTAG, "Social Message Code: " + reader.getString("messageCode"));


                    //pd.dismiss();


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

                        Log.d(LOGTAG, "Login successful");

                        /*

                                        SharedPreferences.Editor editor = sharedpref.edit();
                                        // RIGHT NOW IT IS HARD-CODED, LATE ON THE VALUE WILL COME FROM WEB SERVICES CALL
                                        editor.putString(getString(R.string.spf_user_id), "82"); // Storing User Id
                                        editor.putString(getString(R.string.spf_email), object.getString("email").toString()); // Storing Email
                                        editor.putString(getString(R.string.spf_facebook_id), object.getString("id").toString()); // Storing facebook Id

                                        user_full_name = object.getString("name").toString();

                                        Log.d(LOGTAG, "User Email: "+object.getString("email").toString());
                                        Log.d(LOGTAG, "User FB Id: "+user_full_name);
                                        Log.d(LOGTAG, "User Full Name: "+user_full_name);
                                        editor.commit(); // commit changes
                        */

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



    /*
    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        Log.d("RDM", "Inside back button pressed in display success");
        Intent intent = new Intent(DisplaySuccessActivity.this, AboutScreen.class);
        startActivity(intent);
        finish();
    }*/

}
