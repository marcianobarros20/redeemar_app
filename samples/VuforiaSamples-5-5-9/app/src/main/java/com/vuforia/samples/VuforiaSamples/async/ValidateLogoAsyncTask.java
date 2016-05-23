package com.vuforia.samples.VuforiaSamples.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vuforia.samples.VuforiaSamples.utils.Webservicelinks;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tier5 on 11/5/16.
 */
// Class with extends AsyncTask class

public class ValidateLogoAsyncTask extends AsyncTask<String, Void, Void> {

    // Required initialization
    private Activity actity;

    private final HttpClient Client = new DefaultHttpClient();
    //HttpClient client = HttpClientBuilder.create().build();
    private String Content;
    private String Error = null;
    String url = Webservicelinks.validateLogoURL;
    String page = "";
    String response = "";
    Context context;

    public ValidateLogoAsyncTask(Activity ctxActivity) {
        this.actity = ctxActivity;
    }




    protected void onPreExecute() {


    }

    // Call after onPreExecute method
    protected Void doInBackground(String... params) {

        try
        {


            String target_id = params[0];
            JSONObject registerJson = new JSONObject();
            registerJson.put("webservice_name", "check_target");
            registerJson.put("target_id", target_id);

            Log.d("RMD", "Target Id: "+target_id);
            Log.d("RMD", "JSON: "+registerJson.toString());



            List<NameValuePair> namevaluepair = new ArrayList<NameValuePair>();

            namevaluepair.add(new BasicNameValuePair("data", registerJson
                    .toString()));

            ResponseHandler<String> resHandler = new BasicResponseHandler();

            String paramString = URLEncodedUtils.format(namevaluepair, "utf-8");

            url += paramString;


            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            response = httpClient.execute(httpPost, resHandler);
            Log.d("DEBUG", "ServerCommunication - ValidateLogoAsyncTask Response: " +response);


        }
        catch (ClientProtocolException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("response", e.getMessage());
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void unused) {
        // NOTE: You can call UI Element here.

        // Close progress dialog
        //Dialog.dismiss();

        if (Error != null) {

            //uiUpdate.setText("Output : "+Error);

        } else {

            // Show Response Json On Screen (activity)
            //uiUpdate.setText( Content );

            /****************** Start Parse Response JSON Data *************/

            String OutputData = "";
            JSONObject obj;

            try {

                obj = new JSONObject(response);

                 //JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                /*********** Process each JSON Node ************/

                /*int lengthJsonArr = jsonMainNode.length();

                for(int i=0; i < lengthJsonArr; i++)
                {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    String name       = jsonChildNode.optString("name").toString();
                    String number     = jsonChildNode.optString("number").toString();
                    String date_added = jsonChildNode.optString("date_added").toString();



                }*/

                Log.d("RMD", obj.getString("messageCode"));


                //Log.d("RMD", obj.getString("message"));

                if(obj.getString("messageCode") == "R1001") {

                    //Intent dispOfferList = new Intent(context, DisplaySuccessActivity.class);
                    //dispOfferList.putExtra("unique_target_id", uniqueTargetId);
                    //startActivity(dispOfferList);
                    //finish();
                }

                //Show Parsed Output on screen (activity)
                //jsonParsed.setText( OutputData );


                /*Intent dispSuccess = new Intent(getApplicationContext(), DisplaySuccessActivity.class);
                dispSuccess.putExtra("unique_target_id", uniqueTargetId);
                startActivity(dispSuccess);
                finish();*/


            } catch (JSONException e) {

                e.printStackTrace();
            }


        }
    }

}
