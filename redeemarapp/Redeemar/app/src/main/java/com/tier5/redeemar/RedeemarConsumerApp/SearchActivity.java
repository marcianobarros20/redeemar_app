package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.adapters.SearchViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.SearchFullAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.SearchLocationAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.SearchLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Search;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements SearchLoadedListener {

    private static final String LOGTAG = "Search";

    private TextView tvEmptyView;
    private EditText txtLocation, txtKeyword, txtSearch;
    private SharedPreferences sharedpref;
    private RecyclerView mRecyclerView;
    private ArrayList<Search> mListAddr;
    private SearchViewAdapter mAdapter;
    private Toolbar toolbar;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog pd;

    String keyword = "";
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        txtSearch = (EditText) findViewById(R.id.search_box);
        tvEmptyView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);

        mListAddr = new ArrayList<Search>();

        mAdapter = new SearchViewAdapter(this, mListAddr);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter( mAdapter );


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.search);

            EditText searchBox = (EditText) findViewById(R.id.search_text);

            //Your toolbar is now an action bar and you can use it like you always do, for example:
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // back button pressed
                    finish();
                }
            });

        }



        txtSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                        tvEmptyView.setText(R.string.loading);
                    }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { tvEmptyView.setText(R.string.loading); }

                    private Timer timer=new Timer();
                    private final long DELAY = 500; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {

                                        keyword = txtSearch.getText().toString();
                                        tvEmptyView.setText(R.string.loading);
                                        // TODO: do what you need here (refresh list)
                                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                                        Log.d(LOGTAG, "Calling the async task to load after 2 seconds "+keyword);
                                        callSearchKeywordTask();
                                    }
                                },
                                DELAY
                        );
                    }
                }


        );



    }

    private void callSearchKeywordTask() {

        //tvEmptyView.setText(R.string.loading);
        //tvEmptyView.setVisibility(View.VISIBLE);

        keyword = txtSearch.getText().toString();


        if(!keyword.equals(""))
            new SearchFullAsyncTask(this, getApplicationContext()).execute(keyword);
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
                Toast.makeText(SearchActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onSearchLoaded(ArrayList<Search> listKeywords) {

        Log.d(LOGTAG, "Inside Search Adapter: "+listKeywords.size());

        if (listKeywords.size() > 0) {
            tvEmptyView.setVisibility(View.GONE);
            mAdapter = new SearchViewAdapter(this, listKeywords);
            mAdapter.setKeyword(listKeywords);
            mRecyclerView.setAdapter(mAdapter);
        }
        else
            tvEmptyView.setText(R.string.no_search_result);

    }
}
