package com.tier5.redeemar.RedeemarConsumerApp.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.ValidateOfferActivity;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Banked;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Utils;

import java.text.DecimalFormat;

public class BankedViewHolder extends ChildViewHolder {

    private static final String LOGTAG = "BankedViewHolder";

    private TextView mIngredientTextView;
    private SwipeLayout swipeLayout;
    private TextView tvValidateOffer, tvOfferDescription, tvRetailValue, tvDiscount, tvPayValue, tvDistance, tvOnDemand, tvExpires;
    private NetworkImageView tvThumbnail;
    private ImageView mapIcon;
    private ImageLoader mImageLoader;
    private LinearLayout distanceLayout, discountLayout;

    private Typeface myFont;

    String offer_id = "";

    public BankedViewHolder(View itemView) {
        super(itemView);
        myFont = Typeface.createFromAsset(itemView.getResources().getAssets(),  itemView.getResources().getString(R.string.default_font));
        //mIngredientTextView = (TextView) itemView.findViewById(R.id.ingredient_textview);

        swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
        tvValidateOffer = (TextView) itemView.findViewById(R.id.validate_offer);
        tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
        tvRetailValue = (TextView) itemView.findViewById(R.id.retail_value);
        tvDiscount = (TextView) itemView.findViewById(R.id.discount);
        tvPayValue = (TextView) itemView.findViewById(R.id.pay_value);
        tvOnDemand = (TextView) itemView.findViewById(R.id.on_demand);
        tvThumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
        tvExpires = (TextView) itemView.findViewById(R.id.expires);
        discountLayout = (LinearLayout) itemView.findViewById(R.id.discount_layout);

    }


    public void bind(Banked banked, Context mContext) {

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
        String discount_text = "";

        double dcRetail = 0, dcPay = 0, discVal = 0;


        offer_id = banked.getOfferId();
        String offer_desc = banked.getOfferDesc();

        if(offer_desc.length() > 75)
            tvOfferDescription.setText(offer_desc.substring(0, 75)+"...");

        tvOfferDescription.setText(offer_desc);

        if(banked.getOnDemand() == 1) {
            tvOnDemand.setVisibility(View.VISIBLE);
        }
        else {
            tvOnDemand.setVisibility(View.GONE);
        }

        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        tvValidateOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ValidateOfferActivity.class);
                Log.d(LOGTAG, "My Offer Id: "+offer_id);
                intent.putExtra(view.getContext().getString(R.string.ext_offer_id), offer_id);
                view.getContext().startActivity(intent);
                swipeLayout.close();
            }
        });


        if(banked.getExpires() > 0) {
            DecimalFormat format = new DecimalFormat("#");
            format.setDecimalSeparatorAlwaysShown(false);
            esb.append(expires_in).append(" ").append(format.format(banked.getExpires())).append(" ").append(days);
        }

        tvOfferDescription.setTypeface(myFont);
        tvRetailValue.setTypeface(myFont);
        tvPayValue.setTypeface(myFont);
        //tvDistance.setTypeface(myFont);
        tvDiscount.setTypeface(myFont);
        tvExpires.setTypeface(myFont);

        int valCalc = banked.getValCalc();
        int valText = banked.getValText();

        dcRetail = banked.getRetailVal();
        dcPay = banked.getPayVal();

        tvRetailValue.setText(cur_sym+String.valueOf(Utils.formatPrice(dcRetail))+" Value");
        tvPayValue.setText(cur_sym+(String.valueOf(Utils.formatPrice(dcPay))));

        if(dcRetail > 0 && dcPay > 0) {
            discount_text = Utils.calculateDiscount(dcRetail, dcPay, valCalc);
            Log.d(LOGTAG, "My Discount Value: "+discVal);
        }


        String imageUrlVal = banked.getImageUrl();

        if(!discount_text.equals("")) {
            discountLayout.setVisibility(View.VISIBLE);
        }
        else {
            discountLayout.setVisibility(View.INVISIBLE);
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
                sb.append(save).append(" ").append(perc_sym).append(discount_text);

            }
            else if(valText == 2) {
                sb.append(cur_sym).append(discount_text).append(" ").append(disc);
            }
            else {
                sb.append(cur_sym).append(discount_text).append(" ").append(off);
            }
        }
        tvExpires.setText(esb);


        Log.d(LOGTAG, "SB is: "+sb);

        if(!discount_text.equals("")) {
            tvDiscount.setText(sb);
            tvDiscount.setVisibility(View.VISIBLE);
        }
        else {
            tvDiscount.setVisibility(View.GONE);
        }



        mImageLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();

        // Instantiate the RequestQueue.
        if(!imageUrlVal.equalsIgnoreCase("")) {
            imageUrlVal = UrlEndpoints.serverBaseUrl + imageUrlVal;
            mImageLoader.get(imageUrlVal, ImageLoader.getImageListener(tvThumbnail, R.drawable.icon_watermark, 0));
            tvThumbnail.setImageUrl(imageUrlVal, mImageLoader);
        }

    }
}
