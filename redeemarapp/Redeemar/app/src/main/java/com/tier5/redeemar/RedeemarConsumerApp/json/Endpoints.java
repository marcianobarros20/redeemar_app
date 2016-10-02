package com.tier5.redeemar.RedeemarConsumerApp.json;

import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

/**
 * Created by Windows on 02-03-2015.
 */
public class Endpoints {


    public static String getRequestUrlBrandDetails() {

        return UrlEndpoints.URL_BRAND_DETAILS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getRequestUrlBrandOffers(int limit) {

        return UrlEndpoints.URL_BRAND_OFFERS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getRequestUrlCamapignOffers(int limit) {

        return UrlEndpoints.URL_BRAND_OFFERS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getRequestUrlBrowseOffers(int limit) {

        return UrlEndpoints.URL_BRAND_OFFERS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getRequestUrlOffers(int limit) {

        return UrlEndpoints.URL_ALL_OFFERS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getRequestUrlMyOffers(int limit) {

        return UrlEndpoints.URL_MY_OFFERS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getRequestUrlNearByBrands() {

        return UrlEndpoints.URL_NEARBY_BRANDS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getRequestUrlMenuItems() {

        return UrlEndpoints.URL_MENU_ITEMS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getRequestUrlCategoryItems() {

        return UrlEndpoints.URL_CATEGORY_ITEMS
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getRequestUrlSendFeedback() {

        return UrlEndpoints.URL_SEND_FEEDBACK
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getRequestUrlUpdateProfile() {

        return UrlEndpoints.URL_UPDATE_PROFILE
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getValidatePasscode() {

        return UrlEndpoints.URL_VALIDATE_PASSCODE
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getSearchShort() {

        return UrlEndpoints.URL_SEARCH_SHORT
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getSearchFull() {

        return UrlEndpoints.URL_SEARCH_FULL
                + UrlEndpoints.URL_CHAR_QUESTION;
    }

    public static String getLocations() {

        return UrlEndpoints.URL_SEARCH_LOCATION
                + UrlEndpoints.URL_CHAR_QUESTION;
    }


    public static String getBeacon() {

        return UrlEndpoints.URL_SEARCH_BEACON
                + UrlEndpoints.URL_CHAR_QUESTION;
    }
}
