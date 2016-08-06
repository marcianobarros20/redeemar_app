package com.tier5.redeemar.RedeemarConsumerApp.exception;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;

public class ActivityExceptionDemo extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("ActivityExceptionDemo", "ActivityExceptionDemo");
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				// TODO Auto-generated method stub
			    final String DOUBLE_LINE_SEP = "\r\n\r\n";
                final String SINGLE_LINE_SEP = "\r\n";
                StackTraceElement[] arr = e.getStackTrace();
                final StringBuffer report = new StringBuffer(e.toString());
                final String lineSeperator = "-------------------------------\n\n";
                report.append(DOUBLE_LINE_SEP);
                report.append("--------- Stack trace ---------\n\n");
                report.append(arr.length);
                report.append("\n\n");

                for (int i = 0; i < arr.length; i++) {
                    report.append("    ");
                    report.append(arr[i].toString());
                    report.append(SINGLE_LINE_SEP);
                }
 
                // If the exception was thrown in a background thread inside
                // AsyncTask, then the actual exception can be found with
                // getCause
                Throwable cause = e.getCause();
                if (cause != null) {
                    report.append(lineSeperator);
                    report.append("--------- Cause ---------\n\n");
                    report.append(cause.toString());
                    report.append(DOUBLE_LINE_SEP);
                    arr = cause.getStackTrace();
                    for (int i = 0; i < arr.length; i++) {
                        report.append("    ");
                        report.append(arr[i].toString());
                        report.append(SINGLE_LINE_SEP);
                    }
                }

                Log.d("Redeemar", "Error is: "+report.toString());
                // Getting the Device brand,model and sdk verion details.
                report.append(lineSeperator);
                report.append("--------- Device ---------\n\n");
                report.append("Brand: ");
                report.append(Build.BRAND);
                report.append(SINGLE_LINE_SEP);
                report.append("Device: ");
                report.append(Build.DEVICE);
                report.append(SINGLE_LINE_SEP);
                report.append("Model: ");
                report.append(Build.MODEL);
                report.append(SINGLE_LINE_SEP);
                report.append("Metric: ");
 
                int density = getResources().getDisplayMetrics().densityDpi;
 
                switch (density) {
                case DisplayMetrics.DENSITY_LOW:
                    report.append("LDPI ");
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    report.append("MDPI ");
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    report.append("HDPI ");
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    report.append("XHDPI ");
                    break;
                }
 
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
 
                report.append(String.valueOf(dm.widthPixels) + "x" + String.valueOf(dm.heightPixels) + "  " + String.valueOf(dm.densityDpi) + "dpi") ;
                report.append(SINGLE_LINE_SEP);
                report.append("Id: ");
                report.append(Build.ID);
                report.append(SINGLE_LINE_SEP);
                report.append("Product: ");
                report.append(Build.PRODUCT);
                report.append(SINGLE_LINE_SEP);
                report.append(lineSeperator);
                report.append("--------- Firmware ---------\n\n");
                report.append("SDK: ");
                report.append(Build.VERSION.SDK);
                report.append(SINGLE_LINE_SEP);
                report.append("Release: ");
                report.append(Build.VERSION.RELEASE);
                report.append(SINGLE_LINE_SEP);
                report.append("Incremental: ");
                report.append(Build.VERSION.INCREMENTAL);
                report.append(SINGLE_LINE_SEP);
                report.append(lineSeperator);

 
                Intent crashedIntent = new Intent(ActivityExceptionDemo.this, CrashActivity.class);
                Log.d("Redeemar", "Error is: "+report.toString());
                crashedIntent.putExtra("stacktrace", report.toString());
                crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(crashedIntent);
                System.exit(0);
            }
			
		});
		
		

	}

	


	

}
