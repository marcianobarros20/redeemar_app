package com.tier5.redeemar.RedeemarConsumerApp;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.tier5.redeemar.RedeemarConsumerApp.async.FetchLocationAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.MenuItemsAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ActivityCommunicator;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.CategoriesLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.LocationFetchedListener;
import com.tier5.redeemar.RedeemarConsumerApp.database.DatabaseHelper;
import com.tier5.redeemar.RedeemarConsumerApp.exception.CrashActivity;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.AboutFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.BrowseOfferFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.ContactFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.EditProfileFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.FragmentDrawer;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.HelpFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.HomeFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.MapViewFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.MyOfferFragment;
import com.tier5.redeemar.RedeemarConsumerApp.fragments.RateUsFragment;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Product;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.GPSTracker;
import com.tier5.redeemar.RedeemarConsumerApp.utils.SuperConnectionDetector;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;


public class BrowseOffersActivity extends CrashActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ActivityCommunicator {

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
    private double latitude = 0.0, longitude = 0.0;
    private Resources res;
    private SharedPreferences sharedpref;
    private String user_id = "0", curFragment = "";
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
    private List<Category> categories;
    private int catId = 0;
    int lastClick = 0;
    private String redirectTo = "", redeemarId = "", campaignId = "", categoryId = "", jsonCatText = "", firstName = "", email = "", keyword = "", catName = "";
    private final int NavGroupId = 1001;
    private SharedPreferences.Editor editor;
    private TextView navWelcome, navEmail, navMyOffers, navMyCredits, navEditProfile;


    // For Multilevel Category Menu
    ArrayList<String> mParentCategoryIds = new ArrayList<>();
    ArrayList<String> mParentCategoryNames = new ArrayList<>();
    ArrayList<String> mCategoryIds = new ArrayList<>();
    ArrayList<String> mCategoryNames = new ArrayList<>();
    ArrayList<String> mSubCategoryIds = new ArrayList<>();
    private Product mMainProduct;
    private Product.SubCategory mSubProduct, mSubProduct2, mSubSubProduct;
    private Product.SubCategory.ItemList mSubProductItem;
    private ArrayList<Product> pProductArrayList;
    private ArrayList<Product.SubCategory> pSubItemArrayList;
    private ArrayList<Product.SubCategory>pSubItemArrayList2;
    private LinearLayout mLinearCategoryListView;
    private ArrayList<Product.SubCategory.ItemList> mItemListArray;
    private boolean isFirstViewClick = false;
    private boolean isSecondViewClick = false;
    boolean istrue=true;
    boolean istrue1=true;
    private int secondRowItemCount = 0, thirdRowItemCount = 0, mMenuCtr = 0, mSubMenuCtr = 0;
    private MenuItem actionView;
    private DatabaseHelper db;
    private ArrayList<String> pos = new ArrayList<>();
    private GPSTracker gps;
    private SuperConnectionDetector cd;
    private boolean isInternetPresent = false;



