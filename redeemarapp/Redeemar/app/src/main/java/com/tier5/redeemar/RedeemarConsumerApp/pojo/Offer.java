package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import static android.os.UserHandle.readFromParcel;

public class Offer implements Parcelable {

    private static final long serialVersionUID = 1L;

    private String offerId, offerDescription, whatYouGet, moreInformation, settingsVal, priceRangeId, imageName, address, imageUrl,  largeImageUrl, distance, companyName,  location, zipcode, catName, subcatName, brandLogo, brandInfo, campaignName, latitude, longitude;
    Date startDate, endDate;
    double price, payValue, retailvalue, discount;
    int valueCalculate, valueText, expiredInDays, campaignId, catId, subCatId, userAction, createdBy, onDemand, offersCount, dealsCount;

    public Offer() {

    }

    public Offer(Parcel in) {
        readFromParcel(in);
    }


    public String getCampaignName() {
        return campaignName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getBrandInfo() {
        return brandInfo;
    }

    public void setBrandInfo(String brandInfo) {
        this.brandInfo = brandInfo;
    }

    public int getOffersCount() {
        return offersCount;
    }

    public void setOffersCount(int offersCount) {
        this.offersCount = offersCount;
    }

    public int getDealsCount() {
        return dealsCount;
    }

    public int getValueText() {
        return valueText;
    }

    public void setValueText(int valueText) {
        this.valueText = valueText;
    }

    public void setDealsCount(int dealsCount) {
        this.dealsCount = dealsCount;
    }

    public int getCreatedBy() {
        return createdBy;
    }


    public String getCatName() {
        return catName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(offerDescription);
        dest.writeInt(campaignId);

    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Offer createFromParcel(Parcel in) {
                    return new Offer(in);
                }

                public Offer[] newArray(int size) {
                    return new Offer[size];
                }
            };
}
