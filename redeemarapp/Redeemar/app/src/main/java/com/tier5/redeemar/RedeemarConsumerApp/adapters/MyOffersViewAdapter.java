package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.ValidateOfferActivity;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

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
import java.text.DecimalFormat;
import java.util.ArrayList;


public class MyOffersViewAdapter extends RecyclerSwipeAdapter<MyOffersViewAdapter.SimpleViewHolder> {

    private static final String LOGTAG = "SwipeRecyclerView";
    private Context mContext;
    private ArrayList<Offer> offerList;
    //private ImageLoader mImageLoader;;
    private SharedPreferences sharedpref;
    private String activityName;
    private Resources res;
    private String offerId, userId;
    Typeface myFont;



    public MyOffersViewAdapter(Context context, String actName) {
        this.mContext = context;
        this.activityName = actName;

        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

    }

    public MyOffersViewAdapter(Context context, ArrayList<Offer> objects, String actName) {
        this.mContext = context;
        this.offerList = objects;
        this.activityName = actName;

        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

    }



    public void setOffers(ArrayList<Offer> listOffers) {
        this.offerList = listOffers;
        //update the adapter to reflect the new set of Offers
        notifyDataSetChanged();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_swipe_row_item, parent, false);

        myFont = Typeface.createFromAsset(view.getResources().getAssets(), view.getResources().getString(R.string.default_font));

        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {


        String discDesc = "";
        StringBuilder sb = new StringBuilder(14);
        StringBuilder esb = new StringBuilder(14);
        String address_distance = "";

        String perc_sym = mContext.getResources().getString(R.string.percentage_symbol);
        String off = mContext.getResources().getString(R.string.off);
        String disc = mContext.getResources().getString(R.string.discount);
        String cur_sym = mContext.getResources().getString(R.string.currency_symbol);
        String save = mContext.getResources().getString(R.string.save);
        String expires_in = mContext.getResources().getString(R.string.expires_in);
        String days = mContext.getResources().getString(R.string.days);

        String distance_unit = mContext.getResources().getString(R.string.distance_unit);


        final Offer item = offerList.get(position);

        String offer_desc = item.getOfferDescription();

        if(item.getLocation() != null && !item.getLocation().equalsIgnoreCase("null")) {
            address_distance = item.getLocation() + " ";
            Log.d(LOGTAG, "Browse Location: "+address_distance);
        }
        else {
            address_distance = item.getAddress() + " ";
        }

        if(!item.getDistance().equalsIgnoreCase("")) {
            Log.d(LOGTAG, "Browse Distance: "+item.getDistance());
            address_distance = address_distance + item.getDistance() + " miles";
        }



        if(offer_desc.length() > 50)
            viewHolder.tvOfferDescription.setText(offer_desc.substring(0, 50)+"...");
        else
            viewHolder.tvOfferDescription.setText(offer_desc);


        if(item.getRetailvalue() > 0)
            viewHolder.tvRetailValue.setText(cur_sym+String.valueOf(item.getRetailvalue()));

        if(item.getPayValue() > 0)
            viewHolder.tvPayValue.setText(cur_sym+(String.valueOf(item.getPayValue())));



        if(address_distance.equalsIgnoreCase("")) {
            viewHolder.distanceLayout.setVisibility(View.INVISIBLE);
        }
        else {

            viewHolder.tvDistance.setText(address_distance);
            viewHolder.distanceLayout.setVisibility(View.VISIBLE);
        }

        if(item.getOnDemand() == 1) {
            viewHolder.tVOnDemand.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.tVOnDemand.setVisibility(View.GONE);
        }

        viewHolder.tvOfferDescription.setTypeface(myFont);
        viewHolder.tvRetailValue.setTypeface(myFont);
        viewHolder.tvPayValue.setTypeface(myFont);
        viewHolder.tvDistance.setTypeface(myFont);
        viewHolder.tvDiscount.setTypeface(myFont);

        viewHolder.tvRetailValue.setPaintFlags(viewHolder.tvRetailValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int valCalc = item.getValueCalculate();
        Double discVal = item.getDiscount();
        String imageUrl = item.getImageUrl();

        if(discVal > 0) {
            viewHolder.discountLayout.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.discountLayout.setVisibility(View.INVISIBLE);
        }


        switch(valCalc)
        {
            case 1 :
                sb.append(cur_sym).append(discVal).append(" ").append(off);
                break;
            case 2 :
                sb.append(discVal).append(perc_sym).append(" ").append(off);
                break;
            case 3 :
                sb.append(cur_sym).append(discVal).append(" ").append(disc);
                break;
            case 4 :
                sb.append(discVal).append(perc_sym).append(" ").append(disc);
                break;
            case 5 :
                sb.append(save).append(" ").append(cur_sym).append(discVal);
                break;
            case 6 :
                sb.append(save).append(" ").append(discVal).append(perc_sym);
                break;
            default :
                sb.append(cur_sym).append(discVal).append(" ").append(off);
        }


        if(discVal > 0) {
            viewHolder.tvDiscount.setText(sb);
            viewHolder.tvDiscount.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.tvDiscount.setVisibility(View.GONE);
        }

        viewHolder.mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        // Instantiate the RequestQueue.

        if(imageUrl != "") {
            imageUrl = UrlEndpoints.serverBaseUrl + imageUrl;
            viewHolder.mImageLoader.get(imageUrl, ImageLoader.getImageListener(viewHolder.thumbnail,
                    R.drawable.icon_watermark, android.R.drawable
                            .ic_dialog_alert));
            viewHolder.thumbnail.setImageUrl(imageUrl, viewHolder.mImageLoader);
        }


        //viewHolder.tvName.setText((item.getName()) + "  -  Row Position " + position);
        //viewHolder.tvEmailId.setText(item.getEmailId());


        //viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // Drag From Left
        //viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        // Drag From Right
        //viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));


        // Handling different events when swiping
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.

                Log.d(LOGTAG, "Drag Distance: "+viewHolder.swipeLayout.getDragDistance());
                Log.d(LOGTAG, "Drag Edge: "+viewHolder.swipeLayout.getDragEdge());
                Log.d(LOGTAG, "Open Status: "+viewHolder.swipeLayout.getOpenStatus());

                if(viewHolder.swipeLayout.getDragEdge() == SwipeLayout.DragEdge.Right) {
                    Log.d(LOGTAG, "Inside Drag Right");
                }

                if(viewHolder.swipeLayout.getDragEdge() == SwipeLayout.DragEdge.Left) {
                    Log.d(LOGTAG, "Inside Drag Left");
                }

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.

            }
        });

        viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((((SwipeLayout) v).getOpenStatus() == SwipeLayout.Status.Close)) {
                    //Start your activity

                    Toast.makeText(mContext, "SwipeLayout Status", Toast.LENGTH_SHORT).show();
                }

            }
        });






        /*viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = item.getOfferId();
                //Toast.makeText(mContext, "Offer Id: " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ValidateOfferActivity.class);
                Log.d(LOGTAG, "This Offer Id: "+id);
                intent.putExtra(v.getContext().getString(R.string.ext_offer_id), id);
                v.getContext().startActivity(intent);

            }
        });*/



        viewHolder.tvValidateOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = item.getOfferId();
                //Toast.makeText(mContext, "Offer Id: " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), ValidateOfferActivity.class);
                Log.d(LOGTAG, "My Offer Id: "+id);
                intent.putExtra(view.getContext().getString(R.string.ext_offer_id), id);
                view.getContext().startActivity(intent);


            }
        });



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


    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvValidateOffer, tvOfferDescription, tvRetailValue, tvDiscount, tvPayValue, tvDistance, tVOnDemand;
        private NetworkImageView thumbnail;
        private ImageView mapIcon;
        private ImageLoader mImageLoader;
        private LinearLayout distanceLayout, discountLayout;


        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvValidateOffer = (TextView) itemView.findViewById(R.id.validate_offer);
            tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
            tvRetailValue = (TextView) itemView.findViewById(R.id.retail_value);
            tvDiscount = (TextView) itemView.findViewById(R.id.discount);
            tvPayValue = (TextView) itemView.findViewById(R.id.pay_value);
            tvDistance = (TextView) itemView.findViewById(R.id.distance);
            mapIcon = (ImageView) itemView.findViewById(R.id.map_icon);
            tVOnDemand = (TextView) itemView.findViewById(R.id.on_demand);
            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);


            distanceLayout = (LinearLayout) itemView.findViewById(R.id.distance_layout);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.discount_layout);

            //Image URL - This can point to any image file supported by Android

        }
    }


}
