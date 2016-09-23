package com.tier5.redeemar.RedeemarConsumerApp.callbacks;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.Search;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

import java.util.ArrayList;

/**
 * Created by Windows on 02-03-2015.
 */
public interface SearchLoadedListener {
    public void onSearchLoaded(ArrayList<Search> listOffers);
}
