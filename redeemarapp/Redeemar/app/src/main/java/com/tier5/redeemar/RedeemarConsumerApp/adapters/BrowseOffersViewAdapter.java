package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;
import com.tier5.redeemar.RedeemarConsumerApp.BrowseOffersActivity;
import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.OfferDetailsActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.callbacks.ImageDownloadedListener;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Keys;
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
import java.text.DecimalFormat;
import java.util.ArrayList;


public class BrowseOffersViewAdapter extends RecyclerSwipeAdapter<BrowseOffersViewAdapter.SimpleViewHolder> implements ImageDownloadedListener {

    private static final String LOGTAG = "SwipeRecyclerView";
    private Context mContext;
    private ArrayList<Offer> offerList;
    //private ImageLoader mImageLoader;
    private Bitmap logoBmp;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private String activityName;
    private String mViewType;
    private Resources res;
    private String offerId, userId, offerImageFileName, LogoFileName;
    Typeface myFont, myFontBold;
    public String more_offers = "0";


    public BrowseOffersViewAdapter(Context context, String actName, String moreOffers) {
        this.mContext = context;
        this.activityName = actName;
        this.more_offers = moreOffers;

        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();
        Log.d(LOGTAG, "SimpleViewHolder More Offers 1: "+more_offers);

    }


    public BrowseOffersViewAdapter(Context context, ArrayList<Offer> objects, String actName, String moreOffers) {
        this.mContext = context;
        this.offerList = objects;
        this.activityName = actName;
        this.more_offers = moreOffers;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        Log.d(LOGTAG, "SimpleViewHolder More Offers 2: "+more_offers);
    }



