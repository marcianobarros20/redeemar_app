package com.tier5.redeemar.RedeemarConsumerApp;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.AboutFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.BrowseOfferFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.ContactFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.FragmentDrawer;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.HelpFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.HomeFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.MyOfferFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.RateUsFragment;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Random;


public class BrowseOffersActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * RecyclerView: The new recycler view replaces the list view. Its more modular and therefore we
     * must implement some of the functionality ourselves and attach it to our recyclerview.
     * <p/>
     * 1) Position items on the screen: This is done with LayoutManagers
     * 2) Animate & Decorate views: This is done with ItemAnimators & ItemDecorators
     * 3) Handle any touch events apart from scrolling: This is now done in our adapter's ViewHolder
     */

    private static final String LOGTAG = "BrowseOffer";

    private Toolbar mToolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private String redeemerId = "";
    private JSONArray offersArray;
    private BottomBar mBottomBar;
    private GPSTracker gps;
    double latitude = 0.0, longitude = 0.0;
    private Resources res;
    private SharedPreferences sharedpref;
    String user_id = "0";
    private int REQUEST_COARSE_LOCATION = 0;
    private int REQUEST_FINE_LOCATION = 0;
    private View mLayout;
    private ProgressDialog pd;
    private Fragment fr;
    private Bundle args;
    private ViewGroup mContainerToolbar;
    private FragmentDrawer mDrawerFragment;
    private GoogleMap mMap;
    protected ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ArrayList<Category> categories;
    private String redirectTo="", redeemarId = "", campaignId = "", jsonCatText = "";
    private final int NavGroupId = 1001;


    /**
     * Permissions required to read and write contacts.
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_recycler);

        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        // Get Data from Intent
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            redirectTo = extras.getString(getString(R.string.ext_redir_to));
            redeemarId = extras.getString(getString(R.string.ext_redeemar_id));

            if(redirectTo.equalsIgnoreCase("CampaignOffers")) {
                campaignId = extras.getString(getString(R.string.ext_campaign_id));
            }

            Log.d(LOGTAG, "Redirect to 100: " + redirectTo);
            Log.d(LOGTAG, "Redeemar id 100: " + redeemarId);

        }





        setupToolbar();
        initNavigationDrawer();
        setupBottombar(mBottomBar, savedInstanceState);
        //setUpMap();
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.browse_offers);


        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.list); // it's getSupportActionBar() if you're using AppCompatActivity, not getActionBar()
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // it's getSupportActionBar() if you're using AppCompatActivity, not getActionBar()
        }


        /*final ActionBar ab = this.getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.list);
        ab.setDisplayHomeAsUpEnabled(true);*/
    }

    private void setupBottombar(BottomBar mBottomBar, Bundle savedInstanceState) {

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setMaxFixedTabs(5);
        mBottomBar.setDefaultTabPosition(3);
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                if (menuItemId == R.id.bottom_scan_offer) {

                    Intent intent = new Intent(BrowseOffersActivity.this, CloudReco.class);
                    startActivity(intent);

                } else if (menuItemId == R.id.bottom_my_offers) {

                    getSupportActionBar().setTitle(R.string.my_banked);

                    Fragment fr = new MyOfferFragment();
                    fr.setArguments(args);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();


                } else if (menuItemId == R.id.bottom_nearby) {

                    //Toast.makeText(getApplicationContext(), "Neaby offers selected", Toast.LENGTH_SHORT).show();

                    getSupportActionBar().setTitle(R.string.nearby_brands);


                    Fragment fr = new HomeFragment();
                    args = new Bundle();
                    args.putString(getString(R.string.ext_user_id), user_id);
                    args.putString(getString(R.string.ext_lat), String.valueOf(latitude));
                    args.putString(getString(R.string.ext_lng), String.valueOf(longitude));

                    fr.setArguments(args);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.container_body, fr);
                    transaction.commit();

                } else if (menuItemId == R.id.bottom_browse_offers) {

                    //Toast.makeText(getApplicationContext(), "Browse offers selected", Toast.LENGTH_SHORT).show();

                    getSupportActionBar().setTitle(R.string.browse_offers);

                    Log.d(LOGTAG, "Redirect to 101: " + redirectTo);
                    Log.d(LOGTAG, "Redeemar id 101: " + redeemarId);
                    Log.d(LOGTAG, "Campaign id 101: " + campaignId);

                    Fragment fr = new BrowseOfferFragment();
                    Bundle args1 = new Bundle();
                    if(redirectTo.equalsIgnoreCase("BrandOffers")) {
                        getSupportActionBar().setTitle(R.string.offers_by_brand);
                        args1.putString(getString(R.string.ext_redir_to), "BrandOffers");
                        args1.putString(getString(R.string.ext_redeemar_id), redeemarId);
                    }

                    else if(redirectTo.equalsIgnoreCase("CampaignOffers")) {
                        getSupportActionBar().setTitle(R.string.offers_by_campaign);
                        args1.putString(getString(R.string.ext_redir_to), "CampaignOffers");
                        args1.putString(getString(R.string.ext_redeemar_id), redeemarId);
                        args1.putString(getString(R.string.ext_campaign_id), campaignId);

                    }
                    fr.setArguments(args1);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();

                }
                else if (menuItemId == R.id.bottom_daily_deals) {


                    //getSupportActionBar().setTitle(R.string.logout);
                    Intent i = new Intent(getApplicationContext(), LogoutActivity.class);
                    startActivity(i);


                }

            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottom_scan_offer) {

                    Intent intent = new Intent(BrowseOffersActivity.this, CloudReco.class);
                    startActivity(intent);


                } else if (menuItemId == R.id.bottom_my_offers) {

                    getSupportActionBar().setTitle(R.string.my_banked);

                    //Toast.makeText(getApplicationContext(), "You are already here", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle(R.string.my_banked);

                    Fragment fr = new MyOfferFragment();
                    fr.setArguments(args);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();

                    //Intent intent = new Intent(MyOffersActivity.this, MyOffersActivity.class);
                    //startActivity(intent);
                } else if (menuItemId == R.id.bottom_nearby) {

                    getSupportActionBar().setTitle(R.string.nearby_brands);

                } else if (menuItemId == R.id.bottom_browse_offers) {

                    getSupportActionBar().setTitle(R.string.browse_offers);

                } else if (menuItemId == R.id.bottom_daily_deals) {



                    Toast.makeText(getApplicationContext(), "You have logged out.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    private void initNavigationDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        setupActionBarDrawerToogle();
        if (mNavigationView != null) {
            Log.d(LOGTAG, "Inside left navigation menu");
            setupDrawerContent(mNavigationView);
        }
    }

    /**
     * In case if you require to handle drawer open and close states
     */
    private void setupActionBarDrawerToogle() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                Snackbar.make(view, R.string.drawer_close, Snackbar.LENGTH_SHORT).show();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                Snackbar.make(drawerView, R.string.drawer_open, Snackbar.LENGTH_SHORT).show();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void setupDrawerContent(NavigationView navigationView) {

        addItemsRunTime(navigationView);

        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        Log.d(LOGTAG, "Menu Item clicked "+menuItem.getItemId());

                        MenuItem mPreviousMenuItem=null;



                        switch (menuItem.getItemId()) {

                            case R.id.nav_help:
                                getSupportActionBar().setTitle(R.string.nav_item_help);
                                Fragment helpFragment = new HelpFragment();
                                FragmentManager helpFm = getFragmentManager();
                                FragmentTransaction helpFragmentTransaction = helpFm.beginTransaction();
                                helpFragmentTransaction.replace(R.id.container_body, helpFragment);
                                helpFragmentTransaction.commit();
                                break;

                            case R.id.nav_about:
                                getSupportActionBar().setTitle(R.string.nav_item_about);
                                Fragment aboutFragment = new AboutFragment();
                                FragmentManager aboutFm = getFragmentManager();
                                FragmentTransaction aboutFragmentTransaction = aboutFm.beginTransaction();
                                aboutFragmentTransaction.replace(R.id.container_body, aboutFragment);
                                aboutFragmentTransaction.commit();
                                break;


                            case R.id.nav_contact:
                                getSupportActionBar().setTitle(R.string.nav_item_contact);
                                Fragment contactFragment = new ContactFragment();
                                FragmentManager contactFm = getFragmentManager();
                                FragmentTransaction contactFragmentTransaction = contactFm.beginTransaction();
                                contactFragmentTransaction.replace(R.id.container_body, contactFragment);
                                contactFragmentTransaction.commit();
                                break;


                            case R.id.nav_rate:
                                getSupportActionBar().setTitle(R.string.nav_item_rate);
                                Fragment rateFragment = new RateUsFragment();
                                FragmentManager rateFm = getFragmentManager();
                                FragmentTransaction rateFragmentTransaction = rateFm.beginTransaction();
                                rateFragmentTransaction.replace(R.id.container_body, rateFragment);
                                rateFragmentTransaction.commit();
                                break;

                            case R.id.nav_logout:
                                Intent intent = new Intent(BrowseOffersActivity.this, LogoutActivity.class);
                                startActivity(intent);
                                break;



                            default:

                                int itemInd = menuItem.getOrder();

                                menuItem.setCheckable(true);
                                menuItem.setChecked(true);
                                if (mPreviousMenuItem != null) {
                                    mPreviousMenuItem.setChecked(false);
                                }
                                mPreviousMenuItem = menuItem;

                                mBottomBar.setDefaultTabPosition(3);



                                String catName = Utils.stringToArray(jsonCatText, Category[].class).get(itemInd).getCatName();


                                Fragment browseOfferFragment = new BrowseOfferFragment();
                                getSupportActionBar().setTitle(catName);
                                Bundle args1 = new Bundle();
                                args1.putString(getString(R.string.ext_redir_to), "CategoryOffers");
                                args1.putString(getString(R.string.ext_category_id), String.valueOf(menuItem.getItemId()));
                                browseOfferFragment.setArguments(args1);


                                FragmentManager browseOfferFm = getFragmentManager();
                                FragmentTransaction browseOfferFragmentTransaction = browseOfferFm.beginTransaction();
                                browseOfferFragmentTransaction.replace(R.id.container_body, browseOfferFragment);
                                browseOfferFragmentTransaction.commit();

                                break;






                        }




                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void addItemsRunTime(NavigationView navigationView) {

        Gson gson = new Gson();
        jsonCatText = sharedpref.getString(getString(R.string.spf_categories), null);
        Log.d(LOGTAG, "Category JSON: "+jsonCatText);



        //String[] categories = gson.fromJson(jsonText, String[].class);  //EDIT: gso to gson
        categories = (ArrayList) gson.fromJson(jsonCatText, ArrayList.class);  //EDIT: gso to gson
        Log.d(LOGTAG, "Category JSON ArrayList: "+categories.size());


        String tempCatName = "";
        //adding items run time
        final Menu menu = navigationView.getMenu();
        int k = 0;
        for (int i = 0; i < categories.size(); i++) {
            //Category menuCat = categories.get(i);

            //String menuLabel = menuItem.getCatName()

            String catName = Utils.stringToArray(jsonCatText, Category[].class).get(i).getCatName();
            String parentId = Utils.stringToArray(jsonCatText, Category[].class).get(i).getParentId();
            String catId = Utils.stringToArray(jsonCatText, Category[].class).get(i).getId();

            //Random rand = new Random();

            //int ind = rand.nextInt(100);

            // add(int groupId, int itemId, int order, CharSequence title)


            if(Integer.parseInt(parentId) == 0 && !tempCatName.equalsIgnoreCase(catName)) {

                //menu.add(NavGroupId, Integer.parseInt(catId), k, catName).setIcon(R.drawable.ic_about);
                menu.add(NavGroupId, Integer.parseInt(catId), i, catName);

                tempCatName = catName;

            }

        }

        // adding a section and items into it
        /*final SubMenu subMenu = menu.addSubMenu("SubMenu Title");
        for (int i = 1; i <= 2; i++) {
            subMenu.add("SubMenu Item " + i);
        }*/

        // refreshing navigation drawer adapter
        for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {
            final View child = mNavigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        // Small hack here so that Lint does not warn me in every single activity about null
        // action bar
        return super.getSupportActionBar();
    }



    @Override
    protected void onResume() {
        super.onResume();
        //setUpMap();
    }




    public interface RemiMap {
        public void getMapData();
    }
}
