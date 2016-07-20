package com.tier5.redeemar.RedeemarConsumerApp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.SendFeedbackAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

/**
 * Created by Tier5 on 29/07/15.
 */
public class RateUsFragment extends Fragment implements TaskCompleted {

    private static final String LOGTAG = "Login";

    private EditText txtEmail, txtFeedback;
    private Button btnSendFeedback;
    private RatingBar ratingBar;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog pd;
    private SharedPreferences sharedpref;
    private Resources res;

    private String user_id="0", email;
    public RateUsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rate_us, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        res = getResources();




        txtEmail = (EditText) rootView.findViewById(R.id.email);
        txtFeedback = (EditText) rootView.findViewById(R.id.feedback);
        ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        btnSendFeedback = (Button) rootView.findViewById(R.id.btn_send_feedback);

        sharedpref = getActivity().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
            user_id = sharedpref.getString(res.getString(R.string.spf_user_id), "0");
        }


        if(sharedpref.getString(res.getString(R.string.spf_email), null) != null) {
            email = sharedpref.getString(res.getString(R.string.spf_email), "");
            txtEmail.setText(email);
        }






        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Email is: "+txtEmail.getText());
                Log.d(LOGTAG, "Feedback is: "+txtFeedback.getText());

                if(!txtEmail.getText().toString().equals("") && android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {



                    if(!txtFeedback.getText().toString().equals("") || ratingBar.getRating() > 0) {

                        callme();

                        pd = new ProgressDialog(getActivity());
                        pd.setMessage(getString(R.string.sending_data));
                        pd.show();


                    }
                    else {


                        builder = new AlertDialog.Builder(getActivity());//Context parameter
                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do stuff
                            }
                        });
                        builder.setMessage(getString(R.string.enter_rating_feedback));
                        alertDialog = builder.create();

                        alertDialog.setTitle(getString(R.string.alert_title));
                        alertDialog.setIcon(R.drawable.icon_cross);
                        alertDialog.show();

                    }


                }

                else {

                    builder = new AlertDialog.Builder(getActivity());//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(R.string.enter_valid_email));
                    alertDialog = builder.create();

                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);
                    alertDialog.show();


                }




            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onTaskComplete(String result) {

        Log.d(LOGTAG, "Hello: "+result);

        if(result.equalsIgnoreCase("R01001")) {

            pd.dismiss();

            builder = new AlertDialog.Builder(getActivity());//Context parameter
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Open BrowseOffer

                    getActivity().setTitle(R.string.browse_offers);

                    Fragment fr = new BrowseOfferFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fr);
                    fragmentTransaction.commit();

                }
            });
            builder.setMessage(getString(R.string.msg_feedback_sent));
            alertDialog = builder.create();

            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_tick);
            alertDialog.show();


        }
        else {

            builder = new AlertDialog.Builder(getActivity());//Context parameter
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do stuff
                }
            });
            builder.setMessage(getString(R.string.msg_feedback_not_sent));
            alertDialog = builder.create();

            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_cross);
            alertDialog.show();
        }

    }

    public void callme() {
        // txtEmail.getText().toString(), user_id, txtFeedback.getText().toString(), String.valueOf(ratingBar.getRating())

        new SendFeedbackAsyncTask(this).execute(txtEmail.getText().toString(), user_id, txtFeedback.getText().toString(), String.valueOf(ratingBar.getRating()));
    }
}
