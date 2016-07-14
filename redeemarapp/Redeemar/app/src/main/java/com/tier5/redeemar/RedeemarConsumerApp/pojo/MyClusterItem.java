package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by tier5 on 16/6/16.
 */
public class MyClusterItem implements ClusterItem {

    private final LatLng mPosition;
    private BitmapDescriptor icon;

    public MyClusterItem(double lat, double lng, BitmapDescriptor icon) {
        mPosition = new LatLng(lat, lng);
        setIcon(icon);

    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

}
