package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.roughike.bottombar.BottomBar;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrandViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.MarkerItem;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearMeActivity extends BaseMapActivity implements ActivityCompat.OnRequestPermissionsResultCallback, TaskCompleted {

    private static final String LOGTAG = "NearMe";

    SupportMapFragment mapFragment;
    /*private TextView tvAddress, tvOfferTitle, tvWhatYouGet, tvPriceRangeId, tvPayValue, tvDiscount, tvRetailValue, tvExpires;
    private NetworkImageView thumbnail;
    private ImageLoader mImageLoader;
    private Button btnBank,  btnPass,  btnRedeem;
    private SharedPreferences sharedpref;
    private GoogleMap mMap;
    private Toolbar toolbar;*/

    private ClusterManager<MarkerItem> mClusterManager;
    private ArrayList<User> brandList, dispBrandList;
    private RecyclerView mRecyclerView;
    BrandViewAdapter mAdapter;
    RelativeLayout containerLayout;
    LinearLayout recyclerLayout;
    double latitude = 0.0, longitude = 0.0;
    String user_id = "0";

    private GPSTracker gps;

    private BottomBar mBottomBar;
    private Resources res;
    private SharedPreferences sharedpref;

    private Bundle mySaveInstanceState;

    Fragment fr;
    Bundle args;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        //mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySaveInstanceState = savedInstanceState;

    }



        @Override
    public void startDemo() {



        brandList = new ArrayList<>();
        dispBrandList = new ArrayList<>();

        containerLayout = (RelativeLayout) findViewById(R.id.rl_Container);
        recyclerLayout = (LinearLayout) findViewById(R.id.rl_Container1);


       res = getResources();
       sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

       SharedPreferences.Editor editor = sharedpref.edit();

       if(sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
           user_id = sharedpref.getString(res.getString(R.string.spf_user_id), "0");
       }

       Log.d(LOGTAG, "Near Me User Id: "+user_id);


       //setContentView(R.layout.offers_recycler);
       //toolbar = (Toolbar) findViewById(R.id.toolbar);



       // create class object
       gps = new GPSTracker(NearMeActivity.this);

       // check if GPS enabled
       if(gps.canGetLocation()){

           latitude = gps.getLatitude();
           longitude = gps.getLongitude();

           if(latitude == 0 && longitude == 0) {

               gps.showSettingsAlert();

           }

           // \n is for new line
           Toast.makeText(getApplicationContext(), "My Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

       } else {
           // can't get location
           // GPS or Network is not enabled
           // Ask user to enable GPS/network in settings
           gps.showSettingsAlert();
       }



        /*if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.browse_offers);
        }*/


       args = new Bundle();
       args.putString(getString(R.string.ext_user_id), user_id);
       args.putString(getString(R.string.ext_lat), String.valueOf(latitude));
       args.putString(getString(R.string.ext_lng), String.valueOf(longitude));

       getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));

       mClusterManager = new ClusterManager<MarkerItem>(this, getMap());
       getMap().setOnCameraChangeListener(mClusterManager);
       getMap().setInfoWindowAdapter(mClusterManager.getMarkerManager());
       getMap().setOnMarkerClickListener(mClusterManager);

       //new GetNearByBrandsAsyncTask(NearMeActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));


        /*mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        // Layout Managers:
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check if the Android version code is greater than or equal to Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           //return resources.getDrawable(id, context.getTheme());
           mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider, getApplicationContext().getTheme())));
        } else {
           //return resources.getDrawable(id);
           mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        }*/

       getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {

           @Override
           public void onMapClick(LatLng arg0) {
               // TODO Auto-generated method stub
               Log.d(LOGTAG, "OnMap Clicked "+arg0.latitude + "-" + arg0.longitude);

               if(recyclerLayout.getVisibility()==View.GONE)
               {
                   recyclerLayout.setVisibility(View.VISIBLE);
                   Log.d(LOGTAG, "Set Visible");
               }
               else
               {
                   recyclerLayout.setVisibility(View.GONE);
                   Log.d(LOGTAG, "Set Gone");
               }
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

                    Log.d(LOGTAG, "Cluster clicked 1: "+ind);

                    User br = (User) brandList.get(Integer.parseInt(ind));

                    Log.d(LOGTAG, "User Company : "+br.getCompanyName());
                    Log.d(LOGTAG, "User Address : "+br.getAddress());
                    Log.d(LOGTAG, "User Logo : "+br.getLogoName());

                    br.setLogoName(br.getLogoName());
                    br.setTargetId(br.getTargetId());


                    dispBrandList.add(br);

                }

                recyclerLayout.setVisibility(View.VISIBLE);




                //ArrayList usr = (ArrayList) cluster.getItems();

                mAdapter = new BrandViewAdapter(getApplicationContext(), dispBrandList, "BrandList");
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(LOGTAG, "Recycler Item has been clicked");

                        User br = (User)dispBrandList.get(position);

                        if(br.getTargetId() != null) {

                            Intent intent = new Intent(getApplicationContext(), BrandMainActivity.class);
                            intent.putExtra("unique_target_id", br.getTargetId());
                            intent.putExtra(getString(R.string.ext_activity), "NearMeActivity");
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d(LOGTAG, "Long clicked");
                    }
                }));

                Log.d(LOGTAG, "Adapter Item size: "+myItem.size());
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerItem>() {
            @Override
            public boolean onClusterItemClick(MarkerItem item) {
                //clickedClusterItem = item;

                if(dispBrandList.size() > 0)
                    dispBrandList.clear();

                String ind = item.getTitle();

                Log.d(LOGTAG, "Cluster clicked 1: "+ind);

                User br = (User) brandList.get(Integer.parseInt(ind));
                Log.d(LOGTAG, "User Company : "+br.getCompanyName());
                Log.d(LOGTAG, "User Address : "+br.getAddress());
                Log.d(LOGTAG, "User Logo : "+br.getLogoName());

                br.setLogoName(br.getLogoName());

                dispBrandList.add(br);

                mAdapter = new BrandViewAdapter(getApplicationContext(), dispBrandList, "BrandList");
                mRecyclerView.setAdapter(mAdapter);

                recyclerLayout.setVisibility(View.VISIBLE);

                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(LOGTAG, "Recycler Item has been clicked");

                        User br = (User)dispBrandList.get(position);

                        if(br.getTargetId() != null) {

                            Intent intent = new Intent(getApplicationContext(), BrandMainActivity.class);
                            intent.putExtra("unique_target_id", br.getTargetId());
                            intent.putExtra(getString(R.string.ext_activity), "NearMeActivity");
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Log.d(LOGTAG, "Long clicked");
                    }
                }));



                Log.d(LOGTAG, "Cluster item clicked "+item.getTitle());
                return false;
            }
        });





    }


    private void addItems(String resp) {

        // Set some lat/lng coordinates to start with.

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);


        try {

            JSONObject reader = new JSONObject(resp);


            if (reader.getString("messageCode").equals("R01001")) {

                //Log.d(LOGTAG, "Message Data: " + reader.getString("data"));

                JSONArray offersArray = new JSONArray(reader.getString("data"));

                //Log.d(LOGTAG, "Data Length: " + offersArray.length());


                //Iterate the jsonArray and print the info of JSONObjects
                for (int i = 0; i < offersArray.length(); i++) {
                    JSONObject jsonObject = offersArray.getJSONObject(i);


                    double lat=0.0, lng=0.0;

                    User brand = new User();


                    if (jsonObject.optString("id") != null && jsonObject.optString("id") != "") {
                        lat = Double.parseDouble(jsonObject.optString("id"));
                        brand.setId(jsonObject.optString("id"));
                        //Log.d(LOGTAG, "Cluster Id: "+jsonObject.optString("id"));
                    }

                    if (jsonObject.optString("company_name") != null && jsonObject.optString("company_name") != "") {
                        brand.setCompanyName(jsonObject.optString("company_name"));
                        //Log.d(LOGTAG, "Cluster Company Name: "+jsonObject.optString("company_name"));
                    }

                    if (jsonObject.optString("address") != null && jsonObject.optString("address") != "") {
                        brand.setAddress(jsonObject.optString("address"));
                        //Log.d(LOGTAG, "Cluster Address: "+jsonObject.optString("address"));
                    }


                    if (jsonObject.optString("lat") != null && jsonObject.optString("lat") != "") {
                        lat = Double.parseDouble(jsonObject.optString("lat"));
                        brand.setLat(jsonObject.optString("lat"));
                        //Log.d(LOGTAG, "Cluster Lat: "+jsonObject.optString("lat"));
                    }

                    if (jsonObject.optString("lng") != null && jsonObject.optString("lng") != "") {
                        lng = Double.parseDouble(jsonObject.optString("lng"));
                        brand.setLon(jsonObject.optString("lng"));
                        //Log.d(LOGTAG, "Cluster Lon: "+jsonObject.optString("lng"));
                    }




                    //if (jsonObject.optString("profile") != null && jsonObject.optString("profile") != "") {
                    if(!jsonObject.isNull("profile")) {

                        Log.d(LOGTAG, "Inside profile");

                        JSONObject profileObject = new JSONObject(jsonObject.getString("profile"));

                        if (profileObject.getString("logo_name") != null && profileObject.getString("logo_name") != "") {

                            brand.setLogoName(UrlEndpoints.basePathURL + "" + profileObject.getString("logo_name"));
                            //Log.d(LOGTAG, "Cluster Logo: " + profileObject.getString("logo_name"));

                        }


                        if (profileObject.getString("target_id") != null && profileObject.getString("target_id") != "") {

                            brand.setTargetId(profileObject.getString("target_id"));
                            //Log.d(LOGTAG, "Cluster Traget Id: " + profileObject.getString("target_id"));

                        }

                    }


                    brandList.add(brand);

                    //MyClusterItem offsetItem = new MyClusterItem(lat, lng, icon);

                    //LatLng markerLatLng = new LatLng(lat, lng);

                    //Log.d(LOGTAG, "Cluster Zipcode: "+jsonObject.optString("zipcode"));

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(String.valueOf(i))
                            .icon(icon);
                    MarkerItem markerItem = new MarkerItem(markerOptions);

                    mClusterManager.addItem(markerItem);

                } // End of for loop for videos


            } // End of if

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.action_search:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Toast.makeText(NearMeActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onTaskComplete(String result) {

        //setUpClusterer();

        addItems(result);

        Log.d(LOGTAG, "Task Completed Response: "+result);
    }

}
