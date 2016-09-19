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
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.BrandVideo;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.MyItem;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
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

        Log.d(LOGTAG, "Inside parseOffersJSON "+response);
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<Offer> listOffers = new ArrayList<>();
        int respLen = response.length();
        if (response != null && respLen > 0) {
            try {

                if (response.getString("messageCode").equals("R01001")) {

                    JSONArray offersArray = new JSONArray(response.getString("data"));
                    Log.d(LOGTAG, "Data Length: " + offersArray.length());
                    int offerCnt =  offersArray.length();

                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < offerCnt; i++) {

                        JSONObject jsonObject = offersArray.getJSONObject(i);

                        Offer offer = new Offer();


                        if(!jsonObject.isNull("id") && jsonObject.optString("id").toString() != "") {
                            offer.setOfferId(jsonObject.optString("id").toString());
                            //Log.d(LOGTAG, "Id: "+jsonObject.optString("id").toString());
                        }

                        if(!jsonObject.isNull("created_by") && jsonObject.optString("created_by").toString() != "") {
                            offer.setCreatedBy(jsonObject.getInt("created_by"));
                            //Log.d(LOGTAG, "Created By: "+jsonObject.getInt("created_by"));
                        }

                        if(!jsonObject.isNull("offer_description") && jsonObject.getString("offer_description").toString() != "") {
                            offer.setOfferDescription(jsonObject.optString("offer_description").toString());
                            //Log.d(LOGTAG, "Offer Description: "+jsonObject.optString("offer_description").toString());
                        }
                        else {
                            offer.setOfferDescription("");
                        }

                        if(!jsonObject.isNull("what_you_get") && jsonObject.getString("what_you_get").toString() != "") {
                            offer.setWhatYouGet(jsonObject.optString("what_you_get").toString());
                            //Log.d(LOGTAG, "What you get: "+jsonObject.optString("what_you_get").toString());
                        }
                        else {
                            offer.setWhatYouGet("");
                        }


                        if(!jsonObject.isNull("more_information") && jsonObject.getString("more_information").toString() != "") {
                            offer.setMoreInformation(jsonObject.optString("more_information").toString());
                            //Log.d(LOGTAG, "More Info: "+jsonObject.optString("more_information").toString());
                        }
                        else {
                            offer.setMoreInformation("");
                        }

                        if(!jsonObject.isNull("retails_value") && jsonObject.getString("retails_value").toString() != "") {
                            offer.setRetailvalue(Double.parseDouble(jsonObject.getString("retails_value").toString()));
                            //Log.d(LOGTAG, "retails_value: "+jsonObject.getString("retails_value").toString());
                        }
                        else {
                            offer.setPayValue(0);
                        }


                        if(!jsonObject.isNull("pay_value") && jsonObject.getString("pay_value").toString() != "") {
                            offer.setPayValue(Double.parseDouble(jsonObject.getString("pay_value").toString()));
                            //Log.d(LOGTAG, "pay_value: "+jsonObject.getString("pay_value").toString());
                        }
                        else {
                            offer.setPayValue(0);
                        }

                        if(!jsonObject.isNull("discount") && jsonObject.getString("discount").toString() != "") {

                            offer.setDiscount(Double.parseDouble(jsonObject.getString("discount").toString()));
                            //Log.d(LOGTAG, "discount: "+jsonObject.getString("discount").toString());
                        }
                        else {
                            offer.setDiscount(0);
                        }


                        if(!jsonObject.isNull("value_calculate") && jsonObject.getString("value_calculate").toString() != "") {
                            offer.setValueCalculate(Integer.parseInt(jsonObject.getString("value_calculate").toString()));
                            //Log.d(LOGTAG, "value_calculate: "+jsonObject.getString("value_calculate").toString());
                        }
                        else {
                            offer.setValueCalculate(0);
                        }



                        if(!jsonObject.isNull("value_text") && jsonObject.getString("value_text").toString() != "") {
                            offer.setValueText(Integer.parseInt(jsonObject.getString("value_text")));
                            //Log.d(LOGTAG, "value_text: "+jsonObject.getString("value_text").toString());
                        }
                        else {
                            offer.setValueText(1);
                        }



                        if(!jsonObject.isNull("expires") && jsonObject.getString("expires").toString() != "") {
                            offer.setExpiredInDays(Integer.parseInt(jsonObject.getString("expires")));
                            //Log.d(LOGTAG, "expires: "+jsonObject.getString("expires").toString());
                        }
                        else
                            offer.setExpiredInDays(100);



                        if(!jsonObject.isNull("location") && jsonObject.getString("location").toString() != "") {
                            offer.setLocation(jsonObject.getString("location"));
                            //Log.d(LOGTAG, "distance: "+jsonObject.getString("distance"));
                        }
                        else
                            offer.setLocation("");


                        if(!jsonObject.isNull("distance") && jsonObject.getString("distance").toString() != "") {
                            offer.setDistance(jsonObject.getString("distance"));
                            //Log.d(LOGTAG, "distance: "+jsonObject.getString("distance").toString());
                        }
                        else {
                            offer.setDistance("");
                        }


                        //Log.d(LOGTAG, "Inside distance....");


                        if(!jsonObject.isNull("offer_image_path") && jsonObject.getString("offer_image_path").toString() != "") {
                            offer.setImageUrl(jsonObject.getString("offer_image_path"));
                            //Log.d(LOGTAG, "offer_image_path: "+jsonObject.getString("offer_image_path").toString());
                        }

                        if(!jsonObject.isNull("offer_large_image_path") && jsonObject.getString("offer_large_image_path").toString() != "") {
                            offer.setLargeImageUrl(jsonObject.getString("offer_large_image_path"));
                            //Log.d(LOGTAG, "offer_image_path: "+jsonObject.getString("offer_image_path").toString());
                        }

                        if(!jsonObject.isNull("on_demand") && jsonObject.getString("on_demand").toString() != "") {
                            offer.setOnDemand(Integer.parseInt(jsonObject.getString("on_demand")));
                            //Log.d(LOGTAG, "On Demand: "+jsonObject.getString("on_demand").toString());
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



            } catch (JSONException e) {

                Log.d(LOGTAG, "Exception: "+e.toString());

            }

        }
        return listOffers;
    }




    public static ArrayList<User> parseNearByBrandsJSON(JSONObject response) {

        Log.d(LOGTAG, "Inside parseNearByBrandsJSON");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        storePics.add("http://media.openwifispots.com/LocationMedia/Standard_23082.jpg");
        storePics.add("http://media.peaslovecarrots.com/LocationMedia/Standard_38950.jpg");
        storePics.add("http://www.visit-vermont.com/adservimage/6809.jpg");
        storePics.add("https://s-media-cache-ak0.pinimg.com/236x/77/e0/02/77e00273ce8ce92a032a14f1cb33632b.jpg");
        storePics.add("http://www.chicagotraveler.com/sites/default/files/sutton-place-ext_A.jpg");
        storePics.add("http://garycameradigital.com/media/Store_front.png");
        storePics.add("https://allaccessbranson.com/images/listings/742393_115258.jpg");
        storePics.add("http://downtownmanhattanks.com/wp-content/uploads/2011/01/Celebrations-resized-150x120.jpg");
        storePics.add("https://insideretail.asia/wp-content/uploads/2015/07/Hollys-Coffee-Vietnam-150x120.jpg");
        storePics.add("http://www.pripsjamaica.com/sites/default/files/styles/thumbnail_big/public/images/places/the-houseboat-grill-jamaica.jpg?itok=DRGvlRfL");
        storePics.add("https://photoremedy.files.wordpress.com/2015/03/gd-c-palace-chicory-coffee-ext-1j.jpg?w=150&h=120&crop=1");
        storePics.add("http://www.pripsjamaica.com/sites/default/files/styles/thumbnail_big/public/images/places/13710636_1086403191413996_637515536003171530_o.jpg?itok=R8xjso7A");
        storePics.add("http://www.pripsjamaica.com/sites/default/files/styles/thumbnail_big/public/images/places/the_wine_shop.jpg?itok=Nf4_mp0G");
        storePics.add("https://insideretail.asia/wp-content/uploads/2013/08/giantmalaysia-150x120.jpg");
        storePics.add("https://www.travelvietnam.com/images/Golf-Can-Tho-Hotel-1.jpg");
        storePics.add("https://insideretail.asia/wp-content/uploads/2013/08/mcdonaldsstore-150x120.jpg");
        storePics.add("https://insideretail.asia/wp-content/uploads/2015/07/Country-Style-cooking-restaurant-150x120.png");


        foods.add("http://www.happylifestyletips.com/wp-content/uploads/2014/04/fried-fast-foods-150x150.jpg");
        foods.add("http://youngwomenshealth.org/wp-content/uploads/2014/02/fast-food-150x150.jpg");
        foods.add("http://www.wonderslist.com/wp-content/uploads/2015/08/fast-food-during-pregnancy-150x150.jpg");
        foods.add("http://www.naturalhealth365.com/wp-content/uploads/2016/02/fast-food-chicken-150x150.jpg");
        foods.add("http://www.besthealthmag.ca/wp-content/uploads/2016/01/fast-food-150x150.jpg");
        foods.add("http://www.girlsfash.com/wp-content/uploads/2014/11/disadvantages-of-fast-food-jpg-3-150x150.jpg");
        foods.add("http://www.weightlossdietwatch.com/wp-content/uploads/2015/09/myths-about-fast-food-debunked-150x150.jpg");
        foods.add("http://www.happylifestyletips.com/wp-content/uploads/2014/04/fried-fast-foods-150x150.jpg");
        foods.add("https://s-media-cache-ak0.pinimg.com/236x/dd/07/63/dd07631e8a2769b74e88806bfac2c47c.jpg");
        foods.add("http://healthytastysnack.com/wp-content/uploads/2016/03/Healthy-Tasty-Fast-Food-150x150.jpg");
        foods.add("http://ihateworkinginretail.ooid.com/wp-content/uploads/2014/12/MENS_Grossest-Things-Fast-Food_01_01_4491569287_851d02a09c_z-150x150.jpg");
        foods.add("http://holicoffee.com/wp-content/uploads/2014/05/queso-fundido-pizzas-best-healthy-weight-loss-calories-diet-fast-food-tip1-150x150.jpg");
        foods.add("http://www.happylifestyletips.com/wp-content/uploads/2014/04/fried-fast-foods-150x150.jpg");
        foods.add("http://youngwomenshealth.org/wp-content/uploads/2014/02/fast-food-150x150.jpg");
        foods.add("http://www.wonderslist.com/wp-content/uploads/2015/08/fast-food-during-pregnancy-150x150.jpg");
        foods.add("http://www.naturalhealth365.com/wp-content/uploads/2016/02/fast-food-chicken-150x150.jpg");
        foods.add("http://www.besthealthmag.ca/wp-content/uploads/2016/01/fast-food-150x150.jpg");




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



                            brand.setStoreFrontImage(storePics.get(p).toString());
                            brand.setStoreBrandPic(foods.get(p).toString());

                            if (!jsonObject.isNull("profile")) {


                                JSONObject profileObject = new JSONObject(jsonObject.getString("profile"));

                                if (!profileObject.isNull("logo_name") && profileObject.getString("logo_name").trim() != "") {

                                    brand.setLogoName(UrlEndpoints.basePathURL + "" + profileObject.getString("logo_name"));
                                    //Log.d(LOGTAG, "Cluster Logo: " + profileObject.getString("logo_name"));

                                }


                                if (!profileObject.isNull("target_id") && profileObject.getString("target_id").trim() != "") {

                                    brand.setTargetId(profileObject.getString("target_id"));
                                    //Log.d(LOGTAG, "Cluster Traget Id: " + profileObject.getString("target_id"));

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


    public static String parseSearchFullJSON(JSONObject response) {

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

}
