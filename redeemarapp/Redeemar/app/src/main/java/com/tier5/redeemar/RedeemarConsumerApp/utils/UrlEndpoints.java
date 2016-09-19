package com.tier5.redeemar.RedeemarConsumerApp.utils;

/**
 * Created by Tier5 on 10-02-2015.
 * */
public class UrlEndpoints {


    public static final String serverBaseUrl = "http://dev.redeemar.com/";

    public static final String basePathURL             = serverBaseUrl+"admin/uploads/original/";
    public static final String URL_ALL_OFFERS          =  serverBaseUrl+"admin/public/index.php/bridge/alloffers";
    public static final String URL_MY_OFFERS           =  serverBaseUrl+"admin/public/index.php/bridge/myoffer";
    public static final String URL_NEARBY_BRANDS       =  serverBaseUrl+"admin/public/index.php/bridge/mapalloffers";
    public static final String URL_BRAND_DETAILS       =  serverBaseUrl+"admin/public/index.php/bridge/checktarget";
    public static final String URL_BRAND_OFFERS        =  serverBaseUrl+"admin/public/index.php/bridge/offerlist";
    public static final String URL_MENU_ITEMS          =  serverBaseUrl+"admin/public/index.php/bridge/multicat";
    public static final String URL_CATEGORY_ITEMS      =  serverBaseUrl+"admin/public/index.php/bridge/categories";
    public static final String URL_SEND_FEEDBACK       =  serverBaseUrl+"admin/public/index.php/bridge/sendfeedback";
    public static final String URL_UPDATE_PROFILE      =  serverBaseUrl+"admin/public/index.php/bridge/updateprofile";
    public static final String URL_VALIDATE_PASSCODE   =  serverBaseUrl+"admin/public/index.php/bridge/redeempasscode";
    public static final String URL_SEARCH_SHORT        =  serverBaseUrl+"admin/public/index.php/bridge/searchshort";
    public static final String URL_SEARCH_FULL         =  serverBaseUrl+"admin/public/index.php/bridge/searchfull";
    public static final String URL_SEARCH_LOCATION     =  serverBaseUrl+"admin/public/index.php/bridge/locations";
    public static final String baseLogoLargeURL        = serverBaseUrl+"filemanager/userfiles/large/";
    public static final String baseLogoMediumURL       = serverBaseUrl+"filemanager/userfiles/medium/";
    public static final String baseLogoSmallURL        = serverBaseUrl+"filemanager/userfiles/small/";
    public static final String validateLogoURL         = serverBaseUrl+"admin/public/index.php/bridge/checktarget?";
    public static final String getOfferListURL         = serverBaseUrl+"admin/public/index.php/bridge/offerlist?";
    public static final String getOfferDetailsURL      = serverBaseUrl+"admin/public/index.php/bridge/offerdetail?";
    public static final String loginURL                = serverBaseUrl+"admin/public/index.php/bridge/userlogin?";
    public static final String registerSocialURL       = serverBaseUrl+"admin/public/index.php/bridge/socialsignup?";
    public static final String registerURL             = serverBaseUrl+"admin/public/index.php/bridge/userregister?";
    public static final String browseOffersURL         = serverBaseUrl+"admin/public/index.php/bridge/alloffers?";
    public static final String mapOffersURL            = serverBaseUrl+"admin/public/index.php/bridge/mapalloffers?";
    public static final String bankOffersURL           = serverBaseUrl+"admin/public/index.php/bridge/bankoffer?";
    public static final String passOffersURL           = serverBaseUrl+"admin/public/index.php/bridge/passoffer?";
    public static final String myOffersURL             = serverBaseUrl+"admin/public/index.php/bridge/myoffer?";
    public static final String validateOfferDetailsURL = serverBaseUrl+"admin/public/index.php/bridge/validateofferdetail?";
    public static final String redemptionURL           = serverBaseUrl+"admin/public/index.php/bridge/redeemption?";
    public static final String URL_CHAR_QUESTION       = "?";
    public static final String URL_CHAR_AMEPERSAND     = "&";
    public static final String URL_PARAM_API_KEY       = "apikey=";
    public static final String URL_PARAM_LIMIT         = "limit=";
}