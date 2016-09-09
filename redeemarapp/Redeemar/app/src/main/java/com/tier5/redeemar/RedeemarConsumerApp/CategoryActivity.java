package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.adapters.BrowseOffersViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.adapters.CategoryViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.database.DatabaseHelper;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private static final String LOGTAG = "CategoryActivity";

    private RecyclerView mRecyclerView;
    private CategoryViewAdapter mAdapter;
    private Toolbar toolbar;
    private DatabaseHelper db;
    private int catId = 0;
    private String catName = "";
    private SharedPreferences sharedpref;
    private int catLevel = 0;

    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        db = new DatabaseHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));


        mRecyclerView = (RecyclerView) findViewById(R.id.category_recycler_view);
        mAdapter = new CategoryViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        sharedpref = getSharedPreferences(getString(R.string.spf_key), 0); // 0 - for private mode

        if(sharedpref.getInt(getString(R.string.spf_category_level), 0) != 0) {
            catLevel = sharedpref.getInt(getString(R.string.spf_category_level), 0);
        }


        Bundle extras = getIntent().getExtras();

        // Check whether the usre is validating the
        if (extras != null) {

            String activity = extras.getString(getString(R.string.ext_activity));
            catId = extras.getInt(getString(R.string.ext_category_id));
            catName = extras.getString(getString(R.string.ext_category_name));

            Log.d(LOGTAG, "Cat Id: " + catId);
            Log.d(LOGTAG, "Cat Name: " + catName);
            Log.d(LOGTAG, "Cat Level: " + catLevel);

            ArrayList categories = (ArrayList<Category>) db.getCategories(catId);

            /*Category anyCat = new Category();
            anyCat.setId(catId);
            anyCat.setCatName("All");

            categories.add(0, anyCat);*/

            Log.d(LOGTAG, "Count Cats: " + categories.size());
            mAdapter.setCategories(categories, catId);

        }


        if (toolbar != null) {
            setSupportActionBar(toolbar);

            int counter = db.countCategories(catId);


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

                    Log.d(LOGTAG, "Category Level: "+catLevel);

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
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.menu_main, menu);
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
                Toast.makeText(CategoryActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