    /**
     * Permissions required to read and write contacts.
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_recycler);


        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0);
        editor = sharedpref.edit();
        db = new DatabaseHelper(this);



        pProductArrayList = new ArrayList<Product>();
        pSubItemArrayList2 = new ArrayList<Product.SubCategory>();


        if (sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
            String userId = sharedpref.getString(res.getString(R.string.spf_user_id), "");
            Log.d(LOGTAG, "User Id is: " + userId);
        }

        if (sharedpref.getString(res.getString(R.string.spf_first_name), null) != null) {
            firstName = sharedpref.getString(res.getString(R.string.spf_first_name), "");
        }

        if (sharedpref.getString(res.getString(R.string.spf_email), null) != null) {
            email = sharedpref.getString(res.getString(R.string.spf_email), "");
        }


        if (sharedpref.getString(res.getString(R.string.spf_redir_action), null) != null) {
            redirectTo = sharedpref.getString(res.getString(R.string.spf_redir_action), "");
        }


        if (sharedpref.getString(res.getString(R.string.spf_redeemer_id), null) != null) {
            redeemarId = sharedpref.getString(res.getString(R.string.spf_redeemer_id), "");
        }


        Log.d(LOGTAG, "Redeemar id: " + redeemarId);
        Log.d(LOGTAG, "Campaign id: " + campaignId);
        Log.d(LOGTAG, "Category id: " + categoryId);


        // Get Data from Intent
        Bundle extras = getIntent().getExtras();


        // Need to comment out

        if (extras != null) {
            redirectTo = extras.getString(getString(R.string.ext_redir_to));
            redeemarId = extras.getString(getString(R.string.ext_redeemar_id));

            if (redirectTo.equalsIgnoreCase("CampaignOffers")) {
                campaignId = extras.getString(getString(R.string.ext_campaign_id));
            }

            if (redirectTo.equalsIgnoreCase("CategoryOffers")) {
                categoryId = extras.getString(getString(R.string.ext_category_id));
            }

        }


        setupToolbar();

        setupBottombar(mBottomBar, savedInstanceState);
        initNavigationDrawer();
        //setUpMap();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView searchBox = (TextView) findViewById(R.id.search_text);
        //searchBox.setKeyListener(null);
        //searchBox.setEnabled(false);


        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d(LOGTAG, "Inside New Menu Browse Offer..");
            Intent intent = new Intent(BrowseOffersActivity.this, SearchActivity.class);
            startActivity(intent);
            //finish();
            }

        });


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.browse_offers);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu); // it's getSupportActionBar() if you're using AppCompatActivity, not getActionBar()
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
        //mBottomBar.useFixedMode();

        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                if (menuItemId == R.id.bottom_scan_offer) {

                    Intent intent = new Intent(BrowseOffersActivity.this, CloudReco.class);
                    startActivity(intent);

                } else if (menuItemId == R.id.bottom_my_offers) {

                    getSupportActionBar().setTitle(R.string.my_banked);

                    Fragment fr = new MyOfferFragment();
                    //fr.setArguments(args);
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

                    Toast.makeText(getApplicationContext(), "Browse offers selected", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle(R.string.browse_offers);

                    if (redirectTo.equalsIgnoreCase("EditProfile")) {

                        getSupportActionBar().setTitle(R.string.nav_item_edit_profile);
                        Fragment editProfileFragment = new EditProfileFragment();
                        FragmentManager editProfileFm = getFragmentManager();
                        FragmentTransaction editProfileFragmentTransaction = editProfileFm.beginTransaction();
                        editProfileFragmentTransaction.replace(R.id.container_body, editProfileFragment);
                        editProfileFragmentTransaction.commit();

                    } else {
                        Fragment fr = new BrowseOfferFragment();
                        Bundle args1 = new Bundle();

                        if (redirectTo.equalsIgnoreCase("BrandOffers")) {
                            getSupportActionBar().setTitle(R.string.offers_by_brand);
                            args1.putString(getString(R.string.ext_redir_to), "BrandOffers");
                            args1.putString(getString(R.string.ext_redeemar_id), redeemarId);
                        } else if (redirectTo.equalsIgnoreCase("CampaignOffers")) {

                            getSupportActionBar().setTitle(R.string.offers_by_campaign);
                            args1.putString(getString(R.string.ext_redir_to), "CampaignOffers");
                            args1.putString(getString(R.string.ext_redeemar_id), redeemarId);
                            args1.putString(getString(R.string.ext_campaign_id), campaignId);

                        } else if (redirectTo.equalsIgnoreCase("OnDemand")) {
                            Log.d(LOGTAG, "Inside OnDemand");
                            getSupportActionBar().setTitle(R.string.daily_deals);
                            args1.putString(getString(R.string.ext_redir_to), "onDemand");
                        }

                        /*editor.putString(getString(R.string.spf_search_keyword), ""); // Storing Search Keyword
                        editor.putString(getString(R.string.spf_category_id), ""); // Storing Category Id
                        editor.putString(getString(R.string.spf_category_name), ""); // Storing Category Name
                        editor.commit(); // commit changes*/

