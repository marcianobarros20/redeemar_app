package com.tier5.redeemar.RedeemarConsumerApp.fragments;

/**
 * Created by Dibs on 29/07/15.
 */
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.tier5.redeemar.RedeemarConsumerApp.BrandOfferActivity;
import com.tier5.redeemar.RedeemarConsumerApp.BrowseOffersActivity;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.DividerItemDecoration;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.RecyclerItemClickListener;
import com.tier5.redeemar.RedeemarConsumerApp.ResizeAnimation;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrandViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadImageTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadSaveImageAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.GetNearByBrandsAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.MyItem;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.MarkerItem;
import com.tier5.redeemar.RedeemarConsumerApp.utils.SuperConnectionDetector;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class HomeFragment extends Fragment implements UsersLoadedListener, OnMapReadyCallback {

    private String LOGTAG = "HomeFragment";
    private double latitude = 0.0, longitude = 0.0;
    private View rootView;
    GoogleMap mMap;
    MapView mMapView;
    private ClusterManager mClusterManager;
    private ArrayList<User> brandList, dispBrandList;

    private RecyclerView mRecyclerView;
    private BrandViewAdapter mAdapter;
    private RelativeLayout containerLayout;
    private LinearLayout innerContainerLayout;
    private LinearLayout.LayoutParams lp;

    private float dpHeight = 0, bHeight = 0, nHeight = 0, dpWidth= 0;
    private int pxHeight, pxHeightRev, pxWidth;
    private Resources res;
    private SharedPreferences sharedpref;
    SharedPreferences.Editor editor;
    //private Double last_lat = 0.0, last_lon = 0.0;
    boolean isLocationSensetive = false;
    private boolean mMapViewExpanded = false;
    private SuperConnectionDetector cd;
    private boolean isInternetPresent = false;


    String clickIndex = "", companyName="", companyLocation="";


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();
        sharedpref = getActivity().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();


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



        if(sharedpref.getString(res.getString(R.string.spf_last_lat), null) != null) {
            latitude = Double.parseDouble(sharedpref.getString(res.getString(R.string.spf_last_lat), "0"));
            isLocationSensetive = true;
            Log.d(LOGTAG, "Last Lat: "+latitude);
        }


        if(sharedpref.getString(res.getString(R.string.spf_last_lon), null) != null) {
            longitude = Double.parseDouble(sharedpref.getString(res.getString(R.string.spf_last_lon), "0"));
            Log.d(LOGTAG, "Last Lon: "+longitude);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.nearby_brands);


        try {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

            brandList = new ArrayList<>();
            dispBrandList = new ArrayList<>();

            containerLayout = (RelativeLayout) rootView.findViewById(R.id.mainContainer);
            innerContainerLayout = (LinearLayout) rootView.findViewById(R.id.innerContainer);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.myRecyclerView);
            innerContainerLayout.setVisibility(View.VISIBLE);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            // Check if the Android version code is greater than or equal to Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return resources.getDrawable(id, context.getTheme());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider, getActivity().getApplicationContext().getTheme())));
            } else {
                //return resources.getDrawable(id);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
            }


            if(!isLocationSensetive) {
                // create class object
                GPSTracker gps = new GPSTracker(getActivity());

                // check if GPS enabled
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();



                    if(latitude == 0 && longitude == 0) {

                        gps.showSettingsAlert();

                    }

                    // \n is for new line

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }

            //Toast.makeText(getActivity().getApplicationContext(), "Your last known location as per browsing the map is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity().getApplicationContext(), "Your last known location as per browsing the map is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();




            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            dpWidth = displayMetrics.widthPixels / displayMetrics.density;


            pxHeight = displayMetrics.heightPixels/2;
            pxWidth = displayMetrics.widthPixels/2;

            pxHeightRev = pxHeight + (pxHeight/2);

            /*bHeight = dpHeight/2;
            nHeight = bHeight + (bHeight/2);*/

            Log.d(LOGTAG, "Height dp: "+dpHeight);
            Log.d(LOGTAG, "Width dp: "+dpWidth);

            Log.d(LOGTAG, "Height px: "+pxHeight);
            Log.d(LOGTAG, "Width px: "+pxWidth);




            if (savedInstanceState != null) {
                //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
                //mListOffers = savedInstanceState.getParcelableArrayList(STATE_OFFERS);

                Log.d(LOGTAG, "Inside savedInstanceState");
            } else {
                //if this fragment starts for the first time, load the list of movies from a database
                //mListOffers = MyApplication.getWritableDatabase().readOffers(DBOffers.ALL_OFFERS);
                //if the database is empty, trigger an AsycnTask to download movie list from the web
                //if (mListOffers.isEmpty()) {
                    Log.d(LOGTAG, "HomeFragment: executing task from fragment");
                    new GetNearByBrandsAsyncTask(this).execute(String.valueOf(latitude), String.valueOf(longitude));
                //}
            }

        }
        catch (InflateException e){
            Log.e(LOGTAG, "Inflate exception occured");
        }
        return rootView;
    }


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }*/


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Toast.makeText(getActivity().getApplicationContext(), "My Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        Log.d(LOGTAG, "Inside onMapReady");


        mClusterManager = new ClusterManager(getActivity(), googleMap);

        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //Log.d(LOGTAG, "Max zoom is: "+googleMap.getMaxZoomLevel());

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));
        mClusterManager = new ClusterManager(getActivity(), googleMap);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        googleMap.setOnMarkerClickListener(mClusterManager);
        //mClusterManager.cluster();

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(6.2f));



        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
            // TODO Auto-generated method stub
            Log.d(LOGTAG, "OnMap Clicked "+arg0.latitude + "-" + arg0.longitude);
            //editor.putString(getString(R.string.spf_last_lat), String.valueOf(arg0.latitude)); // Storing Lat
            //editor.putString(getString(R.string.spf_last_lon), String.valueOf(arg0.longitude)); // Storing Lon
            //editor.commit(); // commit changes
            mMapViewExpanded = true;
            animateMapView();
            }


        });


        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerItem>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerItem> cluster) {
                //clickedCluster = cluster; // remember for use later in the Adapter

                ArrayList myItem = (ArrayList) cluster.getItems();

                if(dispBrandList.size() > 0)
                    dispBrandList.clear();

                for(int a=0; a < myItem.size(); a++) {

                    MarkerItem itm = (MarkerItem) myItem.get(a);

                    String ind = itm.getTitle();

                    User br = brandList.get(Integer.parseInt(ind));

                    br.setLogoName(br.getLogoName());
                    br.setTargetId(br.getTargetId());


                    dispBrandList.add(br);

                }


                mAdapter = new BrandViewAdapter(getActivity().getApplicationContext(), dispBrandList, "BrandList");
                mRecyclerView.setAdapter(mAdapter);
                //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                Log.d(LOGTAG, "Brand List: "+dispBrandList.size());

                //innerContainerLayout.setVisibility(View.VISIBLE);
                mMapViewExpanded = false;
                animateMapView();

                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User br = dispBrandList.get(position);

                        if(br.getTargetId() != null) {
                            Log.d(LOGTAG, "Group Recycler Item has been clicked. Brand User Id is "+br.getId());
                            openFragment(br.getId());
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d(LOGTAG, "Long clicked");
                    }
                }));

                //Log.d(LOGTAG, "Adapter Item size: "+myItem.size());
                return false;
            }
        });


        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerItem>() {
            @Override
            public boolean onClusterItemClick(MarkerItem item) {

                Log.d(LOGTAG, "MyItem: "+item.getTitle());

                if(dispBrandList.size() > 0)
                    dispBrandList.clear();

                String ind = item.getTitle();
                clickIndex = ind;

                User br = brandList.get(Integer.parseInt(ind));
                br.setLogoName(br.getLogoName());

                companyName = br.getCompanyName();
                companyLocation = br.getLocation();


                dispBrandList.add(br);
                mAdapter = new BrandViewAdapter(getActivity().getApplicationContext(), dispBrandList, "BrandList");
                mRecyclerView.setAdapter(mAdapter);

                //innerContainerLayout.setVisibility(View.VISIBLE);
                animateMapView();

                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(LOGTAG, "Recycler Item has been clicked");

                        User br = dispBrandList.get(position);

                        if(br.getTargetId() != null) {
                            Log.d(LOGTAG, "Single Recycler Item has been clicked. Brand User Id is "+br.getId());
                            openFragment(br.getId());
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d(LOGTAG, "Long clicked");
                    }
                }));
                if(!getMapViewStatus())
                    animateMapView();

                Log.d(LOGTAG, "Map View: "+getMapViewStatus());
                Log.d(LOGTAG, "Cluster item clicked "+item.getTitle());
                return false;
            }
        });




    }



    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;


        private TextView tvInfoTitle, tvInfoSnippet;
        private ImageView imStoreFrontPic;

        CustomInfoWindowAdapter() {
            mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
//            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
//                // This means that getInfoContents will be called.
//                return null;
//            }
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
//            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
//                // This means that the default info contents will be used.
//                return null;
//            }
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {

            tvInfoTitle = (TextView) view.findViewById(R.id.title);
            tvInfoSnippet = (TextView) view.findViewById(R.id.snippet);
            imStoreFrontPic = (ImageView) view.findViewById(R.id.badge);

            //String ind = marker.getTitle();
            Log.d(LOGTAG, "Map title index: "+clickIndex);

            if(clickIndex != null && !clickIndex.equals("")) {
                User br = brandList.get(Integer.parseInt(clickIndex));
                if(br.getStoreFrontImage() != null && !br.getStoreFrontImage().equals("")) {

                    String imageUrl = br.getStoreFrontImage();
                    Log.d(LOGTAG, "Store Image URL: "+imageUrl);
                    new DownloadImageTask(imStoreFrontPic).execute(imageUrl);

                }


                //new DownloadSaveImageAsyncTask(getActivity()).execute(imageUrl);

                tvInfoTitle.setText(companyName);
                tvInfoSnippet.setText(companyLocation);

            }



        }
    }




    @Override
    public void onUsersLoaded(ArrayList<User> listBrands) {

        brandList = listBrands;

        Log.d(LOGTAG, "Inside callback onOffersLoaded: "+listBrands.size());
        /*mAdapter = new BrowseOffersViewAdapter(getActivity(), listOffers, "BrowseOffers");
        mRecyclerOffers.setAdapter(mAdapter);
        mRecyclerOffers.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);*/

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_deals);

        for (int i = 0; i < brandList.size(); i++) {
            User brnd = brandList.get(i);

            if(brnd.getLat() != null && !brnd.getLat().equalsIgnoreCase("") && brnd.getLon() != null && !brnd.getLon().equalsIgnoreCase("")) {

                MyItem offsetItem = new MyItem(Double.parseDouble(brnd.getLat()), Double.parseDouble(brnd.getLon()));

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(brnd.getLat()), Double.parseDouble(brnd.getLon())))
                        .title(String.valueOf(i))
                        .snippet(brnd.getCompanyName())
                        .icon(icon);
                MarkerItem markerItem = new MarkerItem(markerOptions);

                mClusterManager.addItem(markerItem);

            }


        }

        mClusterManager.cluster();



    }

    private void animateMapView() {
        Log.d(LOGTAG, "CLICKED ON THE MAPVIEW");
        lp = (LinearLayout.LayoutParams) mMapView.getLayoutParams();

        ResizeAnimation a = new ResizeAnimation(mMapView);
        a.setDuration(250);

        if (!getMapViewStatus()) {
            Log.d(LOGTAG, "Mapview just expanded");
            mMapViewExpanded = true;
            //a.setParams(lp.height, dpToPx(getResources(), Integer.parseInt(String.valueOf(bHeight))));
            a.setParams(lp.height, pxHeight);

        } else {
            Log.d(LOGTAG, "Mapview just contracted");
            mMapViewExpanded = false;
            //a.setParams(lp.height, dpToPx(getResources(), Integer.parseInt(String.valueOf(nHeight))));
            a.setParams(lp.height, pxHeightRev);
            //mMapView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));

        }
        mMapView.startAnimation(a);
    }

    private boolean getMapViewStatus() {
        return mMapViewExpanded;
    }

    public int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public void openFragment(String redeemarId) {

        editor.putString(getString(R.string.spf_redir_action), "BrandOffers"); // Storing Last Activity
        editor.putString(getString(R.string.spf_popup_action), "1"); // Storing Last Activity
        editor.putString(getString(R.string.spf_redeemer_id), redeemarId); // Storing Redeemar Id
        editor.commit(); // commit changes


        /*Bundle args = new Bundle();
        args.putString(getString(R.string.ext_redir_to), "BrandOffers");
        args.putString(getString(R.string.ext_redeemar_id), redeemarId);
        Fragment fr = new BrowseOfferFragment();
        fr.setArguments(args);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fr);
        fragmentTransaction.commit();*/

        Intent intent = new Intent(getActivity(), BrowseOffersActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
