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
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Address;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

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
        //viewHolder.tvBrandName.setText(item.getCompanyName());

        /*Address address = new Address();

        address.setStreet(item.getAddress());
        address.setCity(item.getCity());
        address.setState(item.getState());
        address.setZip(item.getZipcode());


        String fullAddr = Utils.getFullAddress(address);*/



        //viewHolder.tvAddress.setText(fullAddr);

        //viewHolder.tvBrandName.setTypeface(myFont);
        //viewHolder.tvAddress.setTypeface(myFont);
        //viewHolder.tvNumOffers.setTypeface(myFont);


        viewHolder.mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        String imageUrl = "", brandImageUrl = "";

        if(item.getLogoName() != null && item.getLogoName() != "") {
            imageUrl = item.getLogoName();
        }

        if(item.getStoreBrandPic() != null && item.getStoreBrandPic() != "") {
            brandImageUrl = item.getStoreBrandPic();
        }

        try {

            viewHolder.mImageLoader.get(imageUrl, ImageLoader.getImageListener(viewHolder.thumbnail,
                    R.drawable.icon_watermark, android.R.drawable.ic_dialog_alert));
            viewHolder.thumbnail.setImageUrl(imageUrl, viewHolder.mImageLoader);


            viewHolder.mImageLoader.get(brandImageUrl, ImageLoader.getImageListener(viewHolder.thumbnailBrandPic,
                    R.drawable.icon_watermark, android.R.drawable.ic_dialog_alert));
            viewHolder.thumbnailBrandPic.setImageUrl(brandImageUrl, viewHolder.mImageLoader);


        } catch(Exception e) {
            Log.d(LOGTAG, "Exception occurred while getting image list: "+e.toString());
        }

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
        private NetworkImageView thumbnailBrandPic;
        private ImageLoader mImageLoader;



        LinearLayout rating;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            /*tvBrandName = (TextView) itemView.findViewById(R.id.brand_name);
            tvAddress = (TextView) itemView.findViewById(R.id.address);
            tvNumOffers = (TextView) itemView.findViewById(R.id.num_offers);*/

            thumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            thumbnailBrandPic = (NetworkImageView) itemView.findViewById(R.id.thumbnailBrandPic);

        }
    }



}
