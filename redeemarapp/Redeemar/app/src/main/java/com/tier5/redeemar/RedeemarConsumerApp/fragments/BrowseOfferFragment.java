package com.tier5.redeemar.RedeemarConsumerApp.fragments;

/**
 * Created by tier5 on 22/6/16.
 */

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.tier5.redeemar.RedeemarConsumerApp.DividerItemDecoration;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrandOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrowseOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.CampaignOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.CategoryOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;

import org.json.JSONArray;

import java.util.ArrayList;

public class BrowseOfferFragment extends Fragment implements OffersLoadedListener {

    private static final String LOGTAG = "BrowseOfferFragment";

    //The key used to store arraylist of movie objects to and from parcelable
    private static final String STATE_OFFERS = "state_offers";
    private ArrayList<Offer> mListOffers;
    private BrowseOffersViewAdapter mAdapter;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;
    //the arraylist containing our list of box office his
    //private String redeemerId = "";
    private JSONArray offersArray;
    private Resources res;
    private SharedPreferences sharedpref;
    private String user_id = "0";
    double latitude = 0.0, longitude = 0.0;
    private FragmentActivity listener;



    public BrowseOfferFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BrowseOfferFragment newInstance(String param1, String param2) {
        BrowseOfferFragment fragment = new BrowseOfferFragment();
        Bundle args = new Bundle();
        //put any extra arguments that you may want to supply to this fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_offers, container, false);


        tvEmptyView = (TextView) layout.findViewById(R.id.empty_view);
        mRecyclerOffers = (RecyclerView) layout.findViewById(R.id.my_recycler_view);

        //tvEmptyView.setText("Loading...");

        mRecyclerOffers.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Check if the Android version code is greater than or equal to Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //return resources.getDrawable(id, context.getTheme());
            mRecyclerOffers.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider, getActivity().getApplicationContext().getTheme())));
        } else {
            //return resources.getDrawable(id);
            mRecyclerOffers.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        }

        mListOffers = new ArrayList<>();



        mAdapter = new BrowseOffersViewAdapter(getActivity(), "BrowseOffers");
        mRecyclerOffers.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            //mListOffers = savedInstanceState.getParcelableArrayList(STATE_OFFERS);

            Log.d(LOGTAG, "Inside savedInstanceState");
        } else {
            //if this fragment starts for the first time, load the list of movies from a database
            //mListOffers = MyApplication.getWritableDatabase().readOffers(DBOffers.ALL_OFFERS);
            //if the database is empty, trigger an AsycnTask to download movie list from the web
            if (mListOffers.isEmpty()) {
                Log.d(LOGTAG, "FragmentBoxOffice: executing task from fragment");



                Bundle args1 = getArguments();
                String redirectTo = "", redeemarId = "", campaignId = "", categoryId = "";

                if(args1 != null && args1.size() > 0) {

                    redirectTo = args1.getString(getString(R.string.ext_redir_to), "");
                    redeemarId = args1.getString(getString(R.string.ext_redeemar_id), "");
                    campaignId = args1.getString(getString(R.string.ext_campaign_id), "");
                    categoryId = args1.getString(getString(R.string.ext_category_id), "");


                }

                Log.d(LOGTAG, "Redirect to 102: " + redirectTo);
                Log.d(LOGTAG, "Redeemar id 102: " + redeemarId);
                Log.d(LOGTAG, "Campaign id 102: " + campaignId);
                Log.d(LOGTAG, "Category id 102: " + categoryId);

                if(redirectTo.equals("BrandOffers") && !redeemarId.equals(""))
                    new BrandOffersAsyncTask(this).execute(redeemarId, user_id, String.valueOf(latitude), String.valueOf(longitude));
                else if(redirectTo.equals("CampaignOffers") && !campaignId.equals(""))
                    new CampaignOffersAsyncTask(this).execute(campaignId, user_id, String.valueOf(latitude), String.valueOf(longitude));
                else if(redirectTo.equals("CategoryOffers") && !categoryId.equals(""))
                    new CategoryOffersAsyncTask(this).execute(categoryId, user_id, String.valueOf(latitude), String.valueOf(longitude));
                else
                    new BrowseOffersAsyncTask(this).execute(user_id, String.valueOf(latitude),  String.valueOf(longitude));

            }
        }
        //update your Adapter to containg the retrieved movies
        mAdapter.setOffers(mListOffers);
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
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

    }
    @Override
    public void onResume() {
        super.onResume();

    }






    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {


        Log.d(LOGTAG, "Inside callback onOffersLoaded: "+listOffers.size());
        if(listOffers.size() > 0 && mAdapter != null) {
            mAdapter = new BrowseOffersViewAdapter(getActivity().getApplicationContext(), listOffers, "BrowseOffers");
            mRecyclerOffers.setAdapter(mAdapter);
            mRecyclerOffers.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
        else
            tvEmptyView.setText(getString(R.string.no_records));


    }
}
