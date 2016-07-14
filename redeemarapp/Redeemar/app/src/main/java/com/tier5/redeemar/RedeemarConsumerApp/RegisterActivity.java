package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
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

public class RegisterActivity extends Activity {


    private static final String LOGTAG = "Login";

    private TextView tvSignIn;
    private EditText txtEmail, txtMobile, txtPassword, txtConfirmPassword;
    private Button btnRegister;
    private ProgressDialog pd;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private SharedPreferences sharedpref;
    Intent intent;
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        txtEmail = (EditText) findViewById(R.id.email);
        //txtMobile = (EditText) findViewById(R.id.mobile);
        txtPassword = (EditText) findViewById(R.id.password);
        txtConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        btnRegister = (Button) findViewById(R.id.btn_register);

        tvSignIn = (TextView) findViewById(R.id.sign_in);
        tvSignIn.setTypeface(myFont);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Email is: "+txtEmail.getText());
                Log.d(LOGTAG, "Pass is: "+txtPassword.getText());
                //Log.d(LOGTAG, "Mobile is: "+txtMobile.getText());




                if(txtEmail.getText().toString().equals("") || txtPassword.getText().toString().equals("")) {


                    builder = new AlertDialog.Builder(RegisterActivity.this);//Context parameter
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

                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {

                    builder = new AlertDialog.Builder(RegisterActivity.this);//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(R.string.enter_valid_email));
                    alertDialog = builder.create();


                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);


                    alertDialog.show();


                }

                /*
                else if(!android.util.Patterns.PHONE.matcher(txtMobile.getText().toString()).matches()) {

                    builder = new AlertDialog.Builder(RegisterActivity.this);//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(R.string.enter_valid_phone));
                    alertDialog = builder.create();


                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);


                    alertDialog.show();

                }*/


                else if(!txtPassword.getText().toString().equalsIgnoreCase(txtConfirmPassword.getText().toString())) {


                    builder = new AlertDialog.Builder(RegisterActivity.this);//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(R.string.password_mismatch));
                    alertDialog = builder.create();


                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);


                    alertDialog.show();



                }

                else {



                    String androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.d(LOGTAG, "Android Id is: "+androidId);

                    //new RegisterAsyncTask().execute(txtEmail.getText().toString(), txtMobile.getText().toString(), txtPassword.getText().toString(), androidId);
                    new RegisterAsyncTask().execute(txtEmail.getText().toString(), txtPassword.getText().toString(), androidId);

                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage(getString(R.string.logging_in));
                    pd.show();

                }




            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }



    private class RegisterAsyncTask extends AsyncTask<String, Void, String> {


        String url = "";

        public RegisterAsyncTask() {

            url = UrlEndpoints.registerURL;

        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            String email = params[0];
            //String mobile = params[1];
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
                data.put("webservice_name","userregister");
                data.put("email", email);
                //data.put("mobile", mobile);
                data.put("password", password);
                data.put("device_token", android_id);
                data.put("source", 2); // 2= Android

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

                    Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                    pd.dismiss();


                    // Login successful
                    if (reader.getString("messageCode").equals("R01001")) {

                        JSONObject jsonObject = new JSONObject( reader.getString("data"));

                        sharedpref = getApplicationContext().getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(getString(R.string.spf_user_id), jsonObject.getString("id")); // Storing User Id
                        editor.putString(getString(R.string.spf_email), jsonObject.getString("email")); // Storing Email
                        //editor.putString(getString(R.string.spf_mobile), jsonObject.getString("mobile")); // Storing Mobile
                        //editor.putString(getString(R.string.spf_first_name), jsonObject.getString("first_name")); // Storing First name
                        //editor.putString(getString(R.string.spf_last_name), jsonObject.getString("last_name")); // Storing Last Name
                        editor.commit(); // commit changes

                        Log.d(LOGTAG, "Registration successful");

                        Toast.makeText(getApplicationContext(), getString(R.string.register_success_msg), Toast.LENGTH_LONG).show();

                        // After successful login, redirect to the previous action

                        Bundle extras = getIntent().getExtras();

                        if (extras != null) {
                            Resources res = getResources();
                            String lastActivity = extras.getString(res.getString(R.string.ext_activity));

                            if(lastActivity.equalsIgnoreCase("BrowseOffers"))
                                intent = new Intent(RegisterActivity.this, BrowseOffersActivity.class);


                            else if(lastActivity.equalsIgnoreCase("OfferList"))
                                intent = new Intent(RegisterActivity.this, OfferListActivity.class);

                            else
                                intent = new Intent(RegisterActivity.this, MyOffersActivity.class);

                            startActivity(intent);
                        }
                        else {
                            intent = new Intent(RegisterActivity.this, BrowseOffersActivity.class);
                            startActivity(intent);
                        }



                    }
                    // You are not allowed to login from mobile device, login from web.This is for admin user.
                    else {

                        builder = new AlertDialog.Builder(RegisterActivity.this);//Context parameter
                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do stuff
                            }
                        });

                        if(reader.getString("messageCode").equals("R01002"))
                            builder.setMessage(getString(R.string.duplicate_email));
                        else
                            builder.setMessage(getString(R.string.unknown_reg_err));

                        Log.d(LOGTAG, reader.getString("messageCode"));

                        /*if(reader.getString("messageCode").equals("R01003"))
                            builder.setMessage(getString(R.string.user_status_inactive));

                        if(reader.getString("messageCode").equals("R01004"))
                            builder.setMessage(getString(R.string.invalid_user_pass));*/


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
