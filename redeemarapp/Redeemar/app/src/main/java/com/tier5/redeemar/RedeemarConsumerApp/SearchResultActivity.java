package com.tier5.redeemar.RedeemarConsumerApp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.CategoryViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.OffersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.database.DatabaseHelper;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements OffersLoadedListener {

    private static final String LOGTAG = "SearchResultActivity";

    private RecyclerView mRecyclerView;
    private BrowseOffersViewAdapter mAdapter;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerOffers;

    private Toolbar toolbar;
    private DatabaseHelper db;
    private int catId = 0;
    private String catName = "";

    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        db = new DatabaseHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);

        mAdapter = new BrowseOffersViewAdapter(this, "BrowseOffers");
        mRecyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();

        // Check whether the usre is validating the
        if (extras != null) {

            String activity = extras.getString(getString(R.string.ext_activity));
            catId = extras.getInt(getString(R.string.ext_category_id));
            catName = extras.getString(getString(R.string.ext_category_name));
            String catLevel = extras.getString(getString(R.string.ext_category_level));

            Log.d(LOGTAG, "Cat Id: " + catId);
            Log.d(LOGTAG, "Cat Name: " + catName);
            Log.d(LOGTAG, "Cat Level: " + catLevel);



        }


        if (toolbar != null) {
            setSupportActionBar(toolbar);



            if(!catName.equals(""))
                getSupportActionBar().setTitle(catName);
            else
                getSupportActionBar().setTitle(R.string.category);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            //Your toolbar is now an action bar and you can use it like you always do, for example:
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // back button pressed
                finish();
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
                }
            });

        }

    }


    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
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
                Toast.makeText(SearchResultActivity.this, "Search is selected", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onOffersLoaded(ArrayList<Offer> listOffers) {

    }
}
