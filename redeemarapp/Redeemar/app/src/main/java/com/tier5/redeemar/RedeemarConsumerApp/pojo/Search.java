package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.io.Serializable;
import java.util.Date;

public class Search implements Serializable {

    private String  resDescription, resCategory;

    public Search() {

    }

    public String getResDescription() {
        return resDescription;
    }

    public void setResDescription(String resDescription) {
        this.resDescription = resDescription;
    }

    public String getResCategory() {
        return resCategory;
    }

    public void setResCategory(String resCategory) {
        this.resCategory = resCategory;
    }
}
