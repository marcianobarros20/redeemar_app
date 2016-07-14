package com.tier5.redeemar.RedeemarConsumerApp.json;

/**
 * Created by tier5 on 5/7/16.
 */
import android.util.Log;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.MyItem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyItemReader {

    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

    public List read(InputStream inputStream) throws JSONException {

        List items = new ArrayList();
        String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {

            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            Log.d("Dibs", "Lat is: "+lat+" Lng is: "+lng);
            items.add(new MyItem(lat, lng));
        }
        return items;

    }
}