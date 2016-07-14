package com.tier5.redeemar.RedeemarConsumerApp.callbacks;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;

import java.util.ArrayList;

/**
 * Created by Windows on 02-03-2015.
 */
public interface OffersLoadedListener {
    public void onOffersLoaded(ArrayList<Offer> listOffers);
}
