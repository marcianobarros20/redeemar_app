package com.tier5.redeemar.RedeemarConsumerApp.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.squareup.picasso.Picasso;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadImageTask;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Brand;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

public class BrandViewHolder extends ParentViewHolder {

    private static final String LOGTAG = "BrandViewHolder";
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private TextView name;
    private ImageView thumbnailBrand;
    private TextView tvActive, tvNumOffers, tvNumOnDemand, tvDistance, tvBrandInfo;


    private Typeface myFont;


    public BrandViewHolder(View view) {
        super(view);

        myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));

        thumbnailBrand = (ImageView) itemView.findViewById(R.id.thumbnailBrand);
        tvNumOffers = (TextView) itemView.findViewById(R.id.numOffers);
        tvActive = (TextView) itemView.findViewById(R.id.numActive);
        tvNumOnDemand = (TextView) itemView.findViewById(R.id.numOnDemand);
        tvDistance = (TextView) itemView.findViewById(R.id.distance);
        tvBrandInfo = (TextView) itemView.findViewById(R.id.brandInfo);
    }

    public void bind(Brand brand, Context context) {

        if(brand.getBrandLogo() != null && !brand.getBrandLogo().equalsIgnoreCase("")) {

            String brandUrl = UrlEndpoints.basePathURL + brand.getBrandLogo();
            Log.d(LOGTAG, "Brand Logo: "+brandUrl);

            Picasso.with(context)
                    .load(brandUrl)
                    .fit()
                    .into(thumbnailBrand);


        }

        tvBrandInfo.setText(String.valueOf(brand.getCompanyName()));
        tvNumOffers.setText(String.valueOf(brand.getCountOffers()));
        tvNumOnDemand.setText(String.valueOf(brand.getCountOnDemand()));
        tvActive.setText(String.valueOf(brand.getCountBankedOffers()));
        tvDistance.setText(brand.getDistanceVal());

    }
}
