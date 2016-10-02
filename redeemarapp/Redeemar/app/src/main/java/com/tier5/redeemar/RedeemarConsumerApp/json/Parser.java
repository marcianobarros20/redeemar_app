package com.tier5.redeemar.RedeemarConsumerApp.json;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadSaveImageAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Beacon;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.BrandVideo;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.MyItem;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Search;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
import com.tier5.redeemar.RedeemarConsumerApp.utils.MarkerItem;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



public class Parser {

    private static final String LOGTAG = "Parser";
    private static final ArrayList storePics = new ArrayList<String>();
    private static final ArrayList foods = new ArrayList<String>();

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


                        //Log.d(LOGTAG, "BrandVideo Id: " + videoId);
                        //Log.d(LOGTAG, "BrandVideo Thumb: " + videoThumb);
                        //Log.d(LOGTAG, "BrandVideo URL: " + videoUrl);
                        //Log.d(LOGTAG, "Provider: " + provider);


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

        //Log.d(LOGTAG, "Inside parseOffersJSON "+response);
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<Offer> listOffers = new ArrayList<>();


        if (response != null) {
            try {

                int respLen = response.length();

                if(respLen > 0) {

                    if (response.getString("messageCode").equals("R01001")) {

                        JSONArray offersArray = new JSONArray(response.getString("data"));
                        //Log.d(LOGTAG, "Data Length: " + offersArray.length());
                        int offerCnt =  offersArray.length();

                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int i = 0; i < offerCnt; i++) {

                            JSONObject jsonObject = offersArray.getJSONObject(i);

                            Offer offer = new Offer();


                            if(!jsonObject.isNull("id") && jsonObject.optString("id").toString() != "") {
                                offer.setOfferId(jsonObject.optString("id").toString());
                            }

                            if(!jsonObject.isNull("created_by") && jsonObject.optString("created_by").toString() != "") {
                                offer.setCreatedBy(jsonObject.getInt("created_by"));
                            }

                            if(!jsonObject.isNull("offer_description") && jsonObject.getString("offer_description").toString() != "") {
                                offer.setOfferDescription(jsonObject.optString("offer_description").toString());
                            }
                            else {
                                offer.setOfferDescription("");
                            }

                            if(!jsonObject.isNull("what_you_get") && jsonObject.getString("what_you_get").toString() != "") {
                                offer.setWhatYouGet(jsonObject.optString("what_you_get").toString());
                            }
                            else {
                                offer.setWhatYouGet("");
                            }


                            if(!jsonObject.isNull("more_information") && jsonObject.getString("more_information").toString() != "") {
                                offer.setMoreInformation(jsonObject.optString("more_information").toString());
                            }
                            else {
                                offer.setMoreInformation("");
                            }

                            if(!jsonObject.isNull("retails_value") && jsonObject.getString("retails_value").toString() != "") {
                                offer.setRetailvalue(Double.parseDouble(jsonObject.getString("retails_value").toString()));
                            }
                            else {
                                offer.setPayValue(0);
                            }


                            if(!jsonObject.isNull("pay_value") && jsonObject.getString("pay_value").toString() != "") {
                                offer.setPayValue(Double.parseDouble(jsonObject.getString("pay_value").toString()));
                            }
                            else {
                                offer.setPayValue(0);
                            }

                            if(!jsonObject.isNull("discount") && jsonObject.getString("discount").toString() != "") {

                                offer.setDiscount(Double.parseDouble(jsonObject.getString("discount").toString()));
                            }
                            else {
                                offer.setDiscount(0);
                            }


                            if(!jsonObject.isNull("value_calculate") && jsonObject.getString("value_calculate").toString() != "") {
                                offer.setValueCalculate(Integer.parseInt(jsonObject.getString("value_calculate").toString()));
                            }
                            else {
                                offer.setValueCalculate(0);
                            }



                            if(!jsonObject.isNull("value_text") && jsonObject.getString("value_text").toString() != "") {
                                offer.setValueText(Integer.parseInt(jsonObject.getString("value_text")));
                            }
                            else {
                                offer.setValueText(1);
                            }



                            if(!jsonObject.isNull("expires") && jsonObject.getString("expires").toString() != "") {
                                offer.setExpiredInDays(Integer.parseInt(jsonObject.getString("expires")));
                            }
                            else
                                offer.setExpiredInDays(100);

                            if(!jsonObject.isNull("distance") && jsonObject.getString("distance").toString() != "") {
                                offer.setDistance(jsonObject.getString("distance"));
                            }
                            else {
                                offer.setDistance("");
                            }

                            if(!jsonObject.isNull("offer_image_path") && jsonObject.getString("offer_image_path").toString() != "") {
                                offer.setImageUrl(jsonObject.getString("offer_image_path"));
                            }

                            if(!jsonObject.isNull("offer_large_image_path") && jsonObject.getString("offer_large_image_path").toString() != "") {
                                offer.setLargeImageUrl(jsonObject.getString("offer_large_image_path"));
                            }

                            if(!jsonObject.isNull("on_demand") && jsonObject.getString("on_demand").toString() != "") {
                                offer.setOnDemand(Integer.parseInt(jsonObject.getString("on_demand")));
                            }

                            offer.setPriceRangeId("");

                            if(!jsonObject.isNull("logo_details") && !jsonObject.getString("logo_details").equalsIgnoreCase("")) {

                                JSONObject jsonLogoSettings = new JSONObject(jsonObject.getString("logo_details"));

                                if(!jsonLogoSettings.isNull("logo_name")) {
                                    offer.setBrandLogo(jsonLogoSettings.getString("logo_name").toString());
                                    String logoUrl = UrlEndpoints.baseLogoMediumURL + jsonLogoSettings.getString("logo_name");
                                    //Log.d(LOGTAG, "My Logo URL 1: "+logoUrl);
                                    Bitmap myBitmap = BitmapFactory.decodeFile(logoUrl);
                                    Utils.saveToInternalStorage(myBitmap, jsonLogoSettings.getString("logo_name"));
                                }
                                else
                                    offer.setBrandLogo("");



                                if(!jsonLogoSettings.isNull("lat") && !jsonLogoSettings.getString("lat").equals("")) {
                                    offer.setLatitude(jsonLogoSettings.getString("lat").toString());
                                }
                                else
                                    offer.setLatitude("");


                                if(!jsonLogoSettings.isNull("lng") && !jsonLogoSettings.getString("lng").equals("")) {
                                    offer.setLongitude(jsonLogoSettings.getString("lng").toString());
                                }
                                else
                                    offer.setLongitude("");

                                if(!jsonLogoSettings.isNull("location") && !jsonLogoSettings.getString("location").toString().equals("")) {
                                    offer.setLocation(jsonLogoSettings.getString("location"));
                                    Log.d(LOGTAG, "Location: "+jsonLogoSettings.getString("location"));
                                }
                                else if(!jsonLogoSettings.isNull("city") && !jsonLogoSettings.getString("city").toString().equals("")) {
                                    offer.setLocation(jsonLogoSettings.getString("city"));
                                    Log.d(LOGTAG, "City: "+jsonLogoSettings.getString("city"));
                                }
                                else
                                    offer.setLocation("");
                            }
                            else {
                                offer.setBrandLogo("");
                            }



                            if(!jsonObject.isNull("campaign_details") && !jsonObject.getString("campaign_details").equalsIgnoreCase("")) {

                                JSONObject jsonCampaignSettings = new JSONObject(jsonObject.getString("campaign_details"));



                                if(!jsonCampaignSettings.isNull("campaign_name")) {
                                    offer.setCampaignName(jsonCampaignSettings.getString("campaign_name").toString());
                                }
                                else
                                    offer.setCampaignName("");

                            }
                            else {
                                offer.setCampaignName("");
                            }

                            if(!jsonObject.isNull("counters") && !jsonObject.getString("counters").equalsIgnoreCase("")) {

                                JSONObject jsonCounters = new JSONObject(jsonObject.getString("counters"));

                                if(!jsonCounters.isNull("offers_count") && !jsonCounters.equals("offers_count")) {
                                    offer.setOffersCount(jsonCounters.getInt("offers_count"));
                                }
                                else
                                    offer.setOffersCount(0);

                                if(!jsonCounters.isNull("deals_count")) {
                                    offer.setDealsCount(jsonCounters.getInt("deals_count"));
                                }
                                else
                                    offer.setDealsCount(0);

                            }




                            if(!jsonObject.isNull("company_detail") && !jsonObject.getString("company_detail").equalsIgnoreCase("")) {
                                JSONArray companyArray = new JSONArray(jsonObject.getString("company_detail"));

                                if(companyArray.length() > 0) {

                                    JSONObject companyJsonObject = companyArray.getJSONObject(0);

                                    if (!companyJsonObject.isNull("company_name")) {
                                        offer.setCompanyName(companyJsonObject.getString("company_name").toString());
                                        //Log.d(LOGTAG, "Company name: " + companyJsonObject.getString("company_name").toString());
                                    } else
                                        offer.setCompanyName("");

                                    if (!companyJsonObject.isNull("address")) {
                                        offer.setAddress(companyJsonObject.getString("address").toString());
                                        //Log.d(LOGTAG, "Address: " + companyJsonObject.getString("address").toString());
                                    } else
                                        offer.setAddress("");

                                    if (!companyJsonObject.isNull("zipcode")) {
                                        offer.setZipcode(companyJsonObject.getString("zipcode").toString());
                                        //Log.d(LOGTAG, "Zip Code: " + companyJsonObject.getString("zipcode").toString());
                                    } else
                                        offer.setZipcode("");
                                }

                            }


                            listOffers.add(offer);



                        } // End of for loop for videos



                    } // End of if


                }


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
        int p = 0;
        if (response != null && response.length() > 0) {


            try {

                if (response.getString("messageCode").equals("R01001")) {

                    JSONArray offersArray = new JSONArray(response.getString("data"));

                    //Log.d(LOGTAG, "Data Length: " + offersArray.length());


                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < offersArray.length(); i++) {
                        if(storePics.size()-1 <= i) {
                            p = 0;

                        }

                        JSONObject jsonObject = offersArray.getJSONObject(i);
                        double lat = 0.0, lng = 0.0;

                        User brand = new User();

                        if (!jsonObject.isNull("lat") && jsonObject.optString("lat").trim() != "" && !jsonObject.isNull("lng") && jsonObject.optString("lng").trim() != "") {


                            if (!jsonObject.isNull("id") && jsonObject.optString("id").trim() != "") {
                                lat = Double.parseDouble(jsonObject.optString("id"));
                                brand.setId(jsonObject.optString("id"));
                                //Log.d(LOGTAG, "Cluster Id: "+jsonObject.optString("id"));
                            }

                            if (!jsonObject.isNull("company_name") && jsonObject.optString("company_name").trim() != "") {
                                brand.setCompanyName(jsonObject.optString("company_name"));
                                //Log.d(LOGTAG, "Cluster Company Name: "+jsonObject.optString("company_name"));
                            }

                            if (!jsonObject.isNull("address") && jsonObject.optString("address") != "") {
                                brand.setAddress(jsonObject.optString("address"));
                                //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                            }

                            if (!jsonObject.isNull("city") && jsonObject.optString("city") != "") {
                                brand.setCity(jsonObject.optString("address"));
                                //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                            }

                            if (!jsonObject.isNull("state") && jsonObject.optString("state") != "") {
                                brand.setState(jsonObject.optString("state"));
                                //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                            }

                            if (!jsonObject.isNull("zipcode") && jsonObject.optString("zipcode") != "") {
                                brand.setZipcode(jsonObject.optString("zipcode"));
                                //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                            }


                            if (!jsonObject.isNull("location") && jsonObject.optString("location") != "") {
                                brand.setLocation(jsonObject.optString("location"));
                                //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                            }

                            if (!jsonObject.isNull("lat") && jsonObject.optString("lat").trim() != "") {
                                lat = Double.parseDouble(jsonObject.optString("lat"));
                                brand.setLat(jsonObject.optString("lat"));
                                //Log.d(LOGTAG, "Cluster Lat: "+jsonObject.optString("lat"));
                            }

                            if (!jsonObject.isNull("lng") && jsonObject.optString("lng").trim() != "") {
                                lng = Double.parseDouble(jsonObject.optString("lng"));
                                brand.setLon(jsonObject.optString("lng"));
                                //Log.d(LOGTAG, "Cluster Lon: "+jsonObject.optString("lng"));
                            }



                            //brand.setStoreFrontImage(storePics.get(p).toString());
                            //brand.setStoreBrandPic(foods.get(p).toString());

                            if (!jsonObject.isNull("profile")) {


                                JSONObject profileObject = new JSONObject(jsonObject.getString("profile"));

                                if (!profileObject.isNull("logo_name") && profileObject.getString("logo_name").trim() != "") {

                                    String logoFileName =   profileObject.getString("logo_name");
                                    String logoUrl = UrlEndpoints.basePathURL + "" + logoFileName;
                                    brand.setLogoName(UrlEndpoints.basePathURL + "" + profileObject.getString("logo_name"));

                                }


                                if (!profileObject.isNull("target_id") && profileObject.getString("target_id").trim() != "") {

                                    brand.setTargetId(profileObject.getString("target_id"));
                                    //Log.d(LOGTAG, "Cluster Traget Id: " + profileObject.getString("target_id"));

                                }

                            }

                            if (!jsonObject.isNull("store_image")) {


                                JSONObject storeImageObject = new JSONObject(jsonObject.getString("store_image"));

                                if (!storeImageObject.isNull("brand_image_path") && storeImageObject.getString("brand_image_path").trim() != "") {
                                    brand.setStoreBrandPic(UrlEndpoints.serverBaseUrl + "" + storeImageObject.getString("brand_image_path"));

                                    Log.d(LOGTAG, UrlEndpoints.serverBaseUrl + "" + storeImageObject.getString("brand_image_path"));
                                }


                                if (!storeImageObject.isNull("store_front_image_path") && storeImageObject.getString("store_front_image_path").trim() != "") {
                                    brand.setStoreFrontImage(UrlEndpoints.serverBaseUrl + "" + storeImageObject.getString("store_front_image_path"));
                                    Log.d(LOGTAG, UrlEndpoints.serverBaseUrl + "" + storeImageObject.getString("store_front_image_path"));

                                }

                            }

                            p++;


                            listBrands.add(brand);


                        }




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

                        category.setId(jsonObject.optInt("id"));
                        category.setParentId(jsonObject.optInt("parent_id"));
                        category.setCatName(jsonObject.optString("cat_name"));
                        category.setStatus(jsonObject.optInt("status"));
                        category.setVisibility(jsonObject.optInt("visibility"));

                        listMenus.add(category);

                        // Redundant section: will be removed in the next sprint

                        if(!jsonObject.isNull("children")) {

                            JSONArray subCategoriesArray = jsonObject.optJSONArray("children");

                            //Iterate the jsonArray and print the info of JSONObjects
                            for (int j = 0; j < subCategoriesArray.length(); j++) {
                                JSONObject jsonObject2 = categoriesArray.getJSONObject(i);

                                Category subcategory = new Category();

                                subcategory.setId(jsonObject2.optInt("id"));
                                subcategory.setParentId(jsonObject2.optInt("parent_id"));
                                subcategory.setCatName(jsonObject2.optString("cat_name").toString());
                                subcategory.setStatus(jsonObject2.optInt("status"));
                                subcategory.setVisibility(jsonObject2.optInt("visibility"));

                                listMenus.add(subcategory);


                            } // End of for loop for categories

                        }




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

        Log.d(LOGTAG, "Inside parseUpdateProfileJSON");

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


    public static String parseValidatePasscodeJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseValidatePasscodeJSON");

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



    public static String parseSearchShortJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseSearchFullJSON");

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


    public static ArrayList<Search> parseSearchFullJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseSearchFullJSON");

        ArrayList<Search> listSearch = new ArrayList<Search>();

        if (response != null && response.length() > 0) {
            try {

                if(!response.isNull("messageCode")) {

                    Log.d(LOGTAG, "Message Code: " + response.getString("messageCode"));

                    if (response.getString("messageCode").equals("R01001")) {

                        JSONObject json2 = new JSONObject(response.getString("data"));


                        if(!json2.isNull("mi") && !json2.isNull("ci")  && !json2.isNull("cn") ) {

                            JSONArray keywordArray = json2.optJSONArray("mi");
                            JSONArray catIdArray = json2.optJSONArray("ci");
                            JSONArray catNameArray = json2.optJSONArray("cn");

                            Log.d(LOGTAG, "Keywords List: " + keywordArray.length());


                            //Iterate the jsonArray and print the info of JSONObjects
                            for (int j = 0; j < keywordArray.length(); j++) {


                                if(catIdArray.get(j) != null && catNameArray.get(j) != null && keywordArray.get(j) != null) {

                                    Search search = new Search();

                                    Log.d(LOGTAG, "Keyword 1: " + keywordArray.get(j));
                                    Log.d(LOGTAG, "Keyword 1: " + catIdArray.get(j));
                                    Log.d(LOGTAG, "Keyword 1: " + catNameArray.get(j));

                                    search.setDescription(keywordArray.get(j).toString());
                                    search.setCategoryId(catIdArray.get(j).toString());
                                    search.setCategoryName(catNameArray.get(j).toString());



                                    listSearch.add(search);
                                }



                            } // End of for loop for categories

                        }



                    }




                }


            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
        return listSearch;
    }


    public static ArrayList<User> parseSearchLocationJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseSearchLocationJSON");
        int respLen = response.length();
        ArrayList<User> listLocations = new ArrayList<>();


        if (response != null && respLen > 0) {
            try {

                if (response.getString("messageCode").equals("R01001")) {

                    JSONArray locationArray = new JSONArray(response.getString("data"));


                    Log.d(LOGTAG, "Data Length: " + locationArray.length());

                    int offerCnt =  locationArray.length();


                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < offerCnt; i++) {

                        JSONObject jsonObject = locationArray.getJSONObject(i);

                        User addr = new User();

                        if(!jsonObject.isNull("id") && jsonObject.optString("id").toString() != "") {
                            addr.setId(jsonObject.optString("id"));
                            //Log.d(LOGTAG, "Id: "+jsonObject.optString("id").toString());
                        }

                        if(!jsonObject.isNull("city") && jsonObject.optString("city").toString() != "") {
                            addr.setCity(jsonObject.optString("city"));
                            //Log.d(LOGTAG, "City: "+jsonObject.optString("city"));
                        }
                        else
                            addr.setCity("");

                        if(!jsonObject.isNull("state") && jsonObject.optString("state").toString() != "") {
                            addr.setState(jsonObject.optString("state"));
                            //Log.d(LOGTAG, "State: "+jsonObject.optString("state"));
                        }
                        else
                            addr.setState("");

                        if(!jsonObject.isNull("zipcode") && jsonObject.getString("zipcode").toString() != "") {
                            addr.setZipcode(jsonObject.optString("zipcode").toString());
                            //Log.d(LOGTAG, "Zipcode: "+jsonObject.optString("zipcode").toString());
                        }
                        else {
                            addr.setZipcode("");
                        }

                        if(!jsonObject.isNull("lat") && jsonObject.getString("lat").toString() != "") {
                            addr.setLat(jsonObject.optString("lat").toString());
                            //Log.d(LOGTAG, "Lat: "+jsonObject.optString("lat").toString());
                        }
                        else {
                            addr.setLat("");
                        }

                        if(!jsonObject.isNull("lng") && jsonObject.getString("lng").toString() != "") {
                            addr.setLon(jsonObject.optString("lng").toString());
                            //Log.d(LOGTAG, "Lon: "+jsonObject.optString("lng").toString());
                        }
                        else {
                            addr.setLon("");
                        }

                        listLocations.add(addr);

                    } // End of for loop for videos



                } // End of if



            } catch (JSONException e) {

                Log.d(LOGTAG, "Exception: "+e.toString());

            }

        }

        return listLocations;
    }



    public static Beacon parseSearchBeaconJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseSearchLocationJSON");

        Beacon beacon = new Beacon();


        if (response != null) {
            try {

                if (response.getString("messageCode").equals("R01001")) {

                    //JSONArray beaconArray = new JSONArray(response.getString("data"));


                    //Log.d(LOGTAG, "Data Length: " + beaconArray.length());

                    //int cnt =  beaconArray.length();


                    //Iterate the jsonArray and print the info of JSONObjects

                    JSONObject jsonObject = new JSONObject(response.getString("data"));



                    if(!jsonObject.isNull("id") && jsonObject.optString("id").toString() != "") {
                        beacon.setId(jsonObject.optInt("id"));
                        Log.d(LOGTAG, "Id: "+jsonObject.optInt("id"));
                    }

                    if(!jsonObject.isNull("redeemar_id") && jsonObject.optString("redeemar_id").toString() != "") {
                        beacon.setRedeemarId(jsonObject.optInt("redeemar_id"));
                        //Log.d(LOGTAG, "City: "+jsonObject.optString("city"));
                    }
                    else
                        beacon.setRedeemarId(0);

                    if(!jsonObject.isNull("uuid") && jsonObject.optString("uuid").toString() != "") {
                        beacon.setUuid(jsonObject.optString("uuid"));
                        //Log.d(LOGTAG, "State: "+jsonObject.optString("state"));
                    }
                    else
                        beacon.setUuid("");

                    if(!jsonObject.isNull("color") && jsonObject.optString("color").toString() != "") {
                        beacon.setColor(jsonObject.optString("color").toString());
                        //Log.d(LOGTAG, "Zipcode: "+jsonObject.optString("zipcode").toString());
                    }
                    else {
                        beacon.setColor("");
                    }

                    if(!jsonObject.isNull("major") && jsonObject.optString("major").toString() != "") {
                        beacon.setMajor(jsonObject.optInt("major"));
                    }
                    else {
                        beacon.setMajor(0);
                    }

                    if(!jsonObject.isNull("minor") && jsonObject.optString("minor").toString() != "") {
                        beacon.setMinor(jsonObject.optInt("minor"));
                    }
                    else
                        beacon.setMinor(0);


                    if(!jsonObject.isNull("action_id") && jsonObject.optString("action_id").toString() != "") {
                        beacon.setActionId(jsonObject.optInt("action_id"));
                    }
                    else
                        beacon.setActionId(0);


                    if(!jsonObject.isNull("particular_id") && jsonObject.optString("particular_id").toString() != "") {
                        beacon.setParticularId(jsonObject.optInt("particular_id"));
                    }
                    else
                        beacon.setParticularId(0);



                } // End of if



            } catch (JSONException e) {

                Log.d(LOGTAG, "Exception: "+e.toString());

            }

        }

        return beacon;
    }

}
