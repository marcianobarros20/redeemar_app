package com.tier5.redeemar.RedeemarConsumerApp.fragments;

/**
 * Created by tier5 on 22/6/16.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BankedOffersAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.MyOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Banked;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Brand;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.SuperConnectionDetector;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MyOfferFragment extends Fragment implements OffersLoadedListener {

    private static final String LOGTAG = "BrowseOfferFragment";

    //The key used to store arraylist of movie objects to and from parcelable
    private static final String STATE_OFFERS = "state_offers";
    private ArrayList<Offer> mListOffers;
    //private BankedOfferAdapter adapter;
    private BankedOffersAdapter adapter;
    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;
    //the arraylist containing our list of box office his
    private String redeemerId = "";
    private JSONArray offersArray;
    private Resources res;
    private SharedPreferences sharedpref;
    private String user_id = "";
    double latitude = 0.0, longitude = 0.0;
    private SuperConnectionDetector cd;
    private boolean isInternetPresent = false;



    public MyOfferFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyOfferFragment newInstance(String param1, String param2) {
        MyOfferFragment fragment = new MyOfferFragment();
        Bundle args = new Bundle();
        //put any extra arguments that you may want to supply to this fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreate(savedInstanceState);

        View layout = inflater.inflate(R.layout.fragment_banked, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        mRecyclerOffers = (RecyclerView) layout.findViewById(R.id.main_recycler);


        cd = new SuperConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();

        if(!isInternetPresent) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Internet");
            alertDialog.setMessage("Internet not enabled in your device. Do you want to enable it from settings menu");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 1);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();

        }



        /*adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {

            @Override
            public void onListItemExpanded(int position) {
                Brand expandedRecipe = recipes.get(position);

                String toastMsg = "This is a test message";
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onListItemCollapsed(int position) {
                Brand collapsedRecipe = recipes.get(position);

                String toastMsg = getResources().getString(R.string.collapsed, "Hello");
                Toast.makeText(getActivity(),
                        toastMsg,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });*/

        //mRecyclerOffers.setAdapter(adapter);
        //adapter = new BankedOfferAdapter(getActivity());

        //adapter = new BankedOffersAdapter();
        //adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);

        mRecyclerOffers.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerOffers.setAdapter(adapter);


        tvEmptyView = (TextView) layout.findViewById(R.id.empty_view);
        tvEmptyView.setVisibility(View.VISIBLE);

        mRecyclerOffers.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListOffers = new ArrayList<>();

        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            //mListOffers = savedInstanceState.getParcelableArrayList(STATE_OFFERS);

            Log.d(LOGTAG, "Inside savedInstanceState");
        } else {
            //if this fragment starts for the first time, load the list of movies from a database
            //mListOffers = MyApplication.getWritableDatabase().readOffers(DBOffers.ALL_OFFERS);
            //if the database is empty, trigger an AsycnTask to download movie list from the web

            Log.d(LOGTAG, "List Offers Before Async: "+mListOffers.isEmpty());
            if (mListOffers.isEmpty()) {
                new MyOffersAsyncTask(this, getActivity().getApplicationContext()).execute(user_id, String.valueOf(latitude),  String.valueOf(longitude));
            }
        }
        //update your Adapter to containg the retrieved movies
        //mAdapter.setOffers(mListOffers);
        return layout;
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
            Log.d(LOGTAG, "User Id is: "+user_id);
        }

        else {
            Log.d(LOGTAG, "No user id found, redirecting to login");
            Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(i);
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
            //Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }



        Log.d(LOGTAG, "Lat Values: "+latitude);
        Log.d(LOGTAG, "Long Values: "+longitude);


        mListOffers = new ArrayList<Offer>();
    }




    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {

        if(listOffers.size() > 0) {
            Log.d(LOGTAG, "Inside callback onMyOffersLoaded: " + listOffers.size());
            //adapter.setOffers(listOffers);
            //mRecyclerOffers.setVisibility(View.VISIBLE);


            if (listOffers != null) {

                Log.d(LOGTAG, "Offer lists now: " + listOffers.size());
                int tempRedeemarId = 0;
                int countBanked = 0;
                String dst = "";
                int c = 0;
                int d = 0;

                List<Brand> brands = new ArrayList();
                //List<Banked> ingredients = new ArrayList();

                // This ArrayList is required to get all counters of banked offers
                ArrayList<Integer> listBanked = new ArrayList();
                ArrayList<Integer> groupOffer = new ArrayList();


                for (int p = 0; p < listOffers.size(); p++) {

                    List<Banked> ingredients = new ArrayList();

                    countBanked = 0;
                    final Offer item = listOffers.get(p);

                    int mRedeemarId = item.getCreatedBy();

                    if (mRedeemarId != tempRedeemarId) {
                        //countBanked++;


                        if (item.getDistance().equals(""))
                            dst = "NA";
                        else
                            dst = item.getDistance();

                        for (int q = 0; q < listOffers.size(); q++) {

                            final Offer item1 = listOffers.get(q);


                            if(mRedeemarId == item1.getCreatedBy()) {

                                Banked ingr = new Banked();
                                ingr.setOfferId(item1.getOfferId());
                                ingr.setOfferDesc(item1.getOfferDescription());
                                ingr.setImageUrl(item1.getImageUrl());
                                ingr.setLocationVal(item1.getLocation());
                                ingr.setPayVal(item1.getPayValue());
                                ingr.setRetailVal(item1.getRetailvalue());
                                ingr.setValCalc(item1.getValueCalculate());
                                ingr.setValText(item1.getValueText());
                                ingr.setOnDemand(item1.getOnDemand());
                                ingr.setExpires(item1.getExpiredInDays());
                                ingredients.add(ingr);
                                Log.d(LOGTAG, "Count Ingredients: " + ingredients.size());
                                countBanked++;

                            }
                        }

                        Brand recp = new Brand();
                        recp.setBrandInfo(item.getBrandInfo());
                        recp.setBrandLogo(item.getBrandLogo());
                        recp.setCompanyName(item.getCompanyName());
                        recp.setCountOffers(item.getOffersCount());
                        recp.setCountOnDemand(item.getDealsCount());
                        recp.setCountBankedOffers(countBanked);
                        recp.setDistanceVal(dst);
                        recp.setmBankeds(ingredients);
                        brands.add(recp);
                        //ingredients.clear();
                        d++;
                    }
                    Log.d(LOGTAG, "My Redeemar Temp Id: " + tempRedeemarId);
                    Log.d(LOGTAG, "My Redeemar Id: " + mRedeemarId);

                    groupOffer.add(d);
                    tempRedeemarId = mRedeemarId;
                }

                adapter = new BankedOffersAdapter(getActivity(), brands);
                mRecyclerOffers.setAdapter(adapter);
                mRecyclerOffers.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);

            }
            else
                tvEmptyView.setText(getString(R.string.no_records));
        }
        else
            tvEmptyView.setText(getString(R.string.no_records));
    }
}
