package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.MyApplication;
import com.tier5.redeemar.RedeemarConsumerApp.database.DBOffers;
import com.tier5.redeemar.RedeemarConsumerApp.json.Endpoints;
import com.tier5.redeemar.RedeemarConsumerApp.json.Parser;
import com.tier5.redeemar.RedeemarConsumerApp.json.Requestor;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Windows on 02-03-2015.
 */
public class OfferUtils {
    private static final String LOGTAG = "OfferUtils";


    public static ArrayList<Object> loadBrandDetails(RequestQueue requestQueue, String targetId) {

        JSONObject response = Requestor.requestBrandDetailsJSON(requestQueue, Endpoints.getRequestUrlBrandDetails(), targetId);
        ArrayList<Object> brandInfo = Parser.parseBrandDetailsJSON(response);
        Log.d(LOGTAG, "Inside loadBrandDetails :"+brandInfo.size());
        return brandInfo;
    }

    public static ArrayList<Offer> loadBrowseOffers(RequestQueue requestQueue, String userId, String lat, String lng, String sLat, String sLng, String catId) {

        Log.d(LOGTAG, "Self Cat: "+catId);

        /* TODO: Use of sCatId not used now */
        JSONObject response = null;

        // Currently catLevel value is not used in web service
        if(!catId.equals(""))
            response = Requestor.requestOffersJSON(requestQueue, 3, Endpoints.getRequestUrlBrowseOffers(30), userId, catId, "0", lat, lng, sLat, sLng);
        else
            response = Requestor.requestOffersJSON(requestQueue, 0, Endpoints.getRequestUrlBrowseOffers(30), userId, "", "0", lat, lng, sLat, sLng);

        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);

        Log.d(LOGTAG, "Inside loadBrowseOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadBrandOffers(RequestQueue requestQueue, String redeemarId, String userId, String lat, String lng) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 1, Endpoints.getRequestUrlBrandOffers(30), userId, redeemarId, "0", lat, lng, lat, lng);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadBrandOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadCampaignOffers(RequestQueue requestQueue, String campaignId, String userId, String lat, String lng) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 2, Endpoints.getRequestUrlCamapignOffers(30), userId, campaignId, "0", lat, lng, lat, lng);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadCampaignOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadCategoryOffers(RequestQueue requestQueue, String catId, String catLevel, String userId, String lat, String lng, String selfLat, String selfLon) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 3, Endpoints.getRequestUrlBrandOffers(30), userId, catId, catLevel,  lat, lng, selfLat, selfLon);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadCategoryOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadOnDemandOffers(RequestQueue requestQueue, String userId, String lat, String lng, String selfLat, String selfLon) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 4, Endpoints.getRequestUrlBrandOffers(30), userId, "0", "0", lat, lng, selfLat, selfLon);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadOnDemandOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadMyOffers(RequestQueue requestQueue, String userId, String lat, String lon) {

        JSONObject response = Requestor.requestMyOffersJSON(requestQueue, Endpoints.getRequestUrlMyOffers(30), userId, lat, lon);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadMyOffers :"+listOffers.size());
        return listOffers;
    }

    public static ArrayList<User> loadNearByBrands(RequestQueue requestQueue, String lat, String lon) {

        JSONObject response = Requestor.requestNearByBrandsJSON(requestQueue, Endpoints.getRequestUrlNearByBrands(), lat, lon);
        ArrayList<User> listBrands = Parser.parseNearByBrandsJSON(response);
        Log.d(LOGTAG, "Inside loadNearByBrands :"+listBrands.size());
        return listBrands;
    }

    public static String loadSendFeedback(RequestQueue requestQueue, String email, String user_id, String feedback, String rating) {

        JSONObject response = Requestor.requestSendFeedbackJSON(requestQueue, Endpoints.getRequestUrlSendFeedback(), email, user_id, feedback, rating);
        String res = Parser.parseSendFeedbackJSON(response);
        Log.d(LOGTAG, "Inside loadSendFeedback :"+res);
        return res;
    }

    public static String loadUpdateProfile(RequestQueue requestQueue, String user_id, String first_name, String last_name, String email, String phone) {

        JSONObject response = Requestor.requestUpdateProfileJSON(requestQueue, Endpoints.getRequestUrlUpdateProfile(), user_id, first_name, last_name, email, phone);
        String res = Parser.parseUpdateProfileJSON(response);
        Log.d(LOGTAG, "Inside loadUpdateProfile :"+res);
        return res;
    }

    public static String loadValidatePassCode(RequestQueue requestQueue, String user_id, String offer_id, String pass_code) {

        JSONObject response = Requestor.requestValidatePassCodeJSON(requestQueue, Endpoints.getValidatePasscode(), user_id, offer_id, pass_code);
        String res = Parser.parseValidatePasscodeJSON(response);
        Log.d(LOGTAG, "Inside loadValidatePassCode :"+res);
        return res;
    }


    public static String loadSearchFull(RequestQueue requestQueue, String location, String keyword) {

        JSONObject response = Requestor.requestSearchFullJSON(requestQueue, Endpoints.getValidatePasscode(), location, keyword);
        String res = Parser.parseSearchFullJSON(response);
        Log.d(LOGTAG, "Inside loadSearchFull :"+res);
        return res;
    }

    public static String loadSearchShort(RequestQueue requestQueue, String location, String keyword) {

        JSONObject response = Requestor.requestSearchFullJSON(requestQueue, Endpoints.getValidatePasscode(), location, keyword);
        String res = Parser.parseSearchFullJSON(response);
        Log.d(LOGTAG, "Inside loadSearchFull :"+res);
        return res;
    }
    public static ArrayList<User> loadSearchLocation(RequestQueue requestQueue, String location) {

        JSONObject response = Requestor.requestSearchLocationJSON(requestQueue, Endpoints.getLocations(), location);
        ArrayList<User> locations = Parser.parseSearchLocationJSON(response);
        Log.d(LOGTAG, "Inside loadSearchLocation :"+locations.size());
        return locations;
    }
}

