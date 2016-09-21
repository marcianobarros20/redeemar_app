package com.tier5.redeemar.RedeemarConsumerApp.json;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Requestor {
    private static final String LOGTAG = "Requestor";


    public static JSONObject requestBrandDetailsJSON(RequestQueue requestQueue, String url, String targetId) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "check_target");
            data.put("target_id", targetId);


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }




    public static JSONObject requestOffersJSON(RequestQueue requestQueue, int mType, String url, String userId, String filterId, String catLevel, String latitude, String longitude, String selfLatitude, String selfLongitude) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name","showoffers");
            data.put("type", String.valueOf(mType));
            data.put("lat", latitude);
            data.put("long", longitude);
            data.put("selflat", selfLatitude);
            data.put("selflong", selfLongitude);
            data.put("radius", Constants.defaultRadius);
            data.put("user_id", userId);

            // Filter By Redeemar Id
            if(mType == 1) {
                data.put("reedemar_id", filterId);
            }
            // Filter By Redeemar Id
            else if(mType == 2) {
                data.put("campaign_id", filterId);
            }
            // Filter By category/Sub Category Id
            else if(mType == 3) {
                data.put("category_id", filterId);
                data.put("category_level", catLevel);

            }


            OutputStream os = conn.getOutputStream();

            Log.d(LOGTAG, "URL : " + url);

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

            Log.d(LOGTAG, "Do In background: " + response);
            bufferedReader.close();
            inputStream.close();
            conn.disconnect();
            os.close();
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }



    public static JSONObject requestMyOffersJSON(RequestQueue requestQueue, String url, String userId, String latitude, String longitude) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name","showoffers");
            data.put("user_id", userId);
            data.put("lat", latitude);
            data.put("long", longitude);
            data.put("radius", Constants.defaultRadius);


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }

    /*public static JSONObject requestOffersByCampaignJSON(RequestQueue requestQueue, String url, String redeemarId, String camapignId, String latitude, String longitude) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



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


            data.put("webservice_name","showoffers");
            data.put("reedemer_id", redeemarId);
            data.put("user_id", 0); // Deliberately sending user id 0
            data.put("lat", latitude);
            data.put("long", longitude);
            data.put("radius", Constants.defaultRadius);




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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }*/



    public static JSONObject requestNearByBrandsJSON(RequestQueue requestQueue, String url, String latitude, String longitude) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;


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
            data.put("webservice_name","mapalloffers");


            data.put("lat", latitude);
            data.put("long", longitude);

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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reader;
    }



    public static JSONObject requestMenuItemsJSON(RequestQueue requestQueue, String url) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "categories");


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }


    public static JSONObject requestSendFeedbackJSON(RequestQueue requestQueue, String url, String email, String userId, String feedback, String rating) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "sendfeedback");
            data.put("email", email);
            data.put("user_id", userId);
            data.put("feedback", feedback);
            data.put("rating", rating);
            data.put("source", "android");


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }

    public static JSONObject requestUpdateProfileJSON(RequestQueue requestQueue, String url, String userId, String firstName, String lastName, String email, String phone) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "updateprofile");
            data.put("user_id", userId);
            data.put("first_name", firstName);
            data.put("last_name", lastName);
            data.put("email", email);
            data.put("phone", phone);


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reader;
    }


    public static JSONObject requestValidatePassCodeJSON(RequestQueue requestQueue, String url, String userId, String offerId, String passcode) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "validatepasscode");
            data.put("user_id", userId);
            data.put("offer_id", offerId);
            data.put("redemption_code", passcode);
            Log.d(LOGTAG, "Passcode: "+passcode);


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return reader;
    }


    public static JSONObject requestSearchShortJSON(RequestQueue requestQueue, String url, String location, String keyword) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "searchshort");
            data.put("location", location);
            data.put("keyword", keyword);

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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reader;
    }


    public static JSONObject requestSearchFullJSON(RequestQueue requestQueue, String url, String location, String keyword) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "searchfull");
            data.put("location", location);
            data.put("keyword", keyword);


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }



    public static JSONObject requestSearchLocationJSON(RequestQueue requestQueue, String url, String location) {


        URL myUrl = null;
        HttpURLConnection conn = null;
        String response = "";
        JSONObject reader = null;



        try {
            myUrl = new URL(url);
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject data = new JSONObject();

            data.put("webservice_name", "locations");
            data.put("keyword", location);


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
            reader = new JSONObject(response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }


}
