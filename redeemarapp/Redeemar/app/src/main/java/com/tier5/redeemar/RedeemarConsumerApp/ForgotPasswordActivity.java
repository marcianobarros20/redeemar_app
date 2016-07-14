package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ForgotPasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_success);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uniqueTargetId = extras.getString("unique_target_id");
            TextView hwTextView = (TextView)findViewById(R.id.tvUniqueTargetId);
            //hwTextView.setText("Unique Id: " + String.valueOf(uniqueTargetId));
            //new ValidateLogoAsyncTask().execute(uniqueTargetId);
        }




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
