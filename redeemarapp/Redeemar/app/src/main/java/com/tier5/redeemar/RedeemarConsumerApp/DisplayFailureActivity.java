package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayFailureActivity extends Activity {

    TextView tvSuccessMessage;
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_failure);

        tvSuccessMessage = (TextView) findViewById(R.id.tvSuccessMessage);
        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));
        tvSuccessMessage.setTypeface(myFont);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String scan_err = extras.getString(getString(R.string.ext_scan_err));

            if(scan_err.equalsIgnoreCase("R01002"))
                tvSuccessMessage.setText(getString(R.string.brand_not_associated));

            else if(scan_err.equalsIgnoreCase("R01003"))
                tvSuccessMessage.setText(getString(R.string.brand_video_not_found));

            else if(scan_err.equalsIgnoreCase("R02001")) {
                tvSuccessMessage.setText(getString(R.string.error_validation_success));
                //getActionBar().setTitle(R.string.success);
            }

            else if(scan_err.equalsIgnoreCase("R02002"))
                tvSuccessMessage.setText(getString(R.string.error_offer_expired));

            else if(scan_err.equalsIgnoreCase("R02003"))
                tvSuccessMessage.setText(getString(R.string.error_wrong_target));

            else if(scan_err.equalsIgnoreCase("R02004"))
                tvSuccessMessage.setText(getString(R.string.error_duplicate_validation));

            else if(scan_err.equalsIgnoreCase("R02005"))
                tvSuccessMessage.setText(getString(R.string.error_validation_wrong_place));


            else if(scan_err.equalsIgnoreCase("R02010")) {

                tvSuccessMessage.setText(getString(R.string.error_cgi_animation));
            }

            else
                tvSuccessMessage.setText(scan_err);

        }
    }


    @Override
    public void onBackPressed()
    {
        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();*/

        Intent sIntent = new Intent(getApplicationContext(), BrowseOffersActivity.class);
        startActivity(sIntent);
    }
}
