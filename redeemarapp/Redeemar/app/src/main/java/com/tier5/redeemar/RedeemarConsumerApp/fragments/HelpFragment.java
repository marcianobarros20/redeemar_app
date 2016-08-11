package com.tier5.redeemar.RedeemarConsumerApp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tier5.redeemar.RedeemarConsumerApp.MyApplication;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;


/**
 * Created by Dibs on 29/07/15.
 */
public class HelpFragment extends Fragment {

    private TextView tvTitle, tvSubTitle;
    String appInfo = "";
    String appVersion = "";

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
