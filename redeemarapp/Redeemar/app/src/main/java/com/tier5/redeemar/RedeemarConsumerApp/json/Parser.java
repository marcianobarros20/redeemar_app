package com.tier5.redeemar.RedeemarConsumerApp.json;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tier5.redeemar.RedeemarConsumerApp.DisplayFailureActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadImageTask;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.BrandVideo;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.MyItem;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
import com.tier5.redeemar.RedeemarConsumerApp.utils.MarkerItem;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.tier5.redeemar.RedeemarConsumerApp.utils.Keys.EndpointOffers.KEY_OFFERS;



public class Parser {

    private static final String LOGTAG = "Parser";

    public static ArrayList<Object> parseBrandDetailsJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseBrandDetailsJSON");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        ArrayList<Object> listBrand = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();


                if (response.getString("messageCode").equals("R01001")) {

                    Log.d(LOGTAG, "Message Code: " + response.getString("messageCode"));
                    Log.d(LOGTAG, "Message Data: " + response.getString("data"));

                    JSONObject json2 = new JSONObject(response.getString("data"));


                    User user = new User();

                    String redeemerId = "", companyName = "", defaultLogoImage = "", logoImage = "";

                    if(!json2.isNull("companyName")){
                        companyName = (String) json2.get("companyName");
                        companyName = companyName.trim();

                        Log.d(LOGTAG, "Company: "+companyName);
                    }


                    if(!json2.isNull("reedemer_id")){
                        redeemerId = (String) json2.get("reedemer_id");
                    }

                    if(!json2.isNull("default_logo")){
                        defaultLogoImage = (String) json2.get("default_logo");
                    }



                    if(!json2.isNull("logoImage")){
                        logoImage = (String) json2.get("logoImage");
                    }

                    user.setCompanyName(companyName);
                    if(defaultLogoImage.equals(""))
                        user.setLogoName(logoImage);
                    else
                        user.setLogoName(defaultLogoImage);

                    user.setId(redeemerId);


                    listBrand.add(user);



                    JSONArray videosArray = json2.optJSONArray("videoList");

                    ArrayList<BrandVideo> videos = new ArrayList();


                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < videosArray.length(); i++) {
                        JSONObject jsonObject = videosArray.getJSONObject(i);


                        String videoId = jsonObject.optString("video_id").toString();
                        String videoThumb = jsonObject.optString("video_thumb").toString();
                        String videoUrl = jsonObject.optString("video_url").toString();
                        String provider = jsonObject.optString("provider").toString();


                        Log.d(LOGTAG, "BrandVideo Id: " + videoId);
                        Log.d(LOGTAG, "BrandVideo Thumb: " + videoThumb);
                        Log.d(LOGTAG, "BrandVideo URL: " + videoUrl);
                        Log.d(LOGTAG, "Provider: " + provider);


                        BrandVideo video  = new BrandVideo();

                        video.setVideoId(videoId);
                        video.setVideoProvider(provider);
                        video.setVideothumb(videoThumb);
                        video.setVideoUrl(videoUrl);

                        //videos.add(video);

                        listBrand.add(video);



                    } // End of for loop for videos
                } // End of if
                else if (response.getString("messageCode").equals("R01002")) {



                }

                else if (response.getString("messageCode").equals("R01003")) {


                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
        return listBrand;
    }



    public static ArrayList<Offer> parseOffersJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseOffersJSON "+response);
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<Offer> listOffers = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {

                if (response.getString("messageCode").equals("R01001")) {

                    //Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                    JSONArray offersArray = new JSONArray(response.getString("data"));

                    Log.d(LOGTAG, "Data Length: " + offersArray.length());


                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < offersArray.length(); i++) {
                        JSONObject jsonObject = offersArray.getJSONObject(i);



                        Offer offer = new Offer();


                        if(!jsonObject.isNull("id") && jsonObject.optString("id").toString() != "") {
                            offer.setOfferId(jsonObject.optString("id").toString());
                            //Log.d(LOGTAG, "offer_id: "+jsonObject.optString("id").toString());
                        }

                        if(!jsonObject.isNull("offer_description")) {
                            offer.setOfferDescription(jsonObject.optString("offer_description").toString());
                            //Log.d(LOGTAG, "Offer Description: "+jsonObject.optString("offer_description").toString());
                        }

                        if(!jsonObject.isNull("retails_value")) {
                            offer.setRetailvalue(Double.parseDouble(jsonObject.getString("retails_value").toString()));
                            //Log.d(LOGTAG, "retails_value: "+jsonObject.getString("retails_value").toString());
                        }
                        else {
                            offer.setPayValue(0);
                        }


                        if(!jsonObject.isNull("pay_value")) {
                            offer.setPayValue(Double.parseDouble(jsonObject.getString("pay_value").toString()));
                            //Log.d(LOGTAG, "pay_value: "+jsonObject.getString("pay_value").toString());
                        }
                        else {
                            offer.setPayValue(0);
                        }

                        if(!jsonObject.isNull("discount")) {

                            offer.setDiscount(Double.parseDouble(jsonObject.getString("discount").toString()));
                            //Log.d(LOGTAG, "discount: "+jsonObject.getString("discount").toString());
                        }
                        else {
                            offer.setDiscount(0);
                        }


                        if(!jsonObject.isNull("value_calculate")) {
                            offer.setValueCalculate(Integer.parseInt(jsonObject.getString("value_calculate").toString()));
                            //Log.d(LOGTAG, "value_calculate: "+jsonObject.getString("value_calculate").toString());
                        }
                        else {
                            offer.setValueCalculate(0);
                        }


                        if(!jsonObject.isNull("expires")) {
                            offer.setExpiredInDays(Integer.parseInt(jsonObject.getString("expires")));
                            //Log.d(LOGTAG, "expires: "+jsonObject.getString("expires").toString());
                        }


                        if(!jsonObject.isNull("distance")) {
                            offer.setDistance(jsonObject.getString("distance"));
                            //Log.d(LOGTAG, "distance: "+jsonObject.getString("distance"));
                        }
                        else {
                            offer.setDistance("");
                        }

                        if(!jsonObject.isNull("offer_image_path") && jsonObject.getString("offer_image_path").toString() != "") {
                            offer.setImageUrl(jsonObject.getString("offer_image_path"));
                            //Log.d(LOGTAG, "offer_image_path: "+jsonObject.getString("offer_image_path").toString());
                        }

                        if(!jsonObject.isNull("on_demand") && jsonObject.getString("on_demand").toString() != "") {
                            offer.setOnDemand(Integer.parseInt(jsonObject.getString("on_demand")));
                            Log.d(LOGTAG, "On Demand: "+jsonObject.getString("on_demand").toString());
                        }


                        JSONObject json_partner_settings = new JSONObject(jsonObject.getString("partner_settings"));


                        if(!json_partner_settings.isNull("price_range_id")) {
                            offer.setPriceRangeId(json_partner_settings.getString("price_range_id").toString());
                            //Log.d(LOGTAG, "price_range_id: "+json_partner_settings.getString("price_range_id").toString());

                        }
                        else
                            offer.setPriceRangeId("");




                        listOffers.add(offer);


                    } // End of for loop for videos



                } // End of if



            } catch (JSONException e) {

                Log.d(LOGTAG, "Exception: "+e.toString());

            }

        }
        return listOffers;
    }




    public static ArrayList<User> parseNearByBrandsJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseNearByBrandsJSON");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<User> listBrands = new ArrayList<>();
        if (response != null && response.length() > 0) {


            try {

                if (response.getString("messageCode").equals("R01001")) {

                    JSONArray offersArray = new JSONArray(response.getString("data"));

                    //Log.d(LOGTAG, "Data Length: " + offersArray.length());


                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < offersArray.length(); i++) {
                        JSONObject jsonObject = offersArray.getJSONObject(i);


                        double lat = 0.0, lng = 0.0;

                        User brand = new User();


                        if (jsonObject.optString("id") != null && jsonObject.optString("id") != "") {
                            lat = Double.parseDouble(jsonObject.optString("id"));
                            brand.setId(jsonObject.optString("id"));
                            //Log.d(LOGTAG, "Cluster Id: "+jsonObject.optString("id"));
                        }

                        if (jsonObject.optString("company_name") != null && jsonObject.optString("company_name") != "") {
                            brand.setCompanyName(jsonObject.optString("company_name"));
                            Log.d(LOGTAG, "Cluster Company Name: "+jsonObject.optString("company_name"));
                        }

                        if (jsonObject.optString("address") != null && jsonObject.optString("address") != "") {
                            brand.setAddress(jsonObject.optString("address"));
                            //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                        }


                        if (jsonObject.optString("lat") != null && jsonObject.optString("lat") != "") {
                            lat = Double.parseDouble(jsonObject.optString("lat"));
                            brand.setLat(jsonObject.optString("lat"));
                            //Log.d(LOGTAG, "Cluster Lat: "+jsonObject.optString("lat"));
                        }

                        if (jsonObject.optString("lng") != null && jsonObject.optString("lng") != "") {
                            lng = Double.parseDouble(jsonObject.optString("lng"));
                            brand.setLon(jsonObject.optString("lng"));
                            //Log.d(LOGTAG, "Cluster Lon: "+jsonObject.optString("lng"));
                        }


                        //if (jsonObject.optString("profile") != null && jsonObject.optString("profile") != "") {
                        if (!jsonObject.isNull("profile")) {


                            JSONObject profileObject = new JSONObject(jsonObject.getString("profile"));

                            if (profileObject.getString("logo_name") != null && profileObject.getString("logo_name") != "") {

                                brand.setLogoName(UrlEndpoints.basePathURL + "" + profileObject.getString("logo_name"));
                                //Log.d(LOGTAG, "Cluster Logo: " + profileObject.getString("logo_name"));

                            }


                            if (profileObject.getString("target_id") != null && profileObject.getString("target_id") != "") {

                                brand.setTargetId(profileObject.getString("target_id"));
                                //Log.d(LOGTAG, "Cluster Traget Id: " + profileObject.getString("target_id"));

                            }

                        }


                        listBrands.add(brand);




                    } // End of if


                }

            } catch(JSONException e) {

                Log.d(LOGTAG, "JSONException occured inside parseNearByBrandsJSON. Details: "+e.toString());

            }
        }
        return listBrands;
    }

    public static ArrayList<Category> parseMenuItemsJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseMenuItemsJSON");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        ArrayList<Category> listMenus = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();


                if (response.getString("messageCode").equals("R01001")) {

                    Log.d(LOGTAG, "Message Code: " + response.getString("messageCode"));
                    Log.d(LOGTAG, "Message Data: " + response.getString("data"));

                    JSONObject json2 = new JSONObject(response.getString("data"));


                    JSONArray categoriesArray = json2.optJSONArray("categories");

                    Log.d(LOGTAG, "Category List: " + categoriesArray.length());



                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        JSONObject jsonObject = categoriesArray.getJSONObject(i);


                        Category category = new Category();

                        Log.d(LOGTAG, "Category Name: " + jsonObject.optString("cat_name").toString());
                        Log.d(LOGTAG, "Parent Id: " + jsonObject.optString("parent_id").toString());



                        category.setId(jsonObject.optString("id").toString());
                        category.setParentId(jsonObject.optString("parent_id").toString());
                        category.setCatName(jsonObject.optString("cat_name").toString());
                        category.setStatus(jsonObject.optString("status").toString());
                        category.setVisibility(jsonObject.optString("visibility").toString());

                        listMenus.add(category);

                        JSONArray subCategoriesArray = jsonObject.optJSONArray("children");

                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int j = 0; j < subCategoriesArray.length(); j++) {
                            JSONObject jsonObject2 = categoriesArray.getJSONObject(i);


                            Category subcategory = new Category();

                            Log.d(LOGTAG, "-- Sub Category Name: " + jsonObject2.optString("cat_name").toString());
                            Log.d(LOGTAG, "-- Parent Id: " + jsonObject2.optString("parent_id").toString());



                            subcategory.setId(jsonObject2.optString("id").toString());
                            subcategory.setParentId(jsonObject2.optString("parent_id").toString());
                            subcategory.setCatName(jsonObject2.optString("cat_name").toString());
                            subcategory.setStatus(jsonObject2.optString("status").toString());
                            subcategory.setVisibility(jsonObject2.optString("visibility").toString());

                            listMenus.add(subcategory);


                        } // End of for loop for categories


                    } // End of for loop for categories
                } // End of if


            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
        return listMenus;
    }



    public static String parseSendFeedbackJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseSendFeedbackJSON");

        String msgCode = "";


        if (response != null && response.length() > 0) {
            try {
                //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();

                if(!response.isNull("messageCode")) {

                    Log.d(LOGTAG, "Message Code: " + response.getString("messageCode"));
                    msgCode = response.getString("messageCode");
                }


            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
        return msgCode;
    }


    public static String parseUpdateProfileJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseSUpdateProfileJSON");

        String msgCode = "";


        if (response != null && response.length() > 0) {
            try {

                if(!response.isNull("messageCode")) {

                    Log.d(LOGTAG, "Message Code: " + response.getString("messageCode"));
                    msgCode = response.getString("messageCode");
                }


            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
        return msgCode;
    }
}
