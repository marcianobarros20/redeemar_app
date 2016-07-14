package com.tier5.redeemar.RedeemarConsumerApp.fragments;

/**
 * Created by tier5 on 22/6/16.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tier5.redeemar.RedeemarConsumerApp.DividerItemDecoration;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrowseOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;

import org.json.JSONArray;

import java.util.ArrayList;

public class NearMeFragment extends SupportMapFragment implements OffersLoadedListener {

    private static final String LOGTAG = "BrowseOfferFragment";

    //The key used to store arraylist of movie objects to and from parcelable
    private static final String STATE_OFFERS = "state_offers";
    private ArrayList<Offer> mListOffers;
    private BrowseOffersViewAdapter mAdapter;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;
    //the arraylist containing our list of box office his
    private String redeemerId = "";
    private JSONArray offersArray;
    private Resources res;
    private SharedPreferences sharedpref;
    private String user_id = "";
    double latitude = 0.0, longitude = 0.0;
    //private FragmentActivity listener;

    static final LatLng TutorialsPoint = new LatLng(21 , 57);



    public NearMeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NearMeFragment newInstance(String param1, String param2) {
        NearMeFragment fragment = new NearMeFragment();
        Bundle args = new Bundle();
        //put any extra arguments that you may want to supply to this fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_nearme, container, false);
        mRecyclerOffers = (RecyclerView) layout.findViewById(R.id.my_recycler_view);



        GoogleMap googleMap = null;


        try {
            if (googleMap == null) {
                googleMap = ((SupportMapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Marker TP = googleMap.addMarker(new MarkerOptions().
                    position(TutorialsPoint).title("TutorialsPoint"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        return layout;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        //outState.putParcelableArrayList(STATE_OFFERS, mListOffers);
    }




    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        res = getResources();
        sharedpref = getActivity().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        SharedPreferences.Editor editor = sharedpref.edit();

        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
            user_id = sharedpref.getString(res.getString(R.string.spf_user_id), "0");
        }

        Log.d(LOGTAG, "Browse Offer User Id: "+user_id);




        // create class object
        GPSTracker gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            if(latitude == 0 && longitude == 0) {

                gps.showSettingsAlert();

            }

            // \n is for new line
            Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }



        Log.d(LOGTAG, "Lat Values: "+latitude);
        Log.d(LOGTAG, "Long Values: "+longitude);


        mListOffers = new ArrayList<Offer>();
        //mAdapter = new BrowseOffersViewAdapter(getActivity(), mDataSet, "BrowseOffers");
    }




    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {


        Log.d(LOGTAG, "Inside callback onOffersLoaded: "+listOffers.size());
        mAdapter = new BrowseOffersViewAdapter(getActivity(), listOffers, "BrowseOffers");
        mRecyclerOffers.setAdapter(mAdapter);
        mRecyclerOffers.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);

    }
}
