package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.OfferDetailsActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ImageDownloadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Search;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.ViewHolder> {

    private static final String LOGTAG = "SearchViewAdapter";
    private Context mContext;
    private ArrayList<User> addressList;
    private Bitmap logoBmp;
    private SharedPreferences sharedpref;
    private Resources res;
    Typeface myFont;


    public SearchViewAdapter(Context context) {
        Log.d(LOGTAG, "Test 1");
        this.mContext = context;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
    }

    public SearchViewAdapter(Context context, ArrayList<User> objects) {
        Log.d(LOGTAG, "Test 1");
        this.mContext = context;
        this.addressList = objects;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
    }

    public void setAddresses(ArrayList<User> listAddress) {

        Log.d(LOGTAG, "Test 2");
        this.addressList = listAddress;
        Log.d(LOGTAG, "Address count: "+listAddress.size());
        //update the adapter to reflect the new set of Offers
        notifyDataSetChanged();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOGTAG, "Test 3");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View view = inflater.inflate(R.layout.search_row, parent, false);
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);


        myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewAdapter.ViewHolder holder, int position) {
        Log.d(LOGTAG, "Test 4");
        final User item = addressList.get(position);

        Log.d(LOGTAG, "Hello "+item.getCity());
        holder.tvTitle.setTypeface(myFont);
        holder.tvDescription.setTypeface(myFont);
        holder.tvTitle.setText(item.getCity());
        holder.tvDescription.setText(item.getState());

    }


    @Override
    public int getItemCount() {
        return addressList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvDescription = (TextView) itemView.findViewById(R.id.description);


        }
    }


}
