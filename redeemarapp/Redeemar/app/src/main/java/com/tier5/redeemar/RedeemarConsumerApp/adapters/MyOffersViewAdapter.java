package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



        final Offer item = offerList.get(position);
        viewHolder.tvOfferDescription.setText(item.getOfferDescription());
        viewHolder.tvPriceRangeId.setText(item.getPriceRangeId());

        viewHolder.tvOfferDescription.setTypeface(myFont);
        viewHolder.tvPriceRangeId.setTypeface(myFont);
        viewHolder.tvDiscount.setTypeface(myFont);
        viewHolder.tvValidateOffer.setTypeface(myFont);



        int valCalc = item.getValueCalculate();
        Double discVal = item.getDiscount();
        String imageUrl = item.getImageUrl();

        String discDesc = "";
        StringBuilder sb = new StringBuilder(14);
        StringBuilder esb = new StringBuilder(14);

        String perc_sym = mContext.getResources().getString(R.string.percentage_symbol);
        String off = mContext.getResources().getString(R.string.off);
        String disc = mContext.getResources().getString(R.string.discount);
        String cur_sym = mContext.getResources().getString(R.string.currency_symbol);
        String save = mContext.getResources().getString(R.string.save);
        String expires_in = mContext.getResources().getString(R.string.expires_in);
        String days = mContext.getResources().getString(R.string.days);


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

        viewHolder.tvDiscount.setText(sb);

        if(item.getExpiredInDays() > 0) {
            DecimalFormat format = new DecimalFormat("#");
            format.setDecimalSeparatorAlwaysShown(false);
            //Log.d(LOGTAG, "Decimal: "+format.format(item.getExpiredInDays()));

            esb.append(expires_in).append(" ").append(format.format(item.getExpiredInDays())).append(" ").append(days);

            //esb.append(expires_in).append(" ").append(Math.floor(item.getExpiredInDays())).append(" ").append(days);
            viewHolder.tvExpires.setText(esb);
        }

        //Log.d("RecycleViewer", "Image URL: "+imageUrl);

        //if(imageUrl != "")
        //    imgLoader.DisplayImage(imageUrl, viewHolder.thumbnail);


        //new DownloadImageTask(viewHolder.thumbnail).execute(imageUrl);

        viewHolder.mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        // Instantiate the RequestQueue.

        if(imageUrl != "") {
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






        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = item.getOfferId();
                //Toast.makeText(mContext, "Offer Id: " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ValidateOfferActivity.class);
                Log.d(LOGTAG, "This Offer Id: "+id);
                intent.putExtra(v.getContext().getString(R.string.ext_offer_id), id);
                v.getContext().startActivity(intent);

            }
        });



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
        private TextView tvValidateOffer, tvOfferDescription, tvPriceRangeId, tvDiscount, tvExpires;
        private NetworkImageView thumbnail;
        private ImageLoader mImageLoader;

        LinearLayout rating;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvValidateOffer = (TextView) itemView.findViewById(R.id.validate_offer);
            //tvPassOffer = (TextView) itemView.findViewById(R.id.pass_offer);
            tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
            tvPriceRangeId = (TextView) itemView.findViewById(R.id.price_range_id);
            tvDiscount = (TextView) itemView.findViewById(R.id.discount);
            tvExpires = (TextView) itemView.findViewById(R.id.expires);

            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);

            //Image URL - This can point to any image file supported by Android

        }
    }

    // Either pass or bank offers using this service
    private class SaveOfferAsyncTask extends AsyncTask<String, Void, String> {


        String bankUrl = "", passUrl = "";
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

            String action = params[0];
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
                data.put("webservice_name","bankoffer");
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


                    if (reader.getString("messageCode").equals("R01001")) {

                        // TODO: Return value
                        mItemManger.closeAllItems();


                    } // End of if



                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }

}
