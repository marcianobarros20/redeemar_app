package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.io.Serializable;

public class Banked {

    private String offerId, offerDesc, locationVal, imageUrl;
    private double payVal, retailVal;
    private int valCalc, valText, onDemand, expires, maxItems;

    public Banked() {

    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferDesc() {
        return offerDesc;
    }

    public void setOfferDesc(String offerDesc) {
        this.offerDesc = offerDesc;
    }

    public String getLocationVal() {
        return locationVal;
    }

    public void setLocationVal(String locationVal) {
        this.locationVal = locationVal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPayVal() {
        return payVal;
    }

    public void setPayVal(double payVal) {
        this.payVal = payVal;
    }

    public double getRetailVal() {
        return retailVal;
    }

    public void setRetailVal(double retailVal) {
        this.retailVal = retailVal;
    }

    public int getValCalc() {
        return valCalc;
    }

    public void setValCalc(int valCalc) {
        this.valCalc = valCalc;
    }

    public int getValText() {
        return valText;
    }

    public void setValText(int valText) {
        this.valText = valText;
    }

    public int getOnDemand() {
        return onDemand;
    }

    public void setOnDemand(int onDemand) {
        this.onDemand = onDemand;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
