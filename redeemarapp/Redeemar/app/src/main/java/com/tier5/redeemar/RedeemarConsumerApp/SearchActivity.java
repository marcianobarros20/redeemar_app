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
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.adapters.SearchViewAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.async.SearchFullAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.SearchLocationAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.UsersLoadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements UsersLoadedListener, TaskCompleted {

    private static final String LOGTAG = "Search";

    //private TextView tvAddress, tvOfferTitle, tvWhatYouGet, tvPriceRangeId, tvPayValue, tvDiscount, tvRetailValue, tvExpires;
    private EditText txtLocation, txtKeyword;
    private Button btnSearch;
    private SharedPreferences sharedpref;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mListAddr;
    private SearchViewAdapter mAdapter;
    private Toolbar toolbar;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog pd;

    String location = "", keyword = "";
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        txtLocation = (EditText) findViewById(R.id.location);
        txtKeyword = (EditText) findViewById(R.id.keyword);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);

        mListAddr = new ArrayList<>();

        mAdapter = new SearchViewAdapter(this);
        mAdapter.setAddresses(mListAddr);
        //mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter( mAdapter );


        btnSearch = (Button) findViewById(R.id.btnSearch);



        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.search);

            //setSupportActionBar(toolbar);
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



        txtLocation.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

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
                                        // TODO: do what you need here (refresh list)
                                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                                        Log.d(LOGTAG, "Calling the async task to load after 2 seconds "+location);
                                        callSearchLocationTask();
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );








        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
                Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                boolean flag = true;
                int errMsg = 0;


                location = txtLocation.getText().toString();
                keyword = txtKeyword.getText().toString();

                if(location.equals("") && keyword.equals("")) {

                    flag = false;
                    errMsg = R.string.enter_location_or_keyword;
                    //txtLocation.requestFocus();

                    builder = new AlertDialog.Builder(getApplicationContext());//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(errMsg));
                    alertDialog = builder.create();

                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);
                    alertDialog.show();
                }


                else {

                    callSearchFullTask();
//                    pd = new ProgressDialog(getApplicationContext());
//                    pd.setMessage(getString(R.string.sending_data));
//                    pd.show();



                }
            }
        });

    }

    private void callSearchFullTask() {


        location = txtLocation.getText().toString();
        keyword = txtKeyword.getText().toString();

        if(!location.equals("") || !keyword.equals(""))
            new SearchFullAsyncTask(this, getApplicationContext()).execute(location, keyword);
    }

    private void callSearchLocationTask() {

        location = txtLocation.getText().toString();
        if(!location.equals(""))
            new SearchLocationAsyncTask(this).execute(location);
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
    public void onUsersLoaded(ArrayList<User> listAddresses) {

        Log.d(LOGTAG, "Inside Location Adapter: "+listAddresses.size());

        if (listAddresses.size() > 0) {
            mAdapter = new SearchViewAdapter(this);
            mAdapter.setAddresses(listAddresses);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onTaskComplete(String result) {

    }
}
