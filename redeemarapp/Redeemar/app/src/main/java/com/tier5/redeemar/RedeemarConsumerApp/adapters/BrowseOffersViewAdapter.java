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
import com.tier5.redeemar.RedeemarConsumerApp.LoginActivity;
import com.tier5.redeemar.RedeemarConsumerApp.OfferDetailsActivity;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
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


public class BrowseOffersViewAdapter extends RecyclerSwipeAdapter<BrowseOffersViewAdapter.SimpleViewHolder> implements TaskCompleted {

    private static final String LOGTAG = "SwipeRecyclerView";
    private Context mContext;
    private ArrayList<Offer> offerList;
    //private ImageLoader mImageLoader;;
    private SharedPreferences sharedpref;
    private String activityName;
    private String mViewType;
    private Resources res;
    private String offerId, userId;
    Typeface myFont;


    public BrowseOffersViewAdapter(Context context, String actName) {
        this.mContext = context;
        this.activityName = actName;

        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

    }


    public BrowseOffersViewAdapter(Context context, ArrayList<Offer> objects, String actName) {
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

        View view;
        if(mViewType.equalsIgnoreCase("thumb"))
            view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_swipe_row_item_thumb, parent, false);
        else
            view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_swipe_row_item, parent, false);

        //view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_swipe_row_item_thumb, parent, false);

        myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {


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

        //address_distance = item.getLocation()+" "+item.getDistance();

        if(offer_desc.length() > 50)
            viewHolder.tvOfferDescription.setText(offer_desc.substring(0, 50)+"...");
        else
            viewHolder.tvOfferDescription.setText(offer_desc);

        if(item.getRetailvalue() > 0)
            viewHolder.tvRetailValue.setText(cur_sym+String.valueOf(item.getRetailvalue()));

        if(item.getPayValue() > 0)
            viewHolder.tvPayValue.setText(cur_sym+(String.valueOf(item.getPayValue())));

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

        viewHolder.tvOfferDescription.setTypeface(myFont);
        viewHolder.tvRetailValue.setTypeface(myFont);
        viewHolder.tvPayValue.setTypeface(myFont);
        viewHolder.tvDistance.setTypeface(myFont);
        viewHolder.tvDiscount.setTypeface(myFont);

        viewHolder.tvRetailValue.setPaintFlags(viewHolder.tvRetailValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int valCalc = item.getValueCalculate();
        Double discVal = item.getDiscount();

        String imageUrl = "";

        if(mViewType.equalsIgnoreCase("thumb"))
            imageUrl = item.getLargeImageUrl();
        else
            imageUrl = item.getImageUrl();

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

        /*if(item.getExpiredInDays() > 0) {
            DecimalFormat format = new DecimalFormat("#");
            format.setDecimalSeparatorAlwaysShown(false);
            //Image URLLog.d(LOGTAG, "Decimal: "+format.format(item.getExpiredInDays()));

            esb.append(expires_in).append(" ").append(format.format(item.getExpiredInDays())).append(" ").append(days);

            //esb.append(expires_in).append(" ").append(Math.floor(item.getExpiredInDays())).append(" ").append(days);
            //viewHolder.tvPayValue.setText(esb);
        }*/


        viewHolder.mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        // Instantiate the RequestQueue.
        if(imageUrl != "") {

            imageUrl = UrlEndpoints.serverBaseUrl + imageUrl;
            viewHolder.mImageLoader.get(imageUrl, ImageLoader.getImageListener(viewHolder.thumbnail,
                    R.drawable.icon_watermark, android.R.drawable
                            .ic_dialog_alert));
            viewHolder.thumbnail.setImageUrl(imageUrl, viewHolder.mImageLoader);
            viewHolder.thumbnail.setAdjustViewBounds(false);
        }


        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // Drag From Left
        //viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

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
                //you are swiping.

                Log.d(LOGTAG, "Swipe Top: "+topOffset);


                if(leftOffset <= -600) {

                    Log.d(LOGTAG, "Swipe Full: "+leftOffset);
                    //viewHolder.swipeLayout.close(false);

                    if(viewHolder.swipeLayout.getDragEdge() == SwipeLayout.DragEdge.Right) {

                        Log.d(LOGTAG, "Inside Bank");
                        offerId = String.valueOf(item.getOfferId());

                        //Resources res = viewHolder.getResources();

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(res.getString(R.string.spf_redir_action), "BANK_OFFER"); // Storing action
                        editor.putString(res.getString(R.string.spf_last_offer_id), offerId); // Storing Last Offer Id
                        editor.commit();

                        Log.d(LOGTAG, "Last Offer Id: "+offerId);

                        Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                        Log.d(LOGTAG, "No. of Items: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                        if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {

                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(res.getString(R.string.ext_activity), activityName); // Settings the activty name where it will be redirected to
                            mContext.startActivity(intent);
                        }
                        else {

                            offerId = String.valueOf(item.getOfferId());
                            userId = sharedpref.getString(res.getString(R.string.spf_user_id), null);

                            //Log.d(LOGTAG, "View Adapter Offer Id: "+offerId);
                            //Log.d(LOGTAG, "View Adapter User Id: "+userId);


                            //new SaveOfferAsyncTask().execute("bank", userId, offerId);


                            if(position < offerList.size()) {
                                //mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                                //offerList.remove(position);
                                //notifyItemRemoved(position);
                                //notifyItemRangeChanged(position, offerList.size());
                            }
                            //mItemManger.closeAllItems();


                            //Toast.makeText(mContext, "Offer successfully banked!", Toast.LENGTH_SHORT).show();
                            //viewHolder.swipeLayout.close(true);
                            //viewHolder.swipeLayout.performClick();

                            Log.d(LOGTAG, "Drag Distance: "+viewHolder.swipeLayout.getDragDistance());


                        }
                    }


                }
                /*else if(leftOffset >= -600 && leftOffset < -250) {

                    Log.d(LOGTAG, "Swipe Leave Open: "+leftOffset);
                    //viewHolder.swipeLayout.close(false);


                }
                else {

                    Log.d(LOGTAG, "Swipe Close: "+leftOffset);
                    viewHolder.swipeLayout.close(true);

                }*/


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

                if(viewHolder.swipeLayout.getDragEdge() == SwipeLayout.DragEdge.Left) {

                    /*

                    SharedPreferences.Editor editor = sharedpref.edit();
                    editor.putString(res.getString(R.string.spf_redir_action), "PASS_OFFER"); // Storing action
                    offerId = String.valueOf(item.getOfferId());

                    Log.d(LOGTAG, "User Id: "+sharedpref.getString(res.getString(R.string.spf_user_id), null));

                    if(sharedpref.getString(res.getString(R.string.spf_user_id), null) == null) {

                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(res.getString(R.string.ext_activity), activityName); // Settings the activty name where it will be redirected to

                        mContext.startActivity(intent);


                    }
                    else {


                        userId = sharedpref.getString(res.getString(R.string.spf_user_id), null);

                        //Log.d(LOGTAG, "View Adapter Offer Id: "+offerId);
                        //Log.d(LOGTAG, "View Adapter User Id: "+userId);


                        new SaveOfferAsyncTask().execute("pass", userId, offerId);

                        if(position < offerList.size()) {
                            mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                            offerList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, offerList.size());
                        }
                        mItemManger.closeAllItems();


                        Toast.makeText(mContext, "Offer successfully passed!", Toast.LENGTH_SHORT).show();
                        viewHolder.swipeLayout.close(true);

                    }

                    */




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

        /*viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((((SwipeLayout) v).getOpenStatus() == SwipeLayout.Status.Close)) {
                    //Start your activity

                    Toast.makeText(mContext, " onClick : " + item.getName() + " \n" + item.getEmailId(), Toast.LENGTH_SHORT).show();
                }

            }
        });*/





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


                    new SaveOfferAsyncTask().execute("pass", userId, offerId);

                    if(position < offerList.size()) {

                        mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                        offerList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, offerList.size());

                    }
                    mItemManger.closeAllItems();



                    //Toast.makeText(view.getContext(), "Offer passed!", Toast.LENGTH_SHORT).show();

                }



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


    @Override
    public void onTaskComplete(String result) {

        // TODO
    }


    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        //private TextView tvBankOffer, tvPassOffer, tvOfferDescription, tvPriceRangeId, tvDiscount, tvExpires;
        private TextView tvBankOffer, tvPassOffer, tvOfferDescription, tvRetailValue, tvDiscount, tvPayValue, tvDistance, tVOnDemand;
        private NetworkImageView thumbnail;
        private ImageView mapIcon;
        private ImageLoader mImageLoader;
        private LinearLayout distanceLayout, discountLayout;



        LinearLayout rating;

        public SimpleViewHolder(View itemView) {
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
            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);

            distanceLayout = (LinearLayout) itemView.findViewById(R.id.distance_layout);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.discount_layout);

        }
    }

    // Either pass or bank offers using this service
    private class SaveOfferAsyncTask extends AsyncTask<String, Void, String> {


        String bankUrl = "", passUrl = "", action = "";
        //private ArrayList<Offer> mDataSet;

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
                //JSONObject auth=new JSONObject();
                //JSONObject parent=new JSONObject();
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

}
