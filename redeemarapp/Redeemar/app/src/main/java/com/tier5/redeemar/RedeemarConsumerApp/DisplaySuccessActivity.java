package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class DisplaySuccessActivity extends Activity {

    TextView tvSuccessMessage;
    Button btnContinue;
    Typeface myFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_success);

        tvSuccessMessage = (TextView) findViewById(R.id.tvSuccessMessage);
        btnContinue = (Button)  findViewById(R.id.btnContinue);

        myFont = Typeface.createFromAsset(getAssets(), getString(R.string.default_font));
        tvSuccessMessage.setTypeface(myFont);


        btnContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


            Intent sIntent = new Intent(getApplicationContext(), BrowseOffersActivity.class);
            startActivity(sIntent);
            finish();

            }
        });



    }


    @Override
    public void onBackPressed()
    {

        Intent sIntent = new Intent(getApplicationContext(), CloudReco.class);
        startActivity(sIntent);
        finish();

        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();*/
    }
}