                        fr.setArguments(args1);
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fr);
                        fragmentTransaction.commit();
                    }


                } else if (menuItemId == R.id.bottom_daily_deals) {

                    editor.putString(getString(R.string.spf_redir_action), "OnDemand"); // Storing Redirect Action
                    editor.commit(); // commit changes

                    getSupportActionBar().setTitle(R.string.offers_on_demand);
                    Fragment fr = new BrowseOfferFragment();
                    Bundle args1 = new Bundle();
                    args1.putString(getString(R.string.ext_redir_to), "OnDemand");
                    fr.setArguments(args1);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();

                }

            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
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

                    //Intent intent = new Intent(MyOffersActivity.this, MyOffersActivity.class);
                    //startActivity(intent);

                } else if (menuItemId == R.id.bottom_nearby) {

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

                    // Open BrowseOffer
                    getSupportActionBar().setTitle(R.string.browse_offers);

                    editor.putString(getString(R.string.spf_search_keyword), "");   // Storing Search Keyword
                    editor.putString(getString(R.string.spf_category_id), "");      // Storing Category Id
                    editor.putString(getString(R.string.spf_category_name), "");    // Storing Category Name
                    editor.commit(); // commit changes

                    Fragment fr = new BrowseOfferFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();


                } else if (menuItemId == R.id.bottom_daily_deals) {
                    //Toast.makeText(getApplicationContext(), "You have logged out.", Toast.LENGTH_SHORT).show();
                    //Intent i = new Intent(getApplicationContext(), LogoutActivity.class);
                    //startActivity(i);
                    editor.putString(getString(R.string.spf_redir_action), "OnDemand"); // Storing Redirect Action
                    editor.commit(); // commit changes

                    getSupportActionBar().setTitle(R.string.offers_on_demand);
                    Fragment fr = new BrowseOfferFragment();
                    Bundle args1 = new Bundle();
                    args1.putString(getString(R.string.ext_redir_to), "OnDemand");
                    fr.setArguments(args1);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();


                }

            }
        });

    }


    private void initNavigationDrawer() {

        Log.d(LOGTAG, "Inside left navigation menu init");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //navWelcome = (TextView) findViewById(R.id.nav_welcome);


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
                        Log.d(LOGTAG, "Menu Item clicked " + menuItem.getItemId());

                        MenuItem mPreviousMenuItem = null;


                        switch (menuItem.getItemId()) {

                            case R.id.nav_my_offers:
                                getSupportActionBar().setTitle(R.string.nav_item_my_offers);
                                Fragment myOfferFragment = new MyOfferFragment();
                                FragmentManager myOfferFm = getFragmentManager();
                                FragmentTransaction myOfferFragmentTransaction = myOfferFm.beginTransaction();
                                myOfferFragmentTransaction.replace(R.id.container_body, myOfferFragment);
                                myOfferFragmentTransaction.commit();
                                break;


                            case R.id.nav_edit_profile:
                                getSupportActionBar().setTitle(R.string.nav_item_edit_profile);
                                Fragment editProfileFragment = new EditProfileFragment();
                                FragmentManager editProfileFm = getFragmentManager();
                                FragmentTransaction editProfileFragmentTransaction = editProfileFm.beginTransaction();
                                editProfileFragmentTransaction.replace(R.id.container_body, editProfileFragment);
                                editProfileFragmentTransaction.commit();
                                break;

                            case R.id.nav_offers:

                                editor.putString(getString(R.string.spf_redir_action), ""); // Storing Redirect Action
                                editor.putString(getString(R.string.spf_redeemer_id), ""); // Storing Redeemar id
                                editor.putString(getString(R.string.spf_campaign_id), ""); // Storing Campaign Id
                                editor.putString(getString(R.string.spf_category_id), ""); // Storing category Id
                                editor.putString(getString(R.string.spf_search_keyword), ""); // Storing Search Keyword
                                editor.commit(); // commit changes

                                getSupportActionBar().setTitle(R.string.nav_item_offers);
                                Fragment browseOfferFragment = new BrowseOfferFragment();
                                FragmentManager browseOfferFm = getFragmentManager();
                                FragmentTransaction browseOfferFragmentTransaction = browseOfferFm.beginTransaction();
                                browseOfferFragmentTransaction.replace(R.id.container_body, browseOfferFragment);
                                browseOfferFragmentTransaction.commit();
                                break;


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


                            case R.id.nav_export:

                                String logText = Utils.extractLogToFileAndWeb();
                                exportLog(logText);
                                break;


                            default:

                                int itemInd = menuItem.getOrder();

                                menuItem.setCheckable(true);
                                menuItem.setChecked(true);
                                if (mPreviousMenuItem != null) {
                                    mPreviousMenuItem.setChecked(false);
                                }
                                int catId = menuItem.getItemId();


                                editor.putInt(res.getString(R.string.spf_category_level), 1); // Storing category Level
                                editor.commit(); // commit changes

                               /* Intent catIntent = new Intent(BrowseOffersActivity.this, CategoryActivity.class);
                                catIntent.putExtra(getString(R.string.ext_redir_to), "CategoryOffers");
                                catIntent.putExtra(getString(R.string.ext_category_id), catId);
                                catIntent.putExtra(getString(R.string.ext_category_name), menuItem.getTitle());
                                startActivity(catIntent);*/

                                //Log.d(LOGTAG, "Category Name: "+menuItem.getTitle());

                                Fragment browseOfferFragment1 = new BrowseOfferFragment();
                                getSupportActionBar().setTitle(menuItem.getTitle());
                                Bundle args1 = new Bundle();
                                args1.putString(getString(R.string.ext_redir_to), "CategoryOffers");
                                args1.putString(getString(R.string.ext_category_id), String.valueOf(menuItem.getItemId()));
                                browseOfferFragment1.setArguments(args1);
                                break;

                                /*FragmentManager browseOfferFm1 = getFragmentManager();
                                FragmentTransaction browseOfferFragmentTransaction1 = browseOfferFm1.beginTransaction();
                                browseOfferFragmentTransaction1.replace(R.id.container_body, browseOfferFragment1);
                                browseOfferFragmentTransaction1.commit();*/


                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void addItemsRunTime(NavigationView navigationView) {


        List categories = db.getCategories(0);

        String tempCatName = "";
        final Menu menu = navigationView.getMenu();



        for (int i = 0; i < categories.size(); i++) {

            Category cat = (Category) categories.get(i);
            int catId = cat.getId();
            String catName = cat.getCatName();
            int parentId = cat.getParentId();

            if(parentId == 0) {
                mParentCategoryIds.add(String.valueOf(catId));
                mParentCategoryNames.add(catName);
            }
            mCategoryIds.add(String.valueOf(catId));
            mSubCategoryIds.add(String.valueOf(parentId));
            mCategoryNames.add(catName);

        }


        View headerView = navigationView.getHeaderView(0);
        mLinearCategoryListView = (LinearLayout) headerView.findViewById(R.id.category_listview);
        navWelcome = (TextView) headerView.findViewById(R.id.nav_welcome);

        if (!firstName.equalsIgnoreCase("")) {
            String welcomeText = "Hi " + firstName;
            navWelcome.setText(welcomeText);
        }


        navEmail = (TextView) headerView.findViewById(R.id.nav_user_email);

        if (!email.equalsIgnoreCase("")) {
            navEmail.setText(email);
        }

        navMyOffers = (TextView) headerView.findViewById(R.id.nav_my_offers);
        navMyCredits = (TextView) headerView.findViewById(R.id.nav_my_credits);
        navEditProfile = (TextView) headerView.findViewById(R.id.nav_edit_profile);


        navMyOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            closeNavDrawer();

            getSupportActionBar().setTitle(R.string.nav_item_my_offers);
            Fragment myOfferFragment = new MyOfferFragment();
            FragmentManager myOfferFm = getFragmentManager();
            FragmentTransaction myOfferFragmentTransaction = myOfferFm.beginTransaction();
            myOfferFragmentTransaction.replace(R.id.container_body, myOfferFragment);
            myOfferFragmentTransaction.commit();

            }

        });

        navMyCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNavDrawer();
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }

        });

        navEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            closeNavDrawer();

            getSupportActionBar().setTitle(R.string.nav_item_edit_profile);
            Fragment editProfileFragment = new EditProfileFragment();
            FragmentManager editProfileFm = getFragmentManager();
            FragmentTransaction editProfileFragmentTransaction = editProfileFm.beginTransaction();
            editProfileFragmentTransaction.replace(R.id.container_body, editProfileFragment);
            editProfileFragmentTransaction.commit();

            }

        });


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

        //if(categories.size() > 0)
        populateListMenuItems();
        setupListMenu();


    }

    @Override
    public void onBackPressed() {

        Log.d(LOGTAG, "Inside back pressed Browse");
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
        actionView = menu.findItem(R.id.action_view_type);

        if (sharedpref.getString(res.getString(R.string.spf_view_type), null) != null) {
            String viewType = sharedpref.getString(res.getString(R.string.spf_view_type), "");

            if(viewType.equals("thumb")) {
                actionView.setIcon(R.drawable.ic_thumb_white);
            }
            else if(viewType.equals("map")) {
                actionView.setIcon(R.drawable.ic_map_white);
            }
            else {
                actionView.setIcon(R.drawable.ic_list_white);
            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Log.d(LOGTAG, "Inside Menu BrowseOffer "+id+" "+R.id.action_view_thumb);



        switch (id) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


            case R.id.action_view_type:

                getSupportActionBar().setTitle(getString(R.string.browse_offers));

                if (sharedpref.getString(res.getString(R.string.spf_view_type), null) != null) {
                    String viewType1 = sharedpref.getString(res.getString(R.string.spf_view_type), "");
                    Log.d(LOGTAG, "Inside Action View Type: "+viewType1);


                    if(viewType1.equals("thumb")) {
                        //Toast.makeText(getApplicationContext(), "Map view coming soon", Toast.LENGTH_SHORT).show();
                        actionView.setIcon(R.drawable.ic_thumb_white);
                        editor.putString(getString(R.string.spf_view_type), "map"); // Storing View Type to Map
                    }

                    else if(viewType1.equals("map")) {
                        //Toast.makeText(getApplicationContext(), "List view option selected", Toast.LENGTH_SHORT).show();
                        actionView.setIcon(R.drawable.ic_list_white);
                        editor.putString(getString(R.string.spf_view_type), "list"); // Storing View Type to List
                    }

                    else {
                        //Toast.makeText(getApplicationContext(), "Thumb view option selected", Toast.LENGTH_SHORT).show();
                        actionView.setIcon(R.drawable.ic_map_white);
                        editor.putString(getString(R.string.spf_view_type), "thumb"); // Storing View Type to Thumb
                    }

                    editor.commit(); // commit changes


                    if(viewType1.equals("map")) {

                        Log.d(LOGTAG, "View Type: "+viewType1);
                        Fragment mapViewFragment = new MapViewFragment();
                        FragmentManager mapViewFm = getFragmentManager();
                        FragmentTransaction mapViewFragmentTransaction = mapViewFm.beginTransaction();
                        mapViewFragmentTransaction.replace(R.id.container_body, mapViewFragment);
                        mapViewFragmentTransaction.commit();
                    }
                    else {

                        Fragment browseOfferListFragment = new BrowseOfferFragment();
                        FragmentManager browseOfferFm = getFragmentManager();
                        FragmentTransaction browseOfferFragmentTransaction = browseOfferFm.beginTransaction();
                        browseOfferFragmentTransaction.replace(R.id.container_body, browseOfferListFragment);
                        browseOfferFragmentTransaction.commit();

                    }
                    return true;


                }


            /*
            case R.id.action_search:

                //Intent intent = new Intent(BrowseOffersActivity.this, CategoryActivity.class);

                //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                //startActivity(intent);
                return true;

            case R.id.action_view_thumb:
                Toast.makeText(getApplicationContext(), "Thumbnail view option selected", Toast.LENGTH_SHORT).show();


                editor.putString(getString(R.string.spf_view_type), "thumb"); // Storing User Id
                editor.commit(); // commit changes

                Fragment fr = new BrowseOfferFragment();
                //Bundle args1 = new Bundle();
                //fr.setArguments(args1);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fr);
                fragmentTransaction.commit();

                return true;
            */

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
        Log.d(LOGTAG, "Resuming the app");


    }

    @Override
    public void passDataToActivity(String currentFragment) {
        //Log.d(LOGTAG, "Data passed from fragment to activity: "+currentFragment);
        this.curFragment = currentFragment;
        //Log.d(LOGTAG, "BottomBar is: "+this.mBottomBar);


    }

    public void exportLog(String logText) {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Redeemar Log");
        emailIntent.putExtra(Intent.EXTRA_TEXT, logText);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.d(LOGTAG, "Finished sending email...");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(BrowseOffersActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BrowseOffers Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tier5.redeemar.RedeemarConsumerApp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BrowseOffers Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tier5.redeemar.RedeemarConsumerApp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void populateListMenuItems() {


        /*if(categories.size() > 0) {

            for (int i = 0; i < categories.size(); i++) {

                Category cat = (Category) categories.get(i);
                int catId = cat.getId();
                String catName = cat.getCatName();
                int parentId = cat.getParentId();

                if(parentId == 0) {
                    mParentCategoryIds.add(String.valueOf(catId));
                    mParentCategoryNames.add(catName);
                }
                mCategoryIds.add(String.valueOf(catId));
                mSubCategoryIds.add(String.valueOf(parentId));
                mCategoryNames.add(catName);

            }
        }*/


        for(int i=0; i<mParentCategoryIds.size(); i++) {
            istrue=true;
            pSubItemArrayList=new ArrayList<Product.SubCategory>();
            for(int j=0;j<mSubCategoryIds.size();j++){
                istrue1=true;
                if(mParentCategoryIds.get(i).equals(mSubCategoryIds.get(j))) {

                    if(istrue) {
                        mItemListArray = new ArrayList<Product.SubCategory.ItemList>();

                        //if(i == 0)
                        //    mItemListArray.add(new Product.SubCategory.ItemList("All", "0"));


                        // Main Category
                        pProductArrayList.add(new Product(mParentCategoryIds.get(i), mParentCategoryNames.get(i), pSubItemArrayList, false));
                        istrue = false;
                    }

                    for(int k=0; k<mCategoryIds.size(); k++) {
                        if(mCategoryIds.get(j).equals(mSubCategoryIds.get(k))) {

                            if(istrue1) {
                                mItemListArray=new ArrayList<Product.SubCategory.ItemList>();
                                istrue1=false;
                            }

                            mItemListArray.add(new Product.SubCategory.ItemList(mCategoryIds.get(k), mCategoryNames.get(k)));
                        }
                    }
                    // Sub Category
                    pSubItemArrayList.add(new Product.SubCategory(mCategoryIds.get(j), mCategoryNames.get(j), mItemListArray, false));

                    //Log.d("MENU", "BB1 "+mCategoryIds.get(j));
                    //Log.d("MENU", "BB2 "+mCategoryNames.get(j));
                    mItemListArray=new ArrayList<Product.SubCategory.ItemList>();
                }
            }
        }

    }


    public void setupListMenu() {

        int nctr = 0; int pctr = 0;

        for (int i = 0; i < pProductArrayList.size(); i++) {

            mMenuCtr = i;

            pos.add(String.valueOf(i));

            LayoutInflater inflater = null;
            inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLinearView = inflater.inflate(R.layout.row_cat_first, null);

            final TextView mProductName = (TextView) mLinearView.findViewById(R.id.textViewName);
            final RelativeLayout mLinearFirstArrow=(RelativeLayout)mLinearView.findViewById(R.id.linearFirst);
            final ImageView mImageArrowFirst=(ImageView)mLinearView.findViewById(R.id.imageFirstArrow);
            final LinearLayout mLinearScrollSecond=(LinearLayout)mLinearView.findViewById(R.id.linear_scroll);

            Product pProd = pProductArrayList.get(i);

            mLinearFirstArrow.setId(i);

            if(pProd.getOpened()==false) {
                mLinearScrollSecond.setVisibility(View.GONE);
                mImageArrowFirst.setBackgroundResource(R.drawable.ic_down);
            }
            else {
                mLinearScrollSecond.setVisibility(View.VISIBLE);
                mImageArrowFirst.setBackgroundResource(R.drawable.ic_up);
            }

            secondRowItemCount = pProductArrayList.get(i).getmSubCategoryList().size();

            mLinearFirstArrow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int xn = mLinearFirstArrow.getId();

                    lastClick = mMenuCtr;


                    mMainProduct = pProductArrayList.get(xn);
                    isFirstViewClick = mMainProduct.getOpened();
                    secondRowItemCount = mMainProduct.getmSubCategoryList().size();

                    Log.d(LOGTAG, "A main menu clicked "+xn+" : "+isFirstViewClick+" "+secondRowItemCount);

                    if(secondRowItemCount > 0) {

                        for (int k=0; k<pos.size(); k++){
                            //isFirstViewClick=false;
                            View vi = mLinearCategoryListView.getChildAt(k);
                            final ImageView mImageArrowFirst=(ImageView)vi.findViewById(R.id.imageFirstArrow);
                            final LinearLayout mLinearScrollSecond=(LinearLayout)vi.findViewById(R.id.linear_scroll);
                            mImageArrowFirst.setBackgroundResource(R.drawable.ic_down);
                            mLinearScrollSecond.setVisibility(View.GONE);
                            //pProductArrayList.clear();
                        }

                        if (isFirstViewClick == false) {
                            mImageArrowFirst.setBackgroundResource(R.drawable.ic_up);
                            mLinearScrollSecond.setVisibility(View.VISIBLE);
                            mMainProduct.setOpened(true);
                        } else {
                            mImageArrowFirst.setBackgroundResource(R.drawable.ic_down);
                            mLinearScrollSecond.setVisibility(View.GONE);
                            mMainProduct.setOpened(false);
                        }
                        pProductArrayList.set(mMenuCtr, mMainProduct);
                    }
                    else {
                        Log.d(LOGTAG, "First level menu clicked");
                    }

                }
            });

            ////////////////////////////// SUB CATEGORY STARTS //////////////////////////////////

            final String name = pProductArrayList.get(i).getpName();
            mProductName.setText(name);

            for (int j = 0; j < secondRowItemCount; j++) {

                mSubMenuCtr = j;

                LayoutInflater inflater2 = null;
                inflater2 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mLinearView2 = inflater2.inflate(R.layout.row_cat_second, null);

                TextView mSubItemName = (TextView) mLinearView2.findViewById(R.id.textViewTitle);
                final RelativeLayout mLinearSecondArrow=(RelativeLayout)mLinearView2.findViewById(R.id.linearSecond);
                final ImageView mImageArrowSecond=(ImageView)mLinearView2.findViewById(R.id.imageSecondArrow);
                final LinearLayout mLinearScrollThird=(LinearLayout)mLinearView2.findViewById(R.id.linear_scroll_third);
                mLinearSecondArrow.setId(j);

                mSubProduct = pProductArrayList.get(mMenuCtr).getmSubCategoryList().get(j);

                thirdRowItemCount = pProductArrayList.get(i).getmSubCategoryList().get(j).getmItemListArray().size();

                //Log.d(LOGTAG, "Third Row Count: "+thirdRowItemCount);
                //thirdRowItemCount = mSubProduct.getmItemListArray().size();

                if(isSecondViewClick==false) {
                    mLinearScrollThird.setVisibility(View.GONE);
                    mImageArrowSecond.setBackgroundResource(R.drawable.ic_down);
                }
                else {
                    mLinearScrollThird.setVisibility(View.VISIBLE);
                    mImageArrowSecond.setBackgroundResource(R.drawable.ic_up);
                }


                mSubItemName.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        int xm = mLinearSecondArrow.getId();

                        mSubProduct2 = pProductArrayList.get(mMenuCtr).getmSubCategoryList().get(xm);

                        isSecondViewClick = mSubProduct2.getOpened();
                        thirdRowItemCount = mSubProduct2.getmItemListArray().size();
                        Log.d(LOGTAG, "A new sub menu clicked "+xm+" : "+isSecondViewClick+" "+thirdRowItemCount);


                        Fragment browseOfferFragment1 = new BrowseOfferFragment();
                        getSupportActionBar().setTitle(mSubProduct2.getpSubCatName());
//                        Bundle args1 = new Bundle();
//                        args1.putString(getString(R.string.ext_redir_to), "CategoryOffers");
//                        args1.putString(getString(R.string.ext_category_id), String.valueOf(mSubProduct2.getpId()));
//                        args1.putString(getString(R.string.ext_category_name), String.valueOf(mSubProduct2.getpSubCatName()));
//                        browseOfferFragment1.setArguments(args1);

                        editor.putString(res.getString(R.string.spf_redir_action), "CategoryOffers");
                        editor.putString(res.getString(R.string.spf_category_name), mSubProduct2.getpSubCatName());
                        editor.putString(res.getString(R.string.spf_category_id), mSubProduct2.getpId());
                        editor.commit();


                        FragmentManager browseOfferFm1 = getFragmentManager();
                        FragmentTransaction browseOfferFragmentTransaction1 = browseOfferFm1.beginTransaction();
                        browseOfferFragmentTransaction1.replace(R.id.container_body, browseOfferFragment1);
                        browseOfferFragmentTransaction1.commit();

                        if(thirdRowItemCount > 0) {

                            if(isSecondViewClick == false) {
                                mImageArrowSecond.setBackgroundResource(R.drawable.ic_up);
                                mLinearScrollThird.setVisibility(View.VISIBLE);
                                mSubProduct2.setOpened(true);
                            } else {
                                mImageArrowSecond.setBackgroundResource(R.drawable.ic_down);
                                mLinearScrollThird.setVisibility(View.GONE);
                                mSubProduct2.setOpened(false);
                            }

                        }
                        else {

                            /*Fragment browseOfferFragment1 = new BrowseOfferFragment();
                            getSupportActionBar().setTitle(mSubProduct2.getpSubCatName());
                            Bundle args1 = new Bundle();

                            args1.putString(getString(R.string.ext_redir_to), "CategoryOffers");
                            args1.putString(getString(R.string.ext_category_id), String.valueOf(mSubProduct2.getpId()));
                            args1.putString(getString(R.string.ext_category_name), String.valueOf(mSubProduct2.getpSubCatName()));
                            browseOfferFragment1.setArguments(args1);

                            editor.putString(res.getString(R.string.spf_redir_action), "CategoryOffers");
                            editor.putString(res.getString(R.string.spf_category_name), mSubProduct2.getpSubCatName());
                            editor.putString(res.getString(R.string.spf_category_id), mSubProduct2.getpId());
                            editor.commit();


                            FragmentManager browseOfferFm1 = getFragmentManager();
                            FragmentTransaction browseOfferFragmentTransaction1 = browseOfferFm1.beginTransaction();
                            browseOfferFragmentTransaction1.replace(R.id.container_body, browseOfferFragment1);
                            browseOfferFragmentTransaction1.commit();*/

                            if(thirdRowItemCount == 0) {
                                mDrawerLayout.closeDrawers();
                            }
                        }

                    }
                });




                final String catName = pProductArrayList.get(i).getmSubCategoryList().get(j).getpSubCatName();
                mSubItemName.setText(catName);

                if(thirdRowItemCount == 0)
                    mImageArrowSecond.setVisibility(View.INVISIBLE);
                else
                {

                    mImageArrowSecond.setVisibility(View.VISIBLE);

                    for (int k = 0; k < thirdRowItemCount; k++) {

                        LayoutInflater inflater3 = null;
                        inflater3 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View mLinearView3 = inflater3.inflate(R.layout.row_cat_third, null);
                        final RelativeLayout mLinearThirdArrow=(RelativeLayout)mLinearView3.findViewById(R.id.linearThird);
                        mLinearThirdArrow.setId(k);

                        mSubSubProduct = pProductArrayList.get(i).getmSubCategoryList().get(j);

                        TextView mItemName = (TextView) mLinearView3.findViewById(R.id.textViewItemName);
                        final String itemId = mSubSubProduct.getmItemListArray().get(k).getId();
                        final String itemName = mSubSubProduct.getmItemListArray().get(k).getItemName();
                        mItemName.setText(itemName);

                        mItemName.setOnTouchListener(new View.OnTouchListener() {

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                            int xp = mLinearThirdArrow.getId();

                            Fragment browseOfferFragment1 = new BrowseOfferFragment();
                            getSupportActionBar().setTitle(itemName);
//                            Bundle args1 = new Bundle();
//                            args1.putString(getString(R.string.ext_redir_to), "CategoryOffers");
//                            args1.putString(getString(R.string.ext_category_id), String.valueOf(itemId));
//                            browseOfferFragment1.setArguments(args1);

                            editor.putString(getString(R.string.spf_redir_action), "CategoryOffers"); // Storing Redirect Action
                            editor.putString(getString(R.string.spf_category_id), String.valueOf(String.valueOf(itemId))); // Storing Redirect Action
                            editor.putString(getString(R.string.spf_category_name), itemName); // Storing Redirect Action
                            editor.commit(); // commit changes

                            FragmentManager browseOfferFm1 = getFragmentManager();
                            FragmentTransaction browseOfferFragmentTransaction1 = browseOfferFm1.beginTransaction();
                            browseOfferFragmentTransaction1.replace(R.id.container_body, browseOfferFragment1);
                            browseOfferFragmentTransaction1.commit();

                            //Log.d(LOGTAG, "Third level menu id: "+itemId);
                            mDrawerLayout.closeDrawers();
                            return false;
                            }
                        });



                        mLinearScrollThird.addView(mLinearView3);
                        nctr++;
                    }
                }

                mLinearScrollSecond.addView(mLinearView2);
                nctr++;
                pctr++;

            }

            mLinearCategoryListView.addView(mLinearView);
            nctr++;
            pctr++;
        }

    }




}
