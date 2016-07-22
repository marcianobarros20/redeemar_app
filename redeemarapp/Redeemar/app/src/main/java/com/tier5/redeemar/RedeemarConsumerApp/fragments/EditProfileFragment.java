package com.tier5.redeemar.RedeemarConsumerApp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.SendFeedbackAsyncTask;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.async.UpdateProfileAsyncTask;

/**
 * Created by Tier5 on 29/07/15.
 */
public class EditProfileFragment extends Fragment implements TaskCompleted {

    private static final String LOGTAG = "EditProfileFragment";

    private EditText txtFirstName, txtLastName, txtEmail, txtPhone;
    private Button btnUpdate;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog pd;
    private SharedPreferences sharedpref;
    private Resources res;

    private String user_id="0", email="", first_name = "", last_name = "", phone = "";
    private String FIRST_NAME_REGEX = "/^[a-z ,.'-]+$/i";


    public EditProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();



        txtFirstName = (EditText) rootView.findViewById(R.id.first_name);
        txtLastName = (EditText) rootView.findViewById(R.id.last_name);
        txtEmail = (EditText) rootView.findViewById(R.id.email);
        txtPhone = (EditText) rootView.findViewById(R.id.phone);
        btnUpdate = (Button) rootView.findViewById(R.id.btn_update);


        res = getResources();

        sharedpref = getActivity().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) != null) {
            user_id = sharedpref.getString(res.getString(R.string.spf_user_id), "0");
            Log.d(LOGTAG, "User Id "+user_id);

            if(sharedpref.getString(res.getString(R.string.spf_first_name), null) != null) {
                txtFirstName.setText(sharedpref.getString(res.getString(R.string.spf_first_name), ""));
            }

            if(sharedpref.getString(res.getString(R.string.spf_last_name), null) != null) {
                txtLastName.setText(sharedpref.getString(res.getString(R.string.spf_last_name), ""));
            }

            if(sharedpref.getString(res.getString(R.string.spf_email), null) != null) {
                txtEmail.setText(sharedpref.getString(res.getString(R.string.spf_email), ""));
            }

            if(sharedpref.getString(res.getString(R.string.spf_mobile), null) != null) {
                txtPhone.setText(sharedpref.getString(res.getString(R.string.spf_mobile), ""));
            }


        }
        else {

            Log.d(LOGTAG, "No user id found, redirecting to login");

            Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            i.putExtra(res.getString(R.string.ext_activity), "EditProfile"); // Settings the activty name where it will be redirected to
            startActivity(i);

        }



        Log.d(LOGTAG, "User id is: "+user_id);


        if(sharedpref.getString(res.getString(R.string.spf_email), null) != null) {
            email = sharedpref.getString(res.getString(R.string.spf_email), "");
            Log.d(LOGTAG, "Email is: "+email);
            txtEmail.setText(email);

        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOGTAG, "Email is: "+txtEmail.getText());
                Log.d(LOGTAG, "First Name is: "+txtFirstName.getText());
                Log.d(LOGTAG, "Last Name is: "+txtLastName.getText());

                email = txtEmail.getText().toString();
                first_name = txtFirstName.getText().toString();
                last_name = txtLastName.getText().toString();
                phone = txtPhone.getText().toString();


                boolean flag = true;
                int errMsg = 0;

                if(first_name.equals("")) {

                    flag = false;
                    errMsg = R.string.enter_valid_first_name;
                    txtFirstName.requestFocus();
                }

                else if(last_name.equals("")) {
                    flag = false;
                    errMsg = R.string.enter_valid_last_name;
                    txtLastName.requestFocus();
                }

                else if(email.equals("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    flag = false;
                    errMsg = R.string.enter_valid_email;
                    txtEmail.requestFocus();
                }

                else if(!txtPhone.getText().toString().equals("")) {

                    if(!Patterns.PHONE.matcher(phone).matches()) {
                        flag = false;
                        errMsg = R.string.enter_valid_phone;
                        txtPhone.requestFocus();
                    }

                }

                if(!flag) {

                    builder = new AlertDialog.Builder(getActivity());//Context parameter
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do stuff
                        }
                    });
                    builder.setMessage(getString(errMsg));
                    alertDialog = builder.create();

                    alertDialog.setTitle(getString(R.string.alert_title));
                    alertDialog.setIcon(R.drawable.icon_cross);
                    alertDialog.show();

                }
                else {
                    callAsyncTask();

                    pd = new ProgressDialog(getActivity());
                    pd.setMessage(getString(R.string.sending_data));
                    pd.show();

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

        Log.d(LOGTAG, "OnTaskCompleted inside EditProfile: "+result);

        if(result.equalsIgnoreCase("R01001")) {

            SharedPreferences.Editor editor = sharedpref.edit();
            //editor.putString(getString(R.string.spf_user_id), jsonObject.getString("user_id")); // Storing User Id
            editor.putString(getString(R.string.spf_email), email); // Storing Email
            editor.putString(getString(R.string.spf_first_name), first_name); // Storing First name
            editor.putString(getString(R.string.spf_last_name), last_name); // Storing Last Name
            editor.putString(getString(R.string.spf_mobile), phone); // Storing Mobile
            editor.commit(); // commit changes

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
            builder.setMessage(getString(R.string.msg_profile_updated));
            alertDialog = builder.create();

            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_tick);
            alertDialog.show();


        }
        else {

            pd.dismiss();

            builder = new AlertDialog.Builder(getActivity());//Context parameter
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do stuff
                }
            });
            builder.setMessage(getString(R.string.msg_profile_not_updated));
            alertDialog = builder.create();

            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setIcon(R.drawable.icon_cross);
            alertDialog.show();
        }

    }

    public void callAsyncTask() {

        new UpdateProfileAsyncTask(this).execute(user_id, txtFirstName.getText().toString(), txtLastName.getText().toString(), txtEmail.getText().toString(), txtPhone.getText().toString());
    }
}
