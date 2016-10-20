package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by tier5 on 20/10/16.
 */
public class OfferParcel implements Parcelable {

    private ArrayList<Offer> items;

    public OfferParcel() {

    }


    public int describeContents() {
        return 0;
    }

    public ArrayList<Offer> getItems() {
        return items;
    }

    public void setItems(ArrayList<Offer> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return items.toString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("KEY_PARCEL_OFFERS", items);
        dest.writeBundle(b);

    }

    public static final Parcelable.Creator<OfferParcel> CREATOR =
            new Parcelable.Creator<OfferParcel>() {
                public OfferParcel createFromParcel(Parcel in) {
                    OfferParcel offer = new OfferParcel();
                    Bundle b = in.readBundle(Offer.class.getClassLoader());
                    offer.items = b.getParcelableArrayList("items");

                    return offer;
                }

                @Override
                public OfferParcel[] newArray(int size) {
                    return new OfferParcel[size];
                }
            };

}
