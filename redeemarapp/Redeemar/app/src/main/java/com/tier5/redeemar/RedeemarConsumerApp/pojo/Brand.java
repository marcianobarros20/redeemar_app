package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Brand implements ParentListItem {

    private String companyName, brandInfo, brandLogo, distanceVal;
    private int countOffers, countOnDemand, countBankedOffers;

    private List<Banked> mBankeds;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBrandInfo() {
        return brandInfo;
    }

    public void setBrandInfo(String brandInfo) {
        this.brandInfo = brandInfo;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getDistanceVal() {
        return distanceVal;
    }

    public void setDistanceVal(String distanceVal) {
        this.distanceVal = distanceVal;
    }

    public int getCountOffers() {
        return countOffers;
    }

    public void setCountOffers(int countOffers) {
        this.countOffers = countOffers;
    }

    public int getCountOnDemand() {
        return countOnDemand;
    }

    public void setCountOnDemand(int countOnDemand) {
        this.countOnDemand = countOnDemand;
    }

    public int getCountBankedOffers() {
        return countBankedOffers;
    }

    public void setCountBankedOffers(int countBankedOffers) {
        this.countBankedOffers = countBankedOffers;
    }

    public List<Banked> getmBankeds() {
        return mBankeds;
    }

    public void setmBankeds(List<Banked> mBankeds) {
        this.mBankeds = mBankeds;
    }

    @Override
    public List<?> getChildItemList() {
        return mBankeds;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
