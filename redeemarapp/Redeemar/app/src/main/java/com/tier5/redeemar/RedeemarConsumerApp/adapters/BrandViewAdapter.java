package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.TaskCompleted;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.User;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;

import java.io.File;
import java.util.ArrayList;


public class BrandViewAdapter extends RecyclerView.Adapter<BrandViewAdapter.SimpleViewHolder> implements AbsListView.OnScrollListener, TaskCompleted {

    private static final String LOGTAG = "BrandViewAdapter";
    private Context mContext;
    private ArrayList<User> userList;
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

        //viewHolder.mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        String imageUrl = "", brandImageUrl = "", brandImagePath = "";


        try {

            if(item.getLogoName() != null && item.getLogoName() != "") {
                imageUrl = item.getLogoName();


                //Uri uri = Uri.fromFile(new File(item.getLogoName()));
                //Picasso.with(activity).load(uri).resize(96, 96).centerCrop().into(viewHolder.image);




                Picasso.with(mContext)
                        .load(imageUrl)
                        .fit()
                        .into(viewHolder.thumbnail);
            }




            if(item.getStoreBrandPic() != null && item.getStoreBrandPic() != "") {

                //brandImageUrl = item.getStoreBrandPic();
                brandImagePath = Constants.brandImgDir+item.getStoreBrandPic();


                //Uri uri = Uri.fromFile(new File(brandImageUrl));
                //Picasso.with(mContext).load(uri).centerCrop().into(viewHolder.thumbnailBrandPic);

                Picasso.with(mContext)
                        .load(brandImageUrl)
                        .fit()
                        .into(viewHolder.thumbnailBrandPic);
            }




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


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {



    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int totalCount) {
        //firstvisible is your first visible item in the list
        Log.d(LOGTAG, "First Visible "+firstVisible);
        Log.d(LOGTAG, "Visible Count "+visibleCount);
        Log.d(LOGTAG, "Total Count "+totalCount);
    }

    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvBrandName, tvAddress, tvNumOffers;
        private ImageView thumbnail;
        private ImageView thumbnailBrandPic;
        private ImageLoader mImageLoader;



        LinearLayout rating;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            thumbnailBrandPic = (ImageView) itemView.findViewById(R.id.thumbnailBrandPic);

        }
    }



}
