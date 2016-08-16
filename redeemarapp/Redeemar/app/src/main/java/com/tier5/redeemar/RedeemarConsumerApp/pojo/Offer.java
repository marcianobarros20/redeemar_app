package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.io.Serializable;
import java.util.Date;

public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String offerId, offerDescription, whatYouGet, moreInformation, settingsVal, priceRangeId, imageName, address, imageUrl,  largeImageUrl, distance, companyName,  location, zipcode, catName, subcatName;
    Date startDate, endDate;
    double price, payValue, retailvalue, discount;
    int valueCalculate, expiredInDays, campaignId, catId, subCatId, userAction, createdBy, onDemand;

    public Offer() {

    }

    public int getCreatedBy() {
        return createdBy;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getSubcatName() {
        return subcatName;
    }

    public void setSubcatName(String subcatName) {
        this.subcatName = subcatName;
    }

    public String getCompanyName() {
        return companyName;

    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getCatId() {
        return catId;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    public int getUserAction() {
        return userAction;
    }

    public void setUserAction(int userAction) {
        this.userAction = userAction;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public int getExpiredInDays() {
        return expiredInDays;
    }

    public void setExpiredInDays(int expiredInDays) {
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getOnDemand() {
        return onDemand;
    }

    public void setOnDemand(int onDemand) {
        this.onDemand = onDemand;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
