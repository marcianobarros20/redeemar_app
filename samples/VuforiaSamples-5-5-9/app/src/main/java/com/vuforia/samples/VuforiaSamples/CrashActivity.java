package com.vuforia.samples.VuforiaSamples;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CrashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.crash_report);
		 
	        final String stackTrace = getIntent().getStringExtra("stacktrace");
	        final TextView reportTextView = (TextView) findViewById(R.id.txtCrash);
	        reportTextView.setText(stackTrace);
	    }
	 
	    public void btnSend_Click(View view) {
	        final TextView t = (TextView) findViewById(R.id.txtCrash);
	 
	        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
	                "mailto", "dibs439@gmail.com", null));
	        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Safelife Crash Report");
	        emailIntent.putExtra(Intent.EXTRA_TEXT, t.getText().toString());
	        startActivity(Intent.createChooser(emailIntent, "Send error to author..."));
	    }
	
		

	

	

	
	
}
