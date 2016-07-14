package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;


public class DisplaySuccessActivity extends Activity {

    TextView tvSuccessMessage, tvUniqueTargetId;
    Typeface myFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_success);

        tvSuccessMessage = (TextView) findViewById(R.id.tvSuccessMessage);
        tvUniqueTargetId = (TextView) findViewById(R.id.tvUniqueTargetId);

        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));

        tvSuccessMessage.setTypeface(myFont);
        tvUniqueTargetId.setTypeface(myFont);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uniqueTargetId = extras.getString("unique_target_id");
            TextView hwTextView = (TextView)findViewById(R.id.tvUniqueTargetId);
            //hwTextView.setText("Unique Id: " + String.valueOf(uniqueTargetId));
            //new ValidateLogoAsyncTask().execute(uniqueTargetId);
        }




    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }
}
