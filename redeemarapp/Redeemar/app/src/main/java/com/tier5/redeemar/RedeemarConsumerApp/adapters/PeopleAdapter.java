package com.tier5.redeemar.RedeemarConsumerApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daimajia.swipe.SwipeLayout;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.tier5.redeemar.RedeemarConsumerApp.CustomVolleyRequestQueue;
import com.tier5.redeemar.RedeemarConsumerApp.R;
import com.tier5.redeemar.RedeemarConsumerApp.ValidateOfferActivity;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadImageTask;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends ExpandableRecyclerAdapter<PeopleAdapter.PeopleListItem> {

    private static final String LOGTAG = "PeopleAdapter";
    public static final int TYPE_PERSON = 1001;
    private ArrayList<Offer> offerList;
    Typeface myFont;

    public PeopleAdapter(Context context) {
        super(context);
        Log.d(LOGTAG, "Testing Me");

        Log.d(LOGTAG, "After Testing Me");
    }

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
        public String companyName, address, brandLogo, brandInfo, distanceVal;
        public int countOffers, countOnDemand, countBankedOffers, onDemand, expires;
        public String offerId, offerDesc, locationVal, imageUrl;
        public double payVal, retailVal, discountVal;

        public PeopleListItem(String companyName, String brandInfo, String brandLogo, int countOffers, int countOnDemand, int countBankedOffers, String distanceVal) {
            super(TYPE_HEADER);

            Log.d(LOGTAG, "Type Header 1: "+TYPE_HEADER);

            this.companyName = companyName;
            this.brandInfo = brandInfo;
            this.brandLogo = brandLogo;
            this.countOnDemand = countOnDemand;
            Log.d(LOGTAG, "Count On Demand: "+countOnDemand);
            this.countOffers = countOffers;
            this.countBankedOffers = countBankedOffers;
            this.distanceVal = distanceVal;

        }


        public PeopleListItem(String offerId, String offerDesc, double payVal, double retailVal, double discountVal, String locationVal, String imageUrl, int onDemand, int expires) {
            super(TYPE_PERSON);

            Log.d(LOGTAG, "Type Header 2: "+TYPE_PERSON);

            this.offerId = offerId;
            this.offerDesc = offerDesc;
            this.payVal = payVal;
            this.retailVal = retailVal;
            this.discountVal = discountVal;
            this.locationVal = locationVal;
            this.imageUrl = imageUrl;
            this.onDemand = onDemand;
            this.expires = expires;

        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;
        private ImageView thumbnailBrand;
        private TextView tvActive, tvNumOffers, tvNumOnDemand, tvDistance, tvBrandInfo;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.thumbnailBrand));

            thumbnailBrand = (ImageView) itemView.findViewById(R.id.thumbnailBrand);
            tvNumOffers = (TextView) itemView.findViewById(R.id.numOffers);
            tvActive = (TextView) itemView.findViewById(R.id.numActive);
            tvNumOnDemand = (TextView) itemView.findViewById(R.id.numOnDemand);
            tvDistance = (TextView) itemView.findViewById(R.id.distance);
            tvBrandInfo = (TextView) itemView.findViewById(R.id.brandInfo);
        }

        public void bind(int position) {
            super.bind(position);

            String fullAddr = "";

            if(visibleItems.get(position).brandLogo != null && !visibleItems.get(position).brandLogo.equalsIgnoreCase("")) {

                String brandUrl = UrlEndpoints.basePathURL + visibleItems.get(position).brandLogo;
                Log.d(LOGTAG, "Brand Logo: "+brandUrl);

                try {

                    new DownloadImageTask(thumbnailBrand).execute(brandUrl);
                    thumbnailBrand.setAdjustViewBounds(false);


                } catch(Exception ex) {

                    Log.d(LOGTAG, "Exception: "+ex.toString());

                }

            }

            tvNumOffers.setText(String.valueOf(visibleItems.get(position).countOffers));
            tvNumOnDemand.setText(String.valueOf(visibleItems.get(position).countOnDemand));
            tvActive.setText(String.valueOf(visibleItems.get(position).countBankedOffers));
            tvDistance.setText(visibleItems.get(position).distanceVal);


            /*fullAddr = visibleItems.get(position).address;
            if(!fullAddr.equalsIgnoreCase(""))
                fullAddr = fullAddr + " ";

            fullAddr = visibleItems.get(position).zipcode;

            tvBrandInfo.setText(fullAddr);*/

        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvValidateOffer, tvOfferDescription, tvRetailValue, tvDiscount, tvPayValue, tvDistance, tvOnDemand, tvExpires;
        private NetworkImageView tvThumbnail;
        private ImageView mapIcon;
        private ImageLoader mImageLoader;
        private LinearLayout distanceLayout, discountLayout;
        String offer_id = "";

        public PersonViewHolder(View viewHolder) {
            super(viewHolder);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvValidateOffer = (TextView) itemView.findViewById(R.id.validate_offer);
            tvOfferDescription = (TextView) itemView.findViewById(R.id.offer_description);
            tvRetailValue = (TextView) itemView.findViewById(R.id.retail_value);
            tvDiscount = (TextView) itemView.findViewById(R.id.discount);
            tvPayValue = (TextView) itemView.findViewById(R.id.pay_value);
            //tvDistance = (TextView) itemView.findViewById(R.id.distance);
            //mapIcon = (ImageView) itemView.findViewById(R.id.map_icon);
            tvOnDemand = (TextView) itemView.findViewById(R.id.on_demand);
            tvThumbnail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            tvExpires = (TextView) itemView.findViewById(R.id.expires);

            //distanceLayout = (LinearLayout) itemView.findViewById(R.id.distance_layout);
            discountLayout = (LinearLayout) itemView.findViewById(R.id.discount_layout);
        }

        public void bind(int position) {

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



            offer_id = visibleItems.get(position).offerId;
            String offer_desc = visibleItems.get(position).offerDesc;




            if(offer_desc.length() > 75)
                tvOfferDescription.setText(offer_desc.substring(0, 75)+"...");

            tvOfferDescription.setText(offer_desc);
            tvRetailValue.setText(cur_sym+String.valueOf(visibleItems.get(position).retailVal)+" Value");
            tvPayValue.setText(cur_sym+(String.valueOf(visibleItems.get(position).payVal)));
            //tvOnDemand.setText(visibleItems.get(position).countOnDemand);



            /*if(item.getOnDemand() == 1) {
                tVOnDemand.setVisibility(View.VISIBLE);
            }
            else {
                tVOnDemand.setVisibility(View.GONE);
            }*/

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);




            tvValidateOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(view.getContext(), ValidateOfferActivity.class);
                    Log.d(LOGTAG, "My Offer Id: "+offer_id);
                    intent.putExtra(view.getContext().getString(R.string.ext_offer_id), offer_id);
                    view.getContext().startActivity(intent);


                }
            });



            if(visibleItems.get(position).expires > 0) {
                DecimalFormat format = new DecimalFormat("#");
                format.setDecimalSeparatorAlwaysShown(false);
                esb.append(expires_in).append(" ").append(format.format(visibleItems.get(position).expires)).append(" ").append(days);
            }

            tvOfferDescription.setTypeface(myFont);
            tvRetailValue.setTypeface(myFont);
            tvPayValue.setTypeface(myFont);
            //tvDistance.setTypeface(myFont);
            tvDiscount.setTypeface(myFont);
            tvExpires.setTypeface(myFont);
            //tvRetailValue.setPaintFlags(tvRetailValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int valCalc = 1;
            Double discVal = 2.0;
            String imageUrlVal = visibleItems.get(position).imageUrl;

            if(discVal > 0) {
                discountLayout.setVisibility(View.VISIBLE);
            }
            else {
                discountLayout.setVisibility(View.INVISIBLE);
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

            tvExpires.setText(esb);


            if(discVal > 0) {
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
                mImageLoader.get(imageUrlVal, ImageLoader.getImageListener(tvThumbnail,
                        R.drawable.icon_watermark, android.R.drawable.ic_dialog_alert));
                tvThumbnail.setImageUrl(imageUrlVal, mImageLoader);
            }

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOGTAG, "PeopleAdapter View Type: "+viewType);
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.item_header, parent));
            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.my_swipe_row_item, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder viewHolder, int position) {

        Log.d(LOGTAG, "Item View Type: "+getItemViewType(position));
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) viewHolder).bind(position);
                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) viewHolder).bind(position);
                break;
        }
    }



    public void setOffers(ArrayList<Offer> listOffers) {

        Log.d(LOGTAG, "Offer lists now: "+listOffers.size());
        this.offerList = listOffers;
        setItems(getSampleItems());

    }



    private List<PeopleListItem> getSampleItems() {
        List<PeopleListItem> items = new ArrayList<>();

        Log.d(LOGTAG, "Trying to get data");

        if(offerList != null) {


            Log.d(LOGTAG, "Offer lists now: "+offerList.size());
            int tempRedeemarId = 0;
            int countBanked = 1;
            int c = 0;

            // This ArrayList is required to get all counters of banked offers
            ArrayList<Integer> listBanked = new ArrayList();

            for(int p=0; p < offerList.size(); p++) {
                final Offer item = offerList.get(p);

                int mRedeemarId = item.getCreatedBy();

                if(mRedeemarId != tempRedeemarId && tempRedeemarId != 0) {
                    listBanked.add(countBanked);
                    countBanked = 0;
                }

                tempRedeemarId = mRedeemarId;

                countBanked++;
            }



            Log.d(LOGTAG, "List Banked: "+listBanked.size());

            // Reset back to 0
            tempRedeemarId = 0;
            int cntBanked = 0;

            for(int p=0; p < offerList.size(); p++) {
                final Offer item = offerList.get(p);

                int redeemarId = item.getCreatedBy();



                Log.d(LOGTAG, "My Offer Id: "+item.getOfferId());
                Log.d(LOGTAG, "My Redeemar Id: "+item.getCreatedBy());
                Log.d(LOGTAG, "My Company Name: "+item.getCompanyName());
                Log.d(LOGTAG, "My Company Address: "+item.getAddress());
                Log.d(LOGTAG, "My Company Zipcode: "+item.getZipcode());
                Log.d(LOGTAG, "My Brand Logo: "+item.getBrandLogo());
                Log.d(LOGTAG, "My Offers Count: "+item.getOffersCount());
                Log.d(LOGTAG, "My Price Range Id: "+item.getPriceRangeId());
                Log.d(LOGTAG, "My Distance: "+item.getDistance());
                Log.d(LOGTAG, "My Description: "+item.getOfferDescription());
                Log.d(LOGTAG, "My Pay Value: "+item.getPayValue());
                Log.d(LOGTAG, "My Retail Value: "+item.getRetailvalue());
                Log.d(LOGTAG, "My Discount: "+item.getDiscount());
                Log.d(LOGTAG, "My Location: "+item.getLocation());




                if(tempRedeemarId != redeemarId) {
                    // Now get the banked counter from the array list

                    /*if(listBanked.size() > 0) {
                        cntBanked = listBanked.get(0);
                        Log.d(LOGTAG, "My Count My Offers: "+cntBanked);
                        c++;
                    }*/

                    items.add(new PeopleListItem(item.getCompanyName(), "", item.getBrandLogo(), item.getOffersCount(), item.getDealsCount(), cntBanked, item.getDistance()));
                }


                items.add(new PeopleListItem(item.getOfferId(), item.getOfferDescription(), item.getPayValue(), item.getRetailvalue(), item.getDiscount(), item.getLocation(),
                       item.getImageUrl(), item.getOnDemand(), item.getExpiredInDays()));


                tempRedeemarId = redeemarId;



            }


        }






        return items;
    }
}
