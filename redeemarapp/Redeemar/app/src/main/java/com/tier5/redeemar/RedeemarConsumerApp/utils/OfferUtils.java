package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.json.Endpoints;
import com.tier5.redeemar.RedeemarConsumerApp.json.Parser;
import com.tier5.redeemar.RedeemarConsumerApp.json.Requestor;
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

    public static ArrayList<Offer> loadBrowseOffers(RequestQueue requestQueue, String userId, String lat, String lng) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 0, Endpoints.getRequestUrlOffers(30), userId, "0", "0", lat, lng);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadBrowseOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadBrandOffers(RequestQueue requestQueue, String redeemarId, String userId, String lat, String lng) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 1, Endpoints.getRequestUrlBrandOffers(30), userId, redeemarId, "0", lat, lng);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadBrandOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadCampaignOffers(RequestQueue requestQueue, String campaignId, String userId, String lat, String lng) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 2, Endpoints.getRequestUrlCamapignOffers(30), userId, campaignId, "0", lat, lng);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadCampaignOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<Offer> loadCategoryOffers(RequestQueue requestQueue, String catId, String catLevel, String userId, String lat, String lng) {

        JSONObject response = Requestor.requestOffersJSON(requestQueue, 3, Endpoints.getRequestUrlBrandOffers(30), userId, catId, catLevel,  lat, lng);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadCategoryOffers :"+listOffers.size());
        return listOffers;
    }

    public static ArrayList<Offer> loadMyOffers(RequestQueue requestQueue, String userId) {

        JSONObject response = Requestor.requestMyOffersJSON(requestQueue, Endpoints.getRequestUrlMyOffers(30), userId);
        ArrayList<Offer> listOffers = Parser.parseOffersJSON(response);
        Log.d(LOGTAG, "Inside loadBrowseOffers :"+listOffers.size());
        return listOffers;
    }


    public static ArrayList<User> loadNearByBrands(RequestQueue requestQueue, String lat, String lon) {

        JSONObject response = Requestor.requestNearByBrandsJSON(requestQueue, Endpoints.getRequestUrlNearByBrands(), lat, lon);
        ArrayList<User> listBrands = Parser.parseNearByBrandsJSON(response);
        Log.d(LOGTAG, "Inside loadNearByBrands :"+listBrands.size());
        return listBrands;
    }
}
