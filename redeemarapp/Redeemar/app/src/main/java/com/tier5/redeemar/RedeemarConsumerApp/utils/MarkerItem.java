package com.tier5.redeemar.RedeemarConsumerApp.utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by tier5 on 20/6/16.
 */
public class MarkerItem implements ClusterItem {
    private String title;
    private String snippet;
    private LatLng latLng;
    private BitmapDescriptor icon;

    public MarkerItem(MarkerOptions markerOptions) {
        this.latLng = markerOptions.getPosition();
        this.title = markerOptions.getTitle();
        this.snippet = markerOptions.getSnippet();
        this.icon = markerOptions.getIcon();
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }
}