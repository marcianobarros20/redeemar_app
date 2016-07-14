package com.tier5.redeemar.RedeemarConsumerApp.fragments;

/**
 * Created by tier5 on 30/6/16.
 */


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;

import java.util.ArrayList;

public class AddressDetailsFragment extends Fragment {
    private TextView mStreet;
    private TextView mTown;

    public AddressDetailsFragment() {
        // Required empty public constructor
    }

    public static AddressDetailsFragment newInstance(Address venue) {
        AddressDetailsFragment fragment = new AddressDetailsFragment();
        fragment.setRetainInstance(true);
        Bundle b = new Bundle();
        b.putSerializable("address", venue);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.map_details_view, container, false);

        Bundle args = getArguments();
        Address address = null;
        if (args.containsKey("address")) {
            address = (Address) args.getSerializable("address");
        }

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // Showing status
        if (status == ConnectionResult.SUCCESS) {
            {
                /*FragmentManager fm = getFragmentManager();
                MyMapFragment mMapFragment = MyMapFragment.newInstance(address.getCoordinates());
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.my_map_fragment, mMapFragment);
                fragmentTransaction.commit();
                fm.executePendingTransactions();*/
            }
        }
        mStreet = (TextView) v.findViewById(R.id.address_details_street);
        mTown = (TextView) v.findViewById(R.id.address_details_town);
        mStreet.setText(address.getStreet());
        mTown.setText(address.getTown());

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        //outState.putParcelableArrayList(STATE_OFFERS, mListOffers);
    }
}