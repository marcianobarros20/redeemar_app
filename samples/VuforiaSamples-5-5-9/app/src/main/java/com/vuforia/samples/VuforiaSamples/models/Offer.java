package com.vuforia.samples.VuforiaSamples.models;

import java.io.Serializable;
import java.util.Date;

public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String offerDescription, whatYouGet, moreInformation, settingsVal, priceRangeId;
    Date startDate, endDate;
    double price, payValue, retailvalue, discount, expiredInDays;
    int valueCalculate;

    public Offer() {

    }

    public double getExpiredInDays() {
        return expiredInDays;
    }

    public void setExpiredInDays(double expiredInDays) {
        this.expiredInDays = expiredInDays;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getWhatYouGet() {
        return whatYouGet;
    }

    public void setWhatYouGet(String whatYouGet) {
        this.whatYouGet = whatYouGet;
    }

    public String getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(String moreInformation) {
        this.moreInformation = moreInformation;
    }

    public String getSettingsVal() {
        return settingsVal;
    }

    public void setSettingsVal(String settingsVal) {
        this.settingsVal = settingsVal;
    }

    public String getPriceRangeId() {
        return priceRangeId;
    }

    public void setPriceRangeId(String priceRangeId) {
        this.priceRangeId = priceRangeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getValueCalculate() {
        return valueCalculate;
    }

    public void setValueCalculate(int valueCalculate) {
        this.valueCalculate = valueCalculate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public double getRetailvalue() {
        return retailvalue;
    }

    public void setRetailvalue(double retailvalue) {
        this.retailvalue = retailvalue;
    }


    public double getPayValue() {
        return payValue;
    }

    public void setPayValue(double payValue) {
        this.payValue = payValue;
    }


}
