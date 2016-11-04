package com.tier5.redeemar.RedeemarConsumerApp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.tier5.redeemar.RedeemarConsumerApp.MyApplication;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;


/**
 * Created by Dibs on 29/07/15.
 */
public class HelpFragment extends Fragment {

    private TextView tvTitle, tvSubTitle;

    private Switch beaconSwitch;
    String appInfo = "";
    String appVersion = "";

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        tvTitle = (TextView) rootView.findViewById(R.id.title);
        tvSubTitle = (TextView) rootView.findViewById(R.id.subTitle);
        beaconSwitch = (Switch) rootView.findViewById(R.id.beaconSwitch);

        res = getResources();
        sharedpref = getActivity().getSharedPreferences(res.getString(R.string.spf_key), 0);
        editor = sharedpref.edit();


        //set the switch to ON
        beaconSwitch.setChecked(true);
        //attach a listener to check for changes in state
        beaconSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Storing Beacon Triggered
                if(isChecked){
                    // In case of Switch being set to ON, app will again start listening for Beacon
                    editor.putString(getString(R.string.spf_beacon_triggered), "0");
                    editor.commit();

                } else {
                    // In case of Switch being set to OFF, app will stop listening
                    editor.putString(getString(R.string.spf_beacon_triggered), "1");
                    editor.commit();
                }

            }
        });

        /*//check the current state before we display the screen
        if(beaconSwitch.isChecked()){
            //switchStatus.setText("Switch is currently ON");
        }
        else {
            //switchStatus.setText("Switch is currently OFF");
        }*/



        /*if(UrlEndpoints.serverBaseUrl.equalsIgnoreCase("http://beta.redeemar.com/")) {
            appInfo = getString(R.string.app_beta);
        }
        else if(UrlEndpoints.serverBaseUrl.equalsIgnoreCase("http://dev.redeemar.com/")) {

            appInfo = getString(R.string.app_dev);
        }*/

        if(MyApplication.getAppEnvironment() == "beta") {
            appInfo = getString(R.string.app_beta);
        }
        else {
            appInfo = getString(R.string.app_dev);
        }




        Log.d("Dibs", "B: "+appInfo);
        tvSubTitle.setText(appInfo);

        tvTitle.setText(getString(R.string.app_name)+" "+appVersion);



        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;


        try {

            appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.d("Dibs", "A: "+appVersion);

        } catch (Exception ex) {

        }


        if (context instanceof Activity){
            a=(Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
