package com.tier5.redeemar.RedeemarConsumerApp.ViewHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.squareup.picasso.Picasso;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Brand;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

public class BrandViewHolder extends ParentViewHolder {

    private static final String LOGTAG = "BrandViewHolder";

    private ImageView thumbnailBrand;
    private TextView tvActive, tvNumOffers, tvNumOnDemand, tvDistance, tvBrandInfo;


    //private Typeface myFont;


    public BrandViewHolder(View view) {
        super(view);

        //myFont = Typeface.createFromAsset(view.getResources().getAssets(),  view.getResources().getString(R.string.default_font));

        thumbnailBrand = (ImageView) itemView.findViewById(R.id.thumbnailBrand);
        tvNumOffers = (TextView) itemView.findViewById(R.id.numOffers);
        tvActive = (TextView) itemView.findViewById(R.id.numActive);
        tvNumOnDemand = (TextView) itemView.findViewById(R.id.numOnDemand);
        tvDistance = (TextView) itemView.findViewById(R.id.distance);
        tvBrandInfo = (TextView) itemView.findViewById(R.id.brandInfo);
    }

    public void bind(Brand brand, Context context) {

        if(brand.getBrandLogo() != null && !brand.getBrandLogo().equalsIgnoreCase("")) {

            String brandUrl = UrlEndpoints.baseLogoMediumURL + brand.getBrandLogo();
            Log.d(LOGTAG, "Brand Logo: "+brandUrl);

            int width = 120;
            int height = 75;
            int maxWidth = 153;

            double newWidth = 0;
            double newHeight = 0;

            int nw = 110;
            int nh = 110;

            Log.d(LOGTAG, "Modified Image dimension: "+newWidth+" x "+newHeight);



            /*try {

                final Bitmap image = Picasso.with(context).load(brandUrl).get();
                width = image.getWidth();
                height = image.getHeight();
                double ratio = 1.6;

                if(height > 0)
                    ratio = width/height;


                if(width > maxWidth)
                    newWidth = maxWidth;
                else
                    newWidth = width;

                newHeight = newWidth / ratio;

                Double d1 = new Double(newWidth);
                nw = d1.intValue();

                Double d2 = new Double(newHeight);
                nh = d2.intValue();


                Log.d(LOGTAG, "Original Image dimension: "+width+" x "+height);
                Log.d(LOGTAG, "Modified Image dimension: "+newWidth+" x "+newHeight);



            } catch(Exception ex) {
                Log.d(LOGTAG, "Exception occured "+ex.toString());
            }*/

            Picasso.with(context)
                    .load(brandUrl)
                    .fit() // resizes the image to these dimensions (in pixel)
                    .into(thumbnailBrand);


        }

        tvBrandInfo.setText(String.valueOf(brand.getCompanyName()));
        tvNumOffers.setText(String.valueOf(brand.getCountOffers()));
        tvNumOnDemand.setText(String.valueOf(brand.getCountOnDemand()));
        tvActive.setText(String.valueOf(brand.getCountBankedOffers()));
        tvDistance.setText(brand.getDistanceVal());

    }
}
