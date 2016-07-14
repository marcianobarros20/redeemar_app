package com.tier5.redeemar.RedeemarConsumerApp.utils;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.json.Endpoints;
import com.tier5.redeemar.RedeemarConsumerApp.json.Parser;
import com.tier5.redeemar.RedeemarConsumerApp.json.Requestor;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Windows on 02-03-2015.
 */
public class MenuUtils {
    private static final String LOGTAG = "MenuUtils";


    public static ArrayList<Category> loadMenuItem(RequestQueue requestQueue, String parentId) {

        JSONObject response = Requestor.requestMenuItemsJSON(requestQueue, Endpoints.getRequestUrlMenuItems(), parentId);
        ArrayList<Category> menuItems = Parser.parseMenuItemsJSON(response);
        Log.d(LOGTAG, "Inside loadMenuItem :"+menuItems.size());
        return menuItems;
    }

}
