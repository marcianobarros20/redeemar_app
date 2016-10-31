package com.tier5.redeemar.RedeemarConsumerApp.utils;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;

import java.util.Comparator;

/**
 * Created by tier5 on 31/10/16.
 */
public class CustomComparator implements Comparator<Offer> {
    @Override
    public int compare(Offer o1, Offer o2) {
        //return o1.getStartDate().compareTo(o2.getStartDate());
        return o1.getCreatedBy() - o2.getCreatedBy();
    }
}