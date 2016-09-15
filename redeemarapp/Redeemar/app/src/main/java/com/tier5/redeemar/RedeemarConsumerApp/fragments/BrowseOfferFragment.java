package com.tier5.redeemar.RedeemarConsumerApp.fragments;

/**
 * Created by tier5 on 22/6/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.tier5.redeemar.RedeemarConsumerApp.BrowseOffersActivity;
import com.tier5.redeemar.RedeemarConsumerApp.CategoryActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.SearchViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrandOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.BrowseOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.CampaignOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.CategoryOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.FetchLocationAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.GetNearByOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.OnDemandOffersAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ActivityCommunicator;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.LocationFetchedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Keys;
import com.tier5.redeemar.RedeemarConsumerApp.utils.SuperConnectionDetector;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BrowseOfferFragment extends Fragment implements OffersLoadedListener, UsersLoadedListener, LocationFetchedListener,  SearchView.OnQueryTextListener  {

    private static final String LOGTAG = "BrowseOfferFragment";
    private static final int LOCATION_SETTINGS_REQUEST = 1;

    //The key used to store arraylist of movie objects to and from parcelable
    private ArrayList<Offer> mListOffers;
    private BrowseOffersViewAdapter mAdapter;
    private SearchViewAdapter sAdapter;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;

    //the arraylist containing our list of box office his
    private JSONArray offersArray;
    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private String user_id = "0";
    private double latitude = 0.0, longitude = 0.0, selfLatitude = 0.0, selfLongitude = 0.0;
    //private FragmentActivity listener;
    private List<Offer> mModels;
    private String redirectTo = "", redeemarId = "", campaignId = "", categoryId = "", viewType = "list", location = "", categoryName = "", locKeyword = "",
            selfLat = "", selfLon = "";
    private static final int VERTICAL_ITEM_SPACE = 48;
    Activity activity;
    private ActivityCommunicator activityCommunicator;
    private ImageView imListView, imMapView, imThumbView;
    private TextView tvCategory;
    private AutoCompleteTextView autoComplete;
    private GPSTracker gps;
    private ArrayList<String> locationList;
    private ArrayList<LatLng> latLngList;
    private ArrayAdapter<String> autoCompleteAdapter;
    private SuperConnectionDetector cd;
    private boolean isInternetPresent = false;

    public BrowseOfferFragment() {
        // Required empty public constructor


    }

    public static BrowseOfferFragment newInstance(String param1) {
        BrowseOfferFragment fragment = new BrowseOfferFragment();
        fragment.setRetainInstance(true);
        Bundle b = new Bundle();
        b.putSerializable("offers", param1);
        fragment.setArguments(b);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOGTAG, "XXX Inside onCreateView");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.browse_offers);
        activity = getActivity();
        activityCommunicator = (ActivityCommunicator) activity;

        res = getResources();
        sharedpref = getActivity().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
            user_id = sharedpref.getString(res.getString(R.string.spf_user_id), "0");
            sharedpref.getString(res.getString(R.string.spf_first_name), "");
        }
        mListOffers = new ArrayList<Offer>();

        setHasOptionsMenu(true);
        Bundle args1 = getArguments();

        if(args1 != null && args1.size() > 0) {

            redirectTo = args1.getString(getString(R.string.ext_redir_to), "");
            redeemarId = args1.getString(getString(R.string.ext_redeemar_id), "");
            campaignId = args1.getString(getString(R.string.ext_campaign_id), "");
            categoryId = args1.getString(getString(R.string.ext_category_id), "");

            categoryName = args1.getString(getString(R.string.ext_category_name), "");
            viewType = args1.getString(getString(R.string.ext_view_type), "");

        }

        if(!categoryName.equals(""))
            ((BrowseOffersActivity) getActivity()).getSupportActionBar().setTitle(categoryName);

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_offers, container, false);

        layout.findViewById(R.id.mainOfferListLayout).requestFocus();

        imListView = (ImageView) layout.findViewById(R.id.menu_list_view);
        imMapView = (ImageView) layout.findViewById(R.id.menu_map_view);
        imThumbView = (ImageView) layout.findViewById(R.id.menu_thumb_view);
        tvCategory = (TextView) layout.findViewById(R.id.search_category);
        autoComplete = (AutoCompleteTextView) layout.findViewById(R.id.autoCompleteTextView1);
        locationList = new ArrayList<String>();
        latLngList = new ArrayList<LatLng>();

        if(imListView.getVisibility() == View.VISIBLE) {
            Log.d(LOGTAG, "Inside ListView");
            imListView.setVisibility(View.GONE);
            imThumbView.setVisibility(View.VISIBLE);
            imMapView.setVisibility(View.GONE);
        }
        else if(imThumbView.getVisibility() == View.VISIBLE) {
            Log.d(LOGTAG, "Inside ThumbView");
            imListView.setVisibility(View.GONE);
            imThumbView.setVisibility(View.GONE);
            imMapView.setVisibility(View.VISIBLE);
        }
        else if(imMapView.getVisibility() == View.VISIBLE) {
            Log.d(LOGTAG, "Inside MapView");
            imListView.setVisibility(View.VISIBLE);
            imThumbView.setVisibility(View.GONE);
            imMapView.setVisibility(View.GONE);
        }


        cd = new SuperConnectionDetector(activity);
        isInternetPresent = cd.isConnectingToInternet();

        if(isInternetPresent)
            getMyLocation();
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Internet");
            alertDialog.setMessage("Internet not enabled in your device. Do you want to enable it from settings menu");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), LOCATION_SETTINGS_REQUEST);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();

        }







        // Read shared preference for the lat-long
        // Save the latitude and longitude in SharedPreferences
        if(sharedpref.getString(res.getString(R.string.spf_last_lat), null) != null) {
            latitude = Double.parseDouble(sharedpref.getString(res.getString(R.string.spf_last_lat), null));

            if(selfLat.equals(""))
                selfLat = String.valueOf(latitude);

        }

        if(sharedpref.getString(res.getString(R.string.spf_last_lon), null) != null) {
            longitude = Double.parseDouble(sharedpref.getString(res.getString(R.string.spf_last_lon), null));


            if(selfLon.equals(""))
                selfLon = String.valueOf(longitude);
        }



        // Collect all address related keywords to be populated to the autocomplete box
        if(isInternetPresent)
            callSearchLocationTask();


        autoCompleteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, locationList);
        autoComplete.animate();
        autoComplete.setThreshold(1);
        autoComplete.setAdapter(autoCompleteAdapter);

        editor = sharedpref.edit();


        autoComplete.addTextChangedListener(new TextWatcher() {

            private boolean shouldAutoComplete = true;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shouldAutoComplete = true;

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (shouldAutoComplete) {


                }
            }

        });

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {


                // When the user selects any items from the autocomplete we get the position as well as the location text

                // Getting the location text if the array list size minus 1 is greater than or equal to the current position index

                String selectedLocation = parent.getItemAtPosition(position).toString();


                Log.d(LOGTAG, "AutoComplete Selected Location: "+selectedLocation);

                if(!selectedLocation.equals("")) {
                    // Save it in shared preference
                    editor.putString(res.getString(R.string.spf_location_keyword), selectedLocation); // Set view type to list
                    editor.commit();
                }



                // From that position we get the lat-long
                LatLng geo = (LatLng) latLngList.get(position);

                Log.d(LOGTAG, "AutoComplete Selected Lat: "+geo.latitude);
                Log.d(LOGTAG, "AutoComplete Selected Lon: "+geo.longitude);

                // Set the geo location of the place you want to search the offers for
                editor.putString(res.getString(R.string.spf_last_lat), String.valueOf(geo.latitude));
                editor.putString(res.getString(R.string.spf_last_lon), String.valueOf(geo.longitude));
                editor.commit();


                // Pass the latitude and longitude to fetch the location information
                loadOffersForCategoryLocations(String.valueOf(geo.latitude),  String.valueOf(geo.longitude));

            }
        });




        imListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putString(res.getString(R.string.spf_view_type), "list"); // Set view type to list
                editor.commit();

                BrowseOfferFragment fragment2 = new BrowseOfferFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment2);
                fragmentTransaction.commit();

                /*Log.d(LOGTAG, "List view clicked");
                imListView.setVisibility(View.GONE);
                imThumbView.setVisibility(View.VISIBLE);
                imMapView.setVisibility(View.GONE);*/
            }
        });


        imMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(res.getString(R.string.spf_view_type), "map"); // Set view type to map
                editor.commit();

                MapViewFragment fragment2 = new MapViewFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment2);
                fragmentTransaction.commit();
                Log.d(LOGTAG, "Map view clicked");

                /*Log.d(LOGTAG, "Thumb view clicked");
                imListView.setVisibility(View.VISIBLE);
                imThumbView.setVisibility(View.GONE);
                imMapView.setVisibility(View.GONE);*/

            }
        });


        imThumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(res.getString(R.string.spf_view_type), "thumb"); // Set view type to thumb
                editor.commit();

                BrowseOfferFragment fragment2 = new BrowseOfferFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment2);
                fragmentTransaction.commit();

                /*imListView.setVisibility(View.GONE);
                imThumbView.setVisibility(View.GONE);
                imMapView.setVisibility(View.VISIBLE);*/


            }
        });

        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Category clicked");

                Intent intent = new Intent();
                intent.setClass(getActivity(), CategoryActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
            }
        });

        tvEmptyView = (TextView) layout.findViewById(R.id.empty_view);
        mRecyclerOffers = (RecyclerView) layout.findViewById(R.id.my_recycler_view);
        mRecyclerOffers.setLayoutManager(new LinearLayoutManager(getActivity()));


        // Check if the Android version code is greater than or equal to Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //return resources.getDrawable(id, context.getTheme());
            //mRecyclerOffers.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider, getActivity().getApplicationContext().getTheme())));
        } else {
            //return resources.getDrawable(id);
            //mRecyclerOffers.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        }

        mListOffers = new ArrayList<>();

        mAdapter = new BrowseOffersViewAdapter(getActivity(), "BrowseOffers");
        mRecyclerOffers.setAdapter(mAdapter);

        //update your Adapter to containg the retrieved movies
        mAdapter.setOffers(mListOffers);
        return layout;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //if this fragment starts for the first time, load the list of movies from a database
        //mListOffers = MyApplication.getWritableDatabase().readOffers(DBOffers.ALL_OFFERS);
        //if the database is empty, trigger an AsycnTask to download movie list from the web
        if (mListOffers.isEmpty()) {

            Log.d(LOGTAG, "Redirect to 102: " + redirectTo);
            Log.d(LOGTAG, "Redeemar id 102: " + redeemarId);
            Log.d(LOGTAG, "Campaign id 102: " + campaignId);
            Log.d(LOGTAG, "Category id 102: " + categoryId);

            if(sharedpref.getString(res.getString(R.string.spf_location_keyword), null) != null) {
                locKeyword = sharedpref.getString(res.getString(R.string.spf_location_keyword), "");
                //autoComplete.setText(locKeyword);
            }

            /*if(sharedpref.getString(res.getString(R.string.spf_last_lat), null) != null) {
                selfLat = sharedpref.getString(res.getString(R.string.spf_last_lat), "");
            }

            if(sharedpref.getString(res.getString(R.string.spf_last_lon), null) != null) {
                selfLon = sharedpref.getString(res.getString(R.string.spf_last_lon), "");
            }*/

            if(isInternetPresent) {

                if (redirectTo.equals("BrandOffers") && !redeemarId.equals(""))
                    new BrandOffersAsyncTask(this).execute(redeemarId, user_id, String.valueOf(latitude), String.valueOf(longitude), selfLat, selfLon);
                else if (redirectTo.equals("CampaignOffers") && !campaignId.equals(""))
                    new CampaignOffersAsyncTask(this).execute(campaignId, user_id, String.valueOf(latitude), String.valueOf(longitude), selfLat, selfLon);
                else if (redirectTo.equals("CategoryOffers") && !categoryId.equals(""))
                    new BrowseOffersAsyncTask(this).execute(user_id, String.valueOf(latitude), String.valueOf(longitude), selfLat, selfLon, categoryId);
                else if (redirectTo.equals("OnDemand"))
                    new OnDemandOffersAsyncTask(this).execute(user_id, String.valueOf(latitude), String.valueOf(longitude), selfLat, selfLon);
                else
                    new BrowseOffersAsyncTask(this).execute(user_id, String.valueOf(latitude), String.valueOf(longitude), selfLat, selfLon, "");

            }
        }

        activityCommunicator.passDataToActivity(redirectTo);

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

        activity = getActivity();
        getActivity().setTitle(R.string.browse_offers);


        // Get the current geo position
        gps = new GPSTracker(getActivity());

        // If GPSTracker can get the current location of the user
        if(gps.canGetLocation()) {

            // Your current location (Self Location)
            selfLat = String.valueOf(gps.getLatitude());
            selfLon = String.valueOf(gps.getLongitude());

        }




    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        switch (id) {

           case R.id.action_search:
               Log.d(LOGTAG, "Inside BrowseOfferFragment");

               //Toast.makeText(getActivity().getApplicationContext(), "Search option selected", Toast.LENGTH_SHORT).show();
               //searchView.setVisibility(View.VISIBLE);


               final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
               searchView.setOnQueryTextListener(this);
               return true;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextChange(String query) {
        Log.d(LOGTAG, "Query is "+query);
        final List<Offer> filteredModelList = filter(mModels, query);
        //mAdapter.animateTo(filteredModelList);

        if(filteredModelList.size() > 0) {
            mAdapter.setOffers((ArrayList)filteredModelList);
            mAdapter.notifyDataSetChanged();
            tvEmptyView.setVisibility(View.GONE);

        }
        else {
            tvEmptyView.setText(R.string.no_records);
            tvEmptyView.setVisibility(View.VISIBLE);
        }


        mRecyclerOffers.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Offer> filter(List<Offer> models, String query) {
        query = query.toLowerCase();


        Log.d(LOGTAG, "Query inside filter is "+query);
        int p=0;

        final List<Offer> filteredModelList = new ArrayList<>();
        if(models != null) {
            for (Offer model : models) {

                // Search by either offer description or what you get or more infomation or company (brand) name or address or zip code
                final String offerDesc = model.getOfferDescription().toLowerCase();
                final String whatYouGet = model.getWhatYouGet().toLowerCase();
                final String moreInfo = model.getMoreInformation().toLowerCase();
                final String compName = model.getCompanyName().toLowerCase();
                final String compAddr = model.getAddress().toLowerCase();
                final String compZip = model.getZipcode().toLowerCase();

                Log.d(LOGTAG, "Search more info is "+moreInfo);

                if (offerDesc.contains(query) || whatYouGet.contains(query) || moreInfo.contains(query) || compName.contains(query)
                        || compAddr.contains(query) || compZip.contains(query)) {
                    filteredModelList.add(model);

                    p++;

                }

            }
        }


        Log.d(LOGTAG, "No. of match found is "+p);
        return filteredModelList;
    }





    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {

        if (isAdded() && activity != null) {
            if (listOffers.size() > 0 && mAdapter != null) {
                mModels = listOffers;
                //Log.d(LOGTAG, "Inside Adapter: "+mAdapter);
                mAdapter = new BrowseOffersViewAdapter(getActivity().getApplicationContext(), listOffers, "BrowseOffers");
                mRecyclerOffers.setAdapter(mAdapter);
                mRecyclerOffers.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);
            } else {
                tvEmptyView.setText(getString(R.string.no_records));
            }

        }


    }


    private void callSearchLocationTask() {
        // This will callback onUsersLoaded function after execution
        new GetNearByOffersAsyncTask(this).execute(String.valueOf(latitude), String.valueOf(longitude));
    }


    @Override
    public void onUsersLoaded(ArrayList<User> listAddresses) {

        Set<User> setAddress = new LinkedHashSet<>(listAddresses);

        /*Iterator it = setAddress.iterator();
        while(it.hasNext()) {
            Log.d(LOGTAG, "ITEM: "+it.next().toString());
        }*/

        location = location.toLowerCase();

        int cnt = listAddresses.size();
        int p = 0;
        locationList.clear();
        boolean found = false;

        for(int i = 0; i < cnt; i++) {
            String tAddress = "", tCity = "", tState = "", tZip = "", tLoc = "", tLat = "", tLon = "";

            User addr = listAddresses.get(i);

            if(addr.getAddress() != null)
                tAddress = addr.getAddress().trim();

            if(addr.getCity() != null)
                tCity = addr.getCity().trim();

            if(addr.getState() != null)
                tState = addr.getState().trim();

            if(addr.getZipcode() != null)
                tZip = addr.getZipcode().trim();

            if(addr.getLocation() != null)
                tLoc = addr.getLocation().trim();

            if(addr.getLat() != null)
                tLat = addr.getLat().trim();

            if(addr.getLon() != null)
                tLon = addr.getLon().trim();


            /*if(tAddress.toLowerCase().contains(location) && !Utils.findDuplicate(locationList, tAddress) && !Utils.findDuplicate(locationList, tAddress+", "+tState.toUpperCase())) {
                found = true;
                if(!tState.equals(""))
                    locationList.add(tAddress+", "+tState.toUpperCase());
                else
                    locationList.add(tAddress);
                p++;
            }*/

            if(tCity.toLowerCase().contains(location) && !Utils.findDuplicate(locationList, tCity) && !Utils.findDuplicate(locationList, tCity+", "+tState.toUpperCase()) && !found) {
                //found = true;
                if(!tState.equals(""))
                    locationList.add(tCity+", "+tState.toUpperCase());
                else
                    locationList.add(tCity);
                p++;
            }
            if(tState.toLowerCase().contains(location) && !Utils.findDuplicate(locationList, tState)) {
                locationList.add(tState);
                p++;
            }

            if(tZip.toLowerCase().contains(location) && !Utils.findDuplicate(locationList, tZip)) {
                locationList.add(tZip);
                p++;
            }

            if(tLoc.toLowerCase().contains(location) && !Utils.findDuplicate(locationList, tLoc) && !Utils.findDuplicate(locationList, tLoc+", "+tState)) {
                found = true;
                locationList.add(tLoc);
                if(!tState.equals(""))
                    locationList.add(tLoc+", "+tState);
                else
                    locationList.add(tLoc);
                p++;
            }


            Log.d(LOGTAG, "After User Loaded Lat is: "+tLat);
            Log.d(LOGTAG, "After User Loaded Lon is: "+tLon);

            if(tLat!="" && tLon != "")
                latLngList.add(new LatLng(Double.parseDouble(tLat), Double.parseDouble(tLon)));
        }

        // Now read shared preference and add your current location to this ArrayList
        if(sharedpref.getString(res.getString(R.string.spf_location_keyword), null) != null) {
            locKeyword = sharedpref.getString(res.getString(R.string.spf_location_keyword), "");
            if(!locKeyword.equals("")) {
                locationList.add(locKeyword);

                // Also set the keyword  as default text
                autoComplete.setText(locKeyword);
            }
        }

    }


    public void getMyLocation() {
        // Initialize GPSTracker
        gps = new GPSTracker(getActivity());

        // If GPSTracker can get the location
        if(gps.canGetLocation()) {

            // Your current location (Self Location)
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Log.d(LOGTAG, "My Lat Values: "+latitude);
            Log.d(LOGTAG, "My Long Values: "+longitude);


            // Save the latitude and longitude in SharedPreferences
            // By default your current location is the location you are searching the offers from
            if(latitude != 0.0 && longitude != 0.0) {
                editor.putString(res.getString(R.string.spf_last_lat), String.valueOf(latitude));
                editor.putString(res.getString(R.string.spf_last_lon), String.valueOf(longitude));
                editor.commit();
            }


            // CHeck if internet is enabled in device
            if(isInternetPresent) {

                // If internet is enabled then call a API which will get location
                new FetchLocationAsyncTask(this, getActivity().getApplicationContext()).execute(String.valueOf(latitude), String.valueOf(longitude));
            } else {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Internet");
                alertDialog.setMessage("Internet not enabled in your device. Do you want to enable it from settings menu");
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SETTINGS_REQUEST);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("GPS settings");
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SETTINGS_REQUEST);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }


    @Override
    public void onLocationFetched(User locUser) {

        // Gte the location from web services
        if(locUser.getLocation() != null && !locUser.getLocation().equals("")) {
            Log.d(LOGTAG, "My current location is: " + locUser.getLocation());
            // Set to Shared Preference
            editor.putString(res.getString(R.string.spf_location_keyword), locUser.getLocation()); // Set location keyword
            editor.commit();
        }
        else {
            Log.d(LOGTAG, "No location is found");
        }


    }



    public void loadOffersForCategoryLocations(String tLat, String tLng) {

        // This location as selected by user from auto complete
        mRecyclerOffers.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
        // Here latitude and longitude are the present location of the user (Self Location)
        if(!categoryId.equals(""))
            new CategoryOffersAsyncTask(this).execute(categoryId, tLat, tLng, selfLat,  selfLon);
        else
            new BrowseOffersAsyncTask(this).execute(user_id, tLat, tLng, selfLat, selfLon, "");

    }
}
