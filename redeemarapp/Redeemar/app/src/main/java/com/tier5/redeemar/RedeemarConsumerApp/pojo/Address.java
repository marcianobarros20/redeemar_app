package com.tier5.redeemar.RedeemarConsumerApp.pojo;

/**
 * Created by tier5 on 30/6/16.
 */
import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Address implements Serializable {
    private String street;
    private String town;
    private String city;
    private String state;
    private String zip;
    private String location;
    private LatLng coordinates;

    public Address() {
        super();
    }


    public Address(String street, String town, LatLng coordinates) {
        super();
        this.street = street;
        this.town = town;
        this.coordinates = coordinates;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

}