    public BrowseOffersViewAdapter(Context context, ArrayList<Offer> objects, String actName, String moreOffers, String viewType) {
        this.mContext = context;
        this.offerList = objects;
        this.activityName = actName;
        this.more_offers = moreOffers;
        this.mViewType = viewType;
        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

        Log.d(LOGTAG, "SimpleViewHolder More Offers 2: "+more_offers);



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

        mViewType = "list";

        if(sharedpref.getString(res.getString(R.string.spf_view_type), null) != null) {
            mViewType = sharedpref.getString(res.getString(R.string.spf_view_type), "");
        }

        Log.d(LOGTAG, "View type: "+mViewType);

        View view;
        if(mViewType.equalsIgnoreCase("thumb")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_swipe_row_item_thumb, parent, false);
            //editor.putString(res.getString(R.string.spf_view_type), "map"); // Storing View Type to Thumb
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_swipe_row_item, parent, false);
            //editor.putString(res.getString(R.string.spf_view_type), "thumb"); // Storing View Type to Thumb
        }

        Log.d(LOGTAG, "SimpleViewHolder More Offers: "+more_offers);



        return new SimpleViewHolder(view, more_offers);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {


        StringBuilder sb = new StringBuilder(14);
        StringBuilder esb = new StringBuilder(14);
        String address_distance = "";
        String imageUrl = "";
        String logoUrl = "";

        String perc_sym = mContext.getResources().getString(R.string.percentage_symbol);
        String off = mContext.getResources().getString(R.string.off);
        String disc = mContext.getResources().getString(R.string.discount);
        String cur_sym = mContext.getResources().getString(R.string.currency_symbol);
        String save = mContext.getResources().getString(R.string.save);
        String expires_in = mContext.getResources().getString(R.string.expires_in);
        String days = mContext.getResources().getString(R.string.days);
        String distance_unit = mContext.getResources().getString(R.string.distance_unit);
        String discount_text = "";

        double dcRetail = 0, dcPay = 0;


        final Offer item = offerList.get(position);

        String offer_desc = item.getOfferDescription();

        /*if(item.getLocation() != null && !item.getLocation().equalsIgnoreCase("") && !item.getLocation().equalsIgnoreCase("null")) {
            address_distance = item.getLocation() + " ";
            Log.d(LOGTAG, "Browse Location: "+address_distance);
        }
        else {
            address_distance = item.getAddress() + " ";
        }

        if(address_distance.equalsIgnoreCase("")) {
            Log.d(LOGTAG, "Browse Distance: "+item.getDistance());
            address_distance = item.getZipcode() + " ";
        }*/

        if(!item.getLocation().equals("")) {
            //Log.d(LOGTAG, "Browse Location: "+item.getLocation());
            address_distance = item.getLocation();
        }

        if(!item.getDistance().equalsIgnoreCase("")) {
            //Log.d(LOGTAG, "Browse Distance: "+item.getDistance());
            if(address_distance.equalsIgnoreCase("")) {
                address_distance = address_distance + item.getDistance() + " miles";
            }
            else {
                address_distance = address_distance + " ("+ item.getDistance() + " miles)";
            }
        }

        dcRetail = item.getRetailvalue();
        dcPay = item.getPayValue();

        //DecimalFormat decimalFormat=new DecimalFormat("#.#");
        //System.out.println(decimalFormat.format(2.0)); //prints 2

        //dcRetail = Double.parseDouble(decimalFormat.format(dcRetail));
        //dcPay = Double.parseDouble(decimalFormat.format(dcPay));
        Log.d(LOGTAG, "XX Retail value: " + dcRetail);
        Log.d(LOGTAG, "XX Pay value: " + dcPay);


        //viewHolder.tvPayValue.getMeasuredWidth();

        Log.d(LOGTAG, "Offer Desc: " + viewHolder.tvPayValue.getMeasuredWidth());



        boolean pvFlag = false;
        boolean rvFlag = false;

        float pvSize = 0;
        float rvSize = 0;

        int pvOffset = 0;
        int rvOffset = 0;

        int cnt1 = 0, cnt2 = 0;


        int pvLen = String.valueOf(dcPay).length();
        int rvLen = String.valueOf(dcRetail).length();



        int maxLimit = 5;

        Log.d(LOGTAG, "Offer Desc: " + offer_desc);
        //Log.d(LOGTAG, "Pay Value Length: " + pvLen);
        //Log.d(LOGTAG, "Retails Value Length: " + rvLen);
        DisplayMetrics metrics;

        int fontOffset = 3;

        if(mViewType.equalsIgnoreCase("thumb")) {
            fontOffset = 6;
        }

        /*if(mViewType.equalsIgnoreCase("thumb")) {

            pvOffset = 8;
            rvOffset = 6;

            //pvSize = res.getDimension(R.dimen.pay_value_thumb);
            //rvSize = res.getDimension(R.dimen.retail_value_thumb);

            pvSize = viewHolder.tvPayValue.getTextSize();
            rvSize = viewHolder.tvRetailValue.getTextSize();


            //int aa = (TypedValue.COMPLEX_UNIT_SP, pvSize);

            //Log.d(LOGTAG, "Pay Value Original Size: " + TypedValue.COMPLEX_UNIT_SP,(pvSize));
            Log.d(LOGTAG, "Pay Value Original Len: " + pvLen);



            if(pvLen > maxLimit) {

                for (int a = pvLen; a > maxLimit; a--) {
                    pvSize = pvSize - 3;
                    pvFlag = true;
                    cnt1++;
                }

            }


            if(rvLen >  6) {
                for (int a = rvLen; a > maxLimit; a--) {
                    rvSize = rvSize - 3;
                    rvFlag = true;
                    //Log.d(LOGTAG, "Retail Value New Size: " + rvSize);
                }
            }


            if(rvSize >= pvSize) {
                pvFlag = true;
                rvSize = pvSize-3;
            }


        }
        else {*/

            pvOffset = 6;
            rvOffset = 4;

            //pvSize = res.getDimension(R.dimen.pay_value_list);
            //rvSize = res.getDimension(R.dimen.retail_value_list);
            //pvSize = viewHolder.tvPayValue.getTextSize();
            //rvSize = viewHolder.tvRetailValue.getTextSize();


            metrics = mContext.getResources().getDisplayMetrics();
            pvSize = viewHolder.tvPayValue.getTextSize()/metrics.density;
            rvSize = viewHolder.tvRetailValue.getTextSize()/metrics.density;

            Log.d(LOGTAG, "PPay Value Size: " + pvSize);
            Log.d(LOGTAG, "Pay Value Len: " + pvLen);
            Log.d(LOGTAG, "RRetail Value Size: " + rvSize);
            Log.d(LOGTAG, "Retail Value Len: " + rvLen);


            if(pvLen > maxLimit) {
                for (int a = pvLen; a > maxLimit; a--) {
                    pvSize = pvSize-fontOffset;
                    pvFlag = true;
                    //Log.d(LOGTAG, "Pay Value New Size: " + pvSize);
                }
            }

            if(rvLen >  maxLimit) {

                for (int a = rvLen; a > maxLimit; a--) {
                    rvSize = rvSize-fontOffset;
                    pvFlag = true;
                    //Log.d(LOGTAG, "Retail Value New Size: " + pvSize);

                }

            }



            if(rvSize > pvSize) {
                pvFlag = true;
                rvSize = pvSize;
            }

       // }




        Log.d(LOGTAG, "Flag 1: " + pvFlag);
        Log.d(LOGTAG, "Flag 2: " + rvFlag);

        Log.d(LOGTAG, "PAY Value New Size: " + pvSize);
        Log.d(LOGTAG, "RETAIL Value New Size: " + rvSize);


        if (pvFlag) {
            //viewHolder.tvPayValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, pvSize);
        }


        if (rvFlag) {
            //viewHolder.tvRetailValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, rvSize);
        }



        if(offer_desc.length() > 75)
            viewHolder.tvOfferDescription.setText(offer_desc.substring(0, 75)+"...");
        else
            viewHolder.tvOfferDescription.setText(offer_desc);

        if(item.getRetailvalue() > 0)
            viewHolder.tvRetailValue.setText(cur_sym+Utils.formatPrice(dcRetail));

        if(item.getPayValue() > 0)
            viewHolder.tvPayValue.setText(cur_sym+Utils.formatPrice(dcPay));

        if(!item.getDistance().equals(""))
            viewHolder.tvDistance.setText(String.valueOf(item.getDistance())+" "+distance_unit);


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

        /*viewHolder.tvOfferDescription.setTypeface(myFontBold);
        viewHolder.tvRetailValue.setTypeface(myFont);
        viewHolder.tvPayValue.setTypeface(myFontBold);
        viewHolder.tvDistance.setTypeface(myFont);
        viewHolder.tvDiscount.setTypeface(myFont);*/

        viewHolder.tvRetailValue.setPaintFlags(viewHolder.tvRetailValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int valCalc = item.getValueCalculate();
        int valText = item.getValueText();
        Double discVal = item.getDiscount();


        if(item.getRetailvalue() > 0 && item.getPayValue() > 0) {
            discount_text = Utils.calculateDiscount(item.getRetailvalue(), item.getPayValue(), valCalc);
        }


        if(mViewType.equalsIgnoreCase("thumb"))
            imageUrl = item.getLargeImageUrl();
        else
            imageUrl = item.getImageUrl();

        if(!discount_text.equals("")) {
            viewHolder.discountLayout.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.discountLayout.setVisibility(View.INVISIBLE);
        }




        // In case valCalc is 2 = then use percentage symbol, else use $ symbol
        if(valCalc == 2) {

            if(valText == 3) {
                sb.append(save).append(" ").append(discount_text).append(perc_sym);
            }
            else if(valText == 2) {
                sb.append(discount_text).append(perc_sym).append(" ").append(disc);
            }
            else {
                sb.append(discount_text).append(perc_sym).append(" ").append(off);
            }


        }
        else {

            if(valText == 3) {
                //sb.append(cur_sym).append(discVal).append(" ").append(off);
                sb.append(save).append(" ").append(cur_sym).append(discount_text);

            }
            else if(valText == 2) {
                sb.append(cur_sym).append(discount_text).append(" ").append(disc);
            }
            else {
                sb.append(cur_sym).append(discount_text).append(" ").append(off);
            }
        }

        if(!discount_text.equals("")) {
            viewHolder.tvDiscount.setText(sb);
            viewHolder.tvDiscount.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.tvDiscount.setVisibility(View.GONE);
        }

        // Instantiate the RequestQueue.
        if(imageUrl != null && !imageUrl.equalsIgnoreCase("")) {

            imageUrl = UrlEndpoints.serverBaseUrl + imageUrl;


            Picasso.with(mContext)
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.thumbnail);

        }

        // Instantiate the RequestQueue.
        if(item.getBrandLogo() != null && !item.getBrandLogo().equalsIgnoreCase("")) {

            logoUrl = UrlEndpoints.baseLogoSmallURL + item.getBrandLogo();

            Log.d(LOGTAG, "Logo URL: "+logoUrl);

            Picasso.with(mContext)
                    .load(logoUrl)
                    .into(viewHolder.logoThumbnail);
        }


        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // Drag From Right
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));


        // Handling different events when swiping
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                if(leftOffset <= -600) {

                    Log.d(LOGTAG, "Swipe Full: "+leftOffset);
                    //viewHolder.swipeLayout.close(false);

                    if(viewHolder.swipeLayout.getDragEdge() == SwipeLayout.DragEdge.Right) {

                        Log.d(LOGTAG, "Inside Bank");
                        offerId = String.valueOf(item.getOfferId());

                        //Resources res = viewHolder.getResources();

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(res.getString(R.string.spf_redir_action), "BANK_OFFER"); // Storing action
                        //editor.putString(res.getString(R.string.spf_last_offer_id), offerId); // Storing Last Offer Id
                        editor.commit();


                    }
                }

            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.


            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }

        });


        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = item.getOfferId();
                //Toast.makeText(mContext, "Offer Id: " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), OfferDetailsActivity.class);
                Log.d(LOGTAG, "My Offer Id: "+id);
                intent.putExtra(v.getContext().getString(R.string.ext_offer_id), id);
                v.getContext().startActivity(intent);

            }
        });



        viewHolder.tvBankOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

                SharedPreferences.Editor editor = sharedpref.edit();
                editor.putString(res.getString(R.string.spf_redir_action), "BANK_OFFER"); // Storing Email

                Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {

                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    intent.putExtra(res.getString(R.string.ext_activity), activityName); // Settings the activty name where it will be redirected to
                    view.getContext().startActivity(intent);

                }
                else {

                    String offerId = String.valueOf(item.getOfferId());
                    String userId = sharedpref.getString(res.getString(R.string.spf_user_id), null);

                    Log.d(LOGTAG, "View Adapter Offer Id: "+offerId);
                    Log.d(LOGTAG, "View Adapter User Id: "+userId);

                    viewHolder.swipeLayout.close(true,false);

                    new SaveOfferAsyncTask().execute("bank", userId, offerId);


                    if(position < offerList.size()) {
                        mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                        offerList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, offerList.size());
                    }
                    mItemManger.closeAllItems();

                    //Toast.makeText(view.getContext(), "Offer successfully banked!", Toast.LENGTH_SHORT).show();

                }


            }
        });


        viewHolder.tvPassOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Resources res = view.getResources();
                sharedpref = view.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
                SharedPreferences.Editor editor = sharedpref.edit();

                /* Intent intent = new Intent(view.getContext(), BrandOfferActivity.class);
                Log.d(LOGTAG, "My Brand Id: "+item.getCreatedBy());
                intent.putExtra(view.getContext().getString(R.string.ext_offer_id), item.getCreatedBy());
                view.getContext().startActivity(intent);


                editor.putString(res.getString(R.string.spf_redir_action), "BrandOffers"); // Storing Last Activity
                editor.putString(res.getString(R.string.spf_popup_action), "1"); // Storing Last Activity
                editor.putString(res.getString(R.string.spf_redeemer_id), redeemarId); // Storing Redeemar Id
                editor.commit(); // commit changes*/


                /*Bundle args = new Bundle();
                args.putString(res.getString(R.string.ext_redir_to), "BrandOffers");
                args.putString(getString(R.string.ext_redeemar_id), item.getCreatedBy());
                Fragment fr = new BrowseOfferFragment();
                fr.setArguments(args);
                FragmentManager fm = res.getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fr);
                fragmentTransaction.commit();*/



                Log.d(LOGTAG, "My Brand Id: "+item.getCreatedBy());

                openFragment(String.valueOf(item.getCreatedBy()), item.getCompanyName());


            }
        });


        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        if(offerList != null)
            return offerList.size();
        else
            return 0;

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
        private SwipeLayout swipeLayout;
        //private TextView tvBankOffer, tvPassOffer, tvOfferDescription, tvPriceRangeId, tvDiscount, tvExpires;
        private TextView tvBankOffer, tvPassOffer, tvOfferDescription, tvRetailValue, tvDiscount, tvPayValue, tvDistance, tVOnDemand;
        private ImageView thumbnail, logoThumbnail;
        private ImageView mapIcon;
        private ImageLoader mImageLoader, mLogoImageLoader;
        private LinearLayout distanceLayout, discountLayout, passLayout, bottomWrapper;


        private SharedPreferences sharedpref;
        private Resources res;

        private Context mContext;

        //private String more_offers = "0";

        LinearLayout rating;

        public SimpleViewHolder(View itemView, String showMore) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvBankOffer = (TextView) itemView.findViewById(R.id.bank_offer);
            tvPassOffer = (TextView) itemView.findViewById(R.id.pass_offer);
            tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
            tvRetailValue = (TextView) itemView.findViewById(R.id.retail_value);
            tvDiscount = (TextView) itemView.findViewById(R.id.discount);
            tvPayValue = (TextView) itemView.findViewById(R.id.pay_value);
            tvDistance = (TextView) itemView.findViewById(R.id.distance);
            mapIcon = (ImageView) itemView.findViewById(R.id.map_icon);
            tVOnDemand = (TextView) itemView.findViewById(R.id.on_demand);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            logoThumbnail = (ImageView) itemView.findViewById(R.id.logo_image);

            distanceLayout = (LinearLayout) itemView.findViewById(R.id.distance_layout);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.discount_layout);
            passLayout = (LinearLayout) itemView.findViewById(R.id.passLayout);


            bottomWrapper = (LinearLayout) itemView.findViewById(R.id.bottom_wrapper);

            Resources res = itemView.getResources();
            sharedpref = itemView.getContext().getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
            res = itemView.getResources();


            /*if(sharedpref.getString(res.getString(R.string.spf_more_offers), null) != null) {
                more_offers = sharedpref.getString(res.getString(R.string.spf_more_offers), "0");
            }*/


            if(Keys.moreOffers == 1) {

                View view_instance = (View) itemView.findViewById(R.id.bottom_wrapper);
                ViewGroup.LayoutParams params=bottomWrapper.getLayoutParams();
                params.width=400;
                view_instance.setLayoutParams(params);

                passLayout.setVisibility(View.GONE);
            }
            else {
                passLayout.setVisibility(View.VISIBLE);
            }


        }
    }

    // Either pass or bank offers using this service
    private class SaveOfferAsyncTask extends AsyncTask<String, Void, String> {

        String bankUrl = "", passUrl = "", action = "";

        public SaveOfferAsyncTask() {
            bankUrl = UrlEndpoints.bankOffersURL;
            passUrl = UrlEndpoints.passOffersURL;
        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";

            action = params[0];
            String user_id = params[1];
            String offer_id = params[2];

            try {
                if(action.equalsIgnoreCase("pass"))
                    myUrl = new URL(passUrl);
                else
                    myUrl = new URL(bankUrl);

                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject data = new JSONObject();

                if(action.equalsIgnoreCase("bank")) {
                    data.put("webservice_name", "bankoffer");
                }
                else if(action.equalsIgnoreCase("pass")) {
                    data.put("webservice_name", "passkoffer");
                }
                data.put("user_id", user_id);
                data.put("offer_id", offer_id);

                OutputStream os = conn.getOutputStream();

                Log.d(LOGTAG, "Request: " + data.toString());


                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write("data="+data.toString());
                bufferedWriter.flush();
                bufferedWriter.close();


                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                //Log.d(LOGTAG, "Do In background: " + response);
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String resp) {
            //do what ever you want with the response

            if (resp != null) {

                try {
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();
                    JSONObject reader = new JSONObject(resp);

                    int iRes = 0;


                    if (reader.getString("messageCode").equals("R01001")) {

                        if(action.equalsIgnoreCase("bank")) {
                            Toast.makeText(mContext, "Offer successfully banked!", Toast.LENGTH_SHORT).show();
                        }
                        else if(action.equalsIgnoreCase("pass")) {
                            Toast.makeText(mContext, "Offer successfully passed!", Toast.LENGTH_SHORT).show();
                        }

                        // TODO: Return value
                        mItemManger.closeAllItems();


                    } // End of if

                    else if (reader.getString("messageCode").equals("R01002")) {

                        // TODO: Return value

                        if(action.equalsIgnoreCase("pass")) {
                            Toast.makeText(mContext.getApplicationContext(), "You have already passed this offer", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext.getApplicationContext(), "You have already banked this offer", Toast.LENGTH_SHORT).show();
                        }

                    } // End of if



                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }



    }

    public void openFragment(String redeemarId, String brandName) {


        res = mContext.getResources();
        sharedpref = mContext.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();

        editor.putString(res.getString(R.string.spf_redir_action), "BrandOffers"); // Storing Last Activity
        editor.putString(res.getString(R.string.spf_popup_action), "1"); // Storing Last Activity
        editor.putString(res.getString(R.string.spf_redeemer_id), redeemarId); // Storing Redeemar Id
        editor.putString(res.getString(R.string.spf_brand_name), brandName); // Storing Redeemar Partner Name
        editor.putString(res.getString(R.string.spf_more_offers), "1"); // Set More Offers to 1
        editor.commit();

        Intent intent = new Intent(mContext, BrowseOffersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(res.getString(R.string.ext_redir_to), "BrandOffers");
        intent.putExtra(res.getString(R.string.ext_redeemar_id), redeemarId);
        intent.putExtra(res.getString(R.string.ext_more_offers), "1");

        Keys.moreOffers=1;

        mContext.startActivity(intent);

        //mContext.startActivity(new Intent(mContext, BrowseOffersActivity.class));
    }


}
