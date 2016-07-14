package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.os.Bundle;


public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_success);

        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uniqueTargetId = extras.getString("unique_target_id");
            TextView hwTextView = (TextView)findViewById(R.id.tvUniqueTargetId);
            //hwTextView.setText("Unique Id: " + String.valueOf(uniqueTargetId));
            //new ValidateLogoAsyncTask().execute(uniqueTargetId);
        }


        Intent intent = new Intent(LoadingActivity.this, BrowseOffersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        */





    }

    /*
    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        Log.d("RDM", "Inside back button pressed in display success");
        Intent intent = new Intent(DisplaySuccessActivity.this, AboutScreen.class);
        startActivity(intent);
        finish();
    }*/
}
