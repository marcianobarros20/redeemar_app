package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.swipe.SwipeLayout;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;

import java.util.ArrayList;


public class BrandViewAdapter extends RecyclerView.Adapter<BrandViewAdapter.SimpleViewHolder> implements TaskCompleted {

    private static final String LOGTAG = "BrandViewAdapter";
    private Context mContext;
    private ArrayList<User> userList;
    //private ImageLoader mImageLoader;;
    private SharedPreferences sharedpref;
    private String activityName;
    private Resources res;
    private String offerId, userId;
    Typeface myFont;


    public BrandViewAdapter(Context context, ArrayList<User> objects, String actName) {
        this.mContext = context;
        this.userList = objects;
        this.activityName = actName;

        res = context.getResources();
        sharedpref = context.getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode

    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_row_item, parent, false);


        myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final User item = userList.get(position);
        viewHolder.tvBrandName.setText(item.getCompanyName());
        viewHolder.tvAddress.setText(item.getAddress());

        viewHolder.tvBrandName.setTypeface(myFont);
        viewHolder.tvAddress.setTypeface(myFont);
        viewHolder.tvNumOffers.setTypeface(myFont);





        viewHolder.mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        String imageUrl = "";

        if(item.getLogoName() != null && item.getLogoName() != "") {

            imageUrl = item.getLogoName();
            //Log.d(LOGTAG, "Image URL "+imageUrl);


        }

        try {

            viewHolder.mImageLoader.get(imageUrl, ImageLoader.getImageListener(viewHolder.thumbnail,
                    R.drawable.icon_watermark, android.R.drawable
                            .ic_dialog_alert));
            viewHolder.thumbnail.setImageUrl(imageUrl, viewHolder.mImageLoader);

        } catch(Exception e) {
            Log.d(LOGTAG, "Exception occured while getting image list: "+e.toString());
        }


        /*viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = item.getId();
                //Toast.makeText(mContext, "Offer Id: " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), OfferDetailsActivity.class);
                Log.d(LOGTAG, "My Offer Id: "+id);
                intent.putExtra(v.getContext().getString(R.string.ext_offer_id), id);
                v.getContext().startActivity(intent);

            }
        });*/





    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



    @Override
    public void onTaskComplete(String result) {

        // TODO
    }


    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvBrandName, tvAddress, tvNumOffers;
        private NetworkImageView thumbnail;
        private ImageLoader mImageLoader;;



        LinearLayout rating;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            tvBrandName = (TextView) itemView.findViewById(R.id.brand_name);
            tvAddress = (TextView) itemView.findViewById(R.id.address);
            tvNumOffers = (TextView) itemView.findViewById(R.id.num_offers);

            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);


        }
    }



}
