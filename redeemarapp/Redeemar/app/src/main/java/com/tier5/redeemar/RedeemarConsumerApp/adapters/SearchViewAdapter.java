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


public class SearchViewAdapter extends RecyclerSwipeAdapter<SearchViewAdapter.SimpleViewHolder> implements ImageDownloadedListener {

    private static final String LOGTAG = "SwipeRecyclerView";
    private Context mContext;
    private ArrayList<Offer> offerList;
    //private ImageLoader mImageLoader;
    private Bitmap logoBmp;
    private SharedPreferences sharedpref;
    private String activityName;
    private String mViewType;
    private Resources res;
    private String offerId, userId, offerImageFileName, LogoFileName;
    Typeface myFont;


    public SearchViewAdapter(Context context) {
        this.mContext = context;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
    }

    public SearchViewAdapter(Context context, String actName) {
        this.mContext = context;
        this.activityName = actName;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
    }


    public SearchViewAdapter(Context context, ArrayList<Offer> objects, String actName) {
        this.mContext = context;
        this.offerList = objects;
        this.activityName = actName;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

    }

    public void setOffers(ArrayList<Offer> listOffers) {
        this.offerList = listOffers;

        Log.d(LOGTAG, "Offers count: "+listOffers.size());
        //update the adapter to reflect the new set of Offers
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2 * 2;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(LOGTAG, "View Type: "+viewType);
        mViewType = "list";

        if(sharedpref.getString(res.getString(R.string.spf_view_type), null) != null) {
            mViewType = sharedpref.getString(res.getString(R.string.spf_view_type), "");
        }

        Log.d(LOGTAG, "View type: "+mViewType);

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);

        myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {



        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    @Override
    public void onImageDownloaded(Bitmap imBmp) {
        String logoImagePath = Utils.saveToInternalStorage(imBmp, LogoFileName);
        this.logoBmp = imBmp;
        //Utils.setImageView(logoImagePath, viewHolder.logoThumbnail);
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        //private TextView tvBankOffer, tvPassOffer, tvOfferDescription, tvPriceRangeId, tvDiscount, tvExpires;
        private TextView tvBankOffer, tvPassOffer;

        public SimpleViewHolder(View itemView) {
            super(itemView);


        }
    }


}